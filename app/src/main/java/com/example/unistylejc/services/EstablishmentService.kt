package com.example.unistylejc.services

import com.example.unistylejc.domain.model.Comment
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class EstablishmentService {
    suspend fun loadEstablishmentList(): QuerySnapshot {
        val output = Firebase.firestore.collection("establishment").get().await()
        return output
    }

    suspend fun addWorker(id : String, idWorker : String) {
        Firebase.firestore.collection("establishment")
            .document(id).update("workersRefs", FieldValue.arrayUnion(idWorker)).await()
    }


    suspend fun getEstablishmentById(establishmentId: String): DocumentSnapshot {
        val doc = Firebase.firestore.collection("establishment").document(establishmentId).get().await()
        return doc
    }

    suspend fun addComment(establishmentId: String, comment: Comment) {
        val firestore = FirebaseFirestore.getInstance()
        val establishmentRef = firestore.collection("establishment").document(establishmentId)
        val commentsRef = firestore.collection("comment").document(comment.id)

        firestore.runTransaction { transaction ->
            val establishmentSnapshot = transaction.get(establishmentRef)

            val commentsList = establishmentSnapshot.get("commentsRef") as? List<*> ?: listOf<String>()

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

            transaction.update(establishmentRef, "commentsRef", FieldValue.arrayUnion(comment.id))
            transaction.update(establishmentRef, "score", newAverageScore)

            null
        }.await()
    }

    suspend fun addReservation(id : String, idReservation : String) {
        Firebase.firestore.collection("establishment")
            .document(id).update("reservationRefs", FieldValue.arrayUnion(idReservation)).await()
    }

}