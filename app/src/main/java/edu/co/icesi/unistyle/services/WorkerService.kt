package edu.co.icesi.unistyle.services

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import edu.co.icesi.unistyle.domain.model.Worker
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
        Firebase.firestore.collection("users").document(uid)
            .addSnapshotListener{ snapshot, error ->
                callback(snapshot)
            }
    }

}