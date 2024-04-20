package edu.co.icesi.unistyle.services

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import edu.co.icesi.unistyle.domain.model.User
import kotlinx.coroutines.tasks.await

class UserService {

    suspend fun createUser(user: User) {
        Firebase.firestore.collection("users").document(user.id).set(user).await()
    }

    suspend fun loadUser(id: String): DocumentSnapshot {
        val output = Firebase.firestore.collection("users").document(id).get().await()
        return output
    }

    fun observeUser(id: String, callback: (DocumentSnapshot?) -> Unit) {
        Firebase.firestore.collection("users").document(id)
            .addSnapshotListener{ snapshot, error ->
                callback(snapshot)
            }
    }


}