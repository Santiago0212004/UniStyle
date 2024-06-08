package com.example.unistylejc.services

import com.example.unistylejc.domain.model.Comment
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class CommentService {
    suspend fun loadComment(id: String): DocumentSnapshot {
        return Firebase.firestore.collection("comment").document(id).get().await()
    }

    suspend fun addComment(comment: Comment) {
        Firebase.firestore.collection("comment").document(comment.id).set(comment).await()
    }
}