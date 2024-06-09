package com.example.unistylejc.services

import com.example.unistylejc.domain.model.Comment
import com.example.unistylejc.domain.model.Response
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class CommentService {
    suspend fun loadComment(id: String): DocumentSnapshot {
        return Firebase.firestore.collection("comment").document(id).get().await()
    }

    suspend fun addComment(comment: Comment) {
        Firebase.firestore.collection("comment").document(comment.id).set(comment).await()
    }

    suspend fun getAllComments(): QuerySnapshot? {
        return Firebase.firestore.collection("comment").get().await()
    }

    suspend fun getResponseById(id: String):DocumentSnapshot{
        return Firebase.firestore.collection("response").document(id).get().await()
    }


    suspend fun addResponse(response:Response) {
        Firebase.firestore.collection("response").document(response.id).set(response).await()
    }

    fun asociateResponse(commentId: String, responseId: String){
        Firebase.firestore.collection("comment").document(commentId).update("responseRef", responseId)
    }
}