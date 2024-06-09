package com.example.unistylejc.services

import com.example.unistylejc.domain.model.Comment
import com.example.unistylejc.domain.model.Customer
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.oAuthCredential
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

    suspend fun deleteAccount(email:String, pass:String, id:String) {
        val user = Firebase.auth.currentUser ?: throw Exception("User not logged in")
        val credential = EmailAuthProvider.getCredential(email, pass)
        try {
            user.reauthenticate(credential).await()
            user.delete().await()
            val updates = mapOf(
                "name" to "Usuario inexistente",
                "email" to "Delete",
                "username" to "Delete",
                "picture" to "https://firebasestorage.googleapis.com/v0/b/unistyle-940e2.appspot.com/o/no_user.png?alt=media&token=51c4f2b4-e1da-4b3f-bad9-016bc0416d81"
            )
            Firebase.firestore.collection("customer").document(id).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            throw Exception("Error al eliminar la cuenta: ${e.message}")
        }
    }

    suspend fun addComment(id : String, comment : Comment) {
        Firebase.firestore.collection("customer")
            .document(id).update("commentsRef", FieldValue.arrayUnion(comment.id)).await()
    }
    suspend fun updateProfile(customerName: String, customerUsername: String) {
        Firebase.firestore.collection("customer").document(
            Firebase.auth.uid!!
        ).update("name", customerName).await()
        Firebase.firestore.collection("customer").document(
            Firebase.auth.uid!!
        ).update("username", customerUsername).await()
    }
}