package com.example.unistylejc.services

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.example.unistylejc.domain.model.Worker
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class WorkerService {
    suspend fun createWorker(worker: Worker) {
        Firebase.firestore.collection("worker").document(worker.id).set(worker).await()
    }
    suspend fun loadWorker(uid: String): DocumentSnapshot {
        val output = Firebase.firestore.collection("worker").document(uid).get().await()
        return output
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

}