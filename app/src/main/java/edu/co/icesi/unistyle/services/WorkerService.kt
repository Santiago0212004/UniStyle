package edu.co.icesi.unistyle.services

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import edu.co.icesi.unistyle.domain.model.Worker
import kotlinx.coroutines.tasks.await

class WorkerService {
    suspend fun createWorker(worker: Worker) {
        Firebase.firestore.collection("worker").document(worker.id).set(worker).await()
    }
}