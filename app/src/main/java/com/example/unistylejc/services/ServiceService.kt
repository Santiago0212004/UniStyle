package com.example.unistylejc.services

import com.example.unistylejc.domain.model.Reservation
import com.example.unistylejc.domain.model.Service
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class ServiceService {
    suspend fun addServiceToWorker(service: Service, workerId: String) {
        Firebase.firestore.collection("service").document(service.id).set(service).await()
        Firebase.firestore.collection("worker").document(workerId).update("servicesRef", FieldValue.arrayUnion(service.id)).await()
    }
}