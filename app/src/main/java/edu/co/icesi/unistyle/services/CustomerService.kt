package edu.co.icesi.unistyle.services

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import edu.co.icesi.unistyle.domain.model.Customer
import kotlinx.coroutines.tasks.await

class CustomerService {

    suspend fun createUser(customer: Customer) {
        Firebase.firestore.collection("customer").document(customer.id).set(customer).await()
    }

    suspend fun loadCustomer(customerId: String): DocumentSnapshot {
        return Firebase.firestore.collection("customer").document(customerId).get().await()
    }

    suspend fun loadWorker(workerId: String): DocumentSnapshot {
        return Firebase.firestore.collection("worker").document(workerId).get().await()
    }

    fun observeUser(id: String, callback: (DocumentSnapshot?) -> Unit) {
        Firebase.firestore.collection("users").document(id)
            .addSnapshotListener{ snapshot, error ->
                callback(snapshot)
            }
    }


}