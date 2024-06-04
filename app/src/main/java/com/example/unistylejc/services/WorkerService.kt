package com.example.unistylejc.services

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.example.unistylejc.domain.model.Worker
import kotlinx.coroutines.tasks.await

class WorkerService {
    suspend fun createWorker(worker: Worker) {
        Firebase.firestore.collection("worker").document(worker.id).set(worker).await()
    }

    suspend fun loadWorker(uid: String): DocumentSnapshot {
        return Firebase.firestore.collection("worker").document(uid).get().await()
    }

    fun observeWorker(uid: String, callback: (DocumentSnapshot?) -> Unit) {
        Firebase.firestore.collection("worker").document(uid)
            .addSnapshotListener { snapshot, error ->
                callback(snapshot)
            }
    }

    suspend fun updateProfilePicture(userId: String, url: String) {
        Firebase.firestore.collection("worker").document(userId).update("picture", url).await()
    }
}
