package com.example.unistylejc.services

import com.example.unistylejc.domain.model.Comment
import com.example.unistylejc.domain.model.Customer
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class CustomerService {
    suspend fun createUser(customer: Customer) {
        Firebase.firestore.collection("customer").document(customer.id).set(customer).await()
    }

    suspend fun loadCustomer(customerId: String): DocumentSnapshot {
        return Firebase.firestore.collection("customer").document(customerId).get().await()
    }

    suspend fun updateProfilePicture(userId: String, url: String) {
        Firebase.firestore.collection("customer").document(userId).update("picture", url).await()
    }

    fun observeUser(id: String, callback: (DocumentSnapshot?) -> Unit) {
        Firebase.firestore.collection("users").document(id)
            .addSnapshotListener { snapshot, error ->
                callback(snapshot)
            }
    }

    suspend fun addReservation(id : String, idReservation : String) {
        Firebase.firestore.collection("customer")
            .document(id).update("reservationRefs", FieldValue.arrayUnion(idReservation)).await()
    }

    suspend fun addComment(id : String, comment : Comment) {
        Firebase.firestore.collection("customer")
            .document(id).update("commentsRef", FieldValue.arrayUnion(comment.id)).await()
    }
}
