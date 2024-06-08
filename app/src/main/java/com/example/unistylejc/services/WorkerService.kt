package com.example.unistylejc.services

import com.example.unistylejc.domain.model.Comment
import com.example.unistylejc.domain.model.Service
import com.example.unistylejc.domain.model.Worker
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
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
        val establishmentRef = firestore.collection("worker").document(workerId)
        val commentsRef = firestore.collection("comment").document(comment.id)

        firestore.runTransaction { transaction ->
            val establishmentSnapshot = transaction.get(establishmentRef)

            val commentsList = establishmentSnapshot.get("commentsRef") as? List<*> ?: listOf<String>()

            val currentScores = commentsList.mapNotNull { commentId ->
                val commentSnapshot = transaction.get(firestore.collection("comment").document(commentId.toString()))
                commentSnapshot.getDouble("score")
            }

            val totalScores = currentScores.size + 1
            val sumScores = currentScores.sum() + comment.score
            val newAverageScore = sumScores / totalScores

            transaction.set(commentsRef, comment)

            transaction.update(establishmentRef, "commentsRef", FieldValue.arrayUnion(comment.id))
            transaction.update(establishmentRef, "score", newAverageScore)

            null
        }.await()
    }
}
