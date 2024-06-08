package com.example.unistylejc.services

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class AuthServices {

    suspend fun signUp(email:String, pass:String) : AuthResult{
        return Firebase.auth.createUserWithEmailAndPassword(email, pass).await()
    }

    suspend fun logIn(email:String, pass:String) : AuthResult{
        return Firebase.auth.signInWithEmailAndPassword(email, pass).await()
    }

     fun changePassword(newPassword: String){
        val user = Firebase.auth.currentUser
        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("><>>><>", "User password updated.")
                }
            }
    }


    suspend fun checkRole(uid: String) : String{
        var role = ""
        try {
            val workerDocument = Firebase.firestore.collection("worker").document(uid).get().await()
            if (workerDocument.exists()) {
                role = "worker"
                Log.e(">>>>>>>>>>>", "WORKER")
            } else {
                val customerDocument = Firebase.firestore.collection("customer").document(uid).get().await()
                if (customerDocument.exists()) {
                    role = "customer"
                    Log.e(">>>>>>>>>>>", "CUSTOMER")
                } else {
                    Log.e("error", "No se encontró el usuario con el ID: $uid")
                }
            }
        } catch (e: Exception) {
            Log.e("error", e.message.toString())
        }
        return role
    }

    fun signOut(){
        Firebase.auth.signOut()
    }
}