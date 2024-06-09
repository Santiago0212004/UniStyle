package com.example.unistylejc.services

import android.util.Log
import com.example.unistylejc.domain.model.Comment
import com.example.unistylejc.domain.model.Service
import com.example.unistylejc.domain.model.Worker
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.getField
import kotlinx.coroutines.tasks.await

class WorkerService {
    suspend fun createWorker(worker: Worker) {
        Firebase.firestore.collection("worker").document(worker.id).set(worker).await()
    }

    suspend fun loadWorker(uid: String): DocumentSnapshot {
        return Firebase.firestore.collection("worker").document(uid).get().await()
    }

    fun observeWorker(uid: String,callback: (DocumentSnapshot?) -> Unit) {
        Firebase.firestore.collection("worker").document(uid)
            .addSnapshotListener{ snapshot, error ->
                callback(snapshot)
            }
    }

    suspend fun updateProfile(workerName: String, workerUsername: String) {
        Firebase.firestore.collection("worker").document(
            Firebase.auth.uid!!
        ).update("name", workerName).await()
        Firebase.firestore.collection("worker").document(
            Firebase.auth.uid!!
        ).update("username", workerUsername).await()
    }

    suspend fun updateProfilePicture(userId: String, url: String) {
        Firebase.firestore.collection("worker").document(userId).update("picture", url).await()
    }

    suspend fun loadWorkerServices(serviceIds: List<String>): List<Service> {
        val services = mutableListOf<Service>()
        for (serviceId in serviceIds) {
            val document = Firebase.firestore.collection("service").document(serviceId).get().await()
            document.toObject(Service::class.java)?.let { services.add(it) }
        }
        return services
    }

    suspend fun loadService(serviceId:String): DocumentSnapshot? {
        val document = Firebase.firestore.collection("service").document(serviceId).get().await()
        return document
    }

    suspend fun addReservation(id : String, idReservation : String) {
        Firebase.firestore.collection("worker")
            .document(id).update("reservationRefs", FieldValue.arrayUnion(idReservation)).await()
    }

    suspend fun addComment(workerId: String, comment: Comment) {
        val firestore = FirebaseFirestore.getInstance()
        val workerRef = firestore.collection("worker").document(workerId)
        val commentsRef = firestore.collection("comment").document(comment.id)

        firestore.runTransaction { transaction ->
            val workerSnapshot = transaction.get(workerRef)

            val commentsList = workerSnapshot.get("commentsRef") as? List<*> ?: listOf<String>()

            val validCommentRefs = commentsList.filterIsInstance<String>().filter { it.isNotBlank() }

            val totalScores: Int
            val sumScores: Double

            if (validCommentRefs.isNotEmpty()) {
                val currentScores = validCommentRefs.mapNotNull { commentId ->
                    val commentSnapshot = transaction.get(firestore.collection("comment").document(commentId.toString()))
                    commentSnapshot.getDouble("score")
                }

                totalScores = currentScores.size + 1
                sumScores = currentScores.sum() + comment.score
            } else {
                totalScores = 1
                sumScores = comment.score
            }

            val newAverageScore: Double = sumScores / totalScores

            transaction.set(commentsRef, comment)

            transaction.update(workerRef, "commentsRef", FieldValue.arrayUnion(comment.id))
            transaction.update(workerRef, "score", newAverageScore)

            null
        }.await()
    }
    suspend fun deleteEstablishmentFromWorker(email:String, pass:String, id:String) {
        val user = Firebase.auth.currentUser ?: throw Exception("User not logged in")
        val credential = EmailAuthProvider.getCredential(email, pass)
        try {
            user.reauthenticate(credential).await()
            val worker = Firebase.firestore.collection("worker").document(id).get().await()
            val establishmentID = worker.get("establishmentRef").toString()
            val updatesWorker = mapOf(
                "establishmentRef" to ""
            )
            Firebase.firestore.collection("worker").document(id).update(updatesWorker).await()

            val establishmentDoc = Firebase.firestore.collection("establishment").document(establishmentID).get().await()
            val workersRefs = establishmentDoc.get("workersRefs") as List<*>
            val updatedWorkersRefs = workersRefs.filter { it != id }
            Firebase.firestore.collection("establishment").document(establishmentID).update(mapOf("workersRefs" to updatedWorkersRefs)).await()

            Result.success(Unit)
        } catch (e: Exception) {
            throw Exception("Error al desvincular: ${e.message}")
        }
    }
}
