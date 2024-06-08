package com.example.unistylejc.services

import com.example.unistylejc.domain.model.Reservation
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class ReservationService {
    suspend fun createReservation(reservation: Reservation) {
        Firebase.firestore.collection("reservation").document(reservation.id).set(reservation).await()
    }

    suspend fun loadReservation(reservationId: String): DocumentSnapshot {
        return Firebase.firestore.collection("reservation").document(reservationId).get().await()
    }

    suspend fun loadPaymentMethods(): QuerySnapshot {
        val output = Firebase.firestore.collection("payment_method").get().await()
        return output
    }


}