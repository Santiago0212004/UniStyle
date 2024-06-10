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
    suspend fun loadPaymentMethod(paymentMethodId:String ):DocumentSnapshot {
        val output = Firebase.firestore.collection("payment_method").document(paymentMethodId).get().await()
        return output
    }

    suspend fun deleteReservation(reservationId: String?, customerId: String?, workerId: String?, establishmentId: String?) {
        try {
            val workerDoc = workerId?.let { Firebase.firestore.collection("worker").document(it).get().await() }
            val reservationRefsWork = workerDoc?.get("reservationRefs") as? List<*>
            if (reservationRefsWork != null) {
                val updatedReservationRefsWork = reservationRefsWork.filter { it != reservationId }
                Firebase.firestore.collection("worker").document(workerId)
                    .update(mapOf("reservationRefs" to updatedReservationRefsWork)).await()
            }

            val customerDoc = customerId?.let { Firebase.firestore.collection("customer").document(it).get().await() }
            val reservationRefsCus = customerDoc?.get("reservationRefs") as? List<*>
            if (reservationRefsCus != null) {
                val updatedReservationRefsCus = reservationRefsCus.filter { it != reservationId }
                Firebase.firestore.collection("customer").document(customerId)
                    .update(mapOf("reservationRefs" to updatedReservationRefsCus)).await()
            }

            val establishmentDoc = establishmentId?.let { Firebase.firestore.collection("establishment").document(it).get().await() }
            val reservationRefsEst = establishmentDoc?.get("reservationRefs") as? List<*>
            if (reservationRefsEst != null) {
                val updatedReservationRefsEst = reservationRefsEst.filter { it != reservationId }
                Firebase.firestore.collection("establishment").document(establishmentId)
                    .update(mapOf("reservationRefs" to updatedReservationRefsEst)).await()
            }

            if (reservationId != null) {
                Firebase.firestore.collection("reservation").document(reservationId).delete().await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            throw Exception("Error al eliminar la reserva: ${e.message}")
        }
    }

}