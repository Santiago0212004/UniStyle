package com.example.unistylejc.services

import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
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
}