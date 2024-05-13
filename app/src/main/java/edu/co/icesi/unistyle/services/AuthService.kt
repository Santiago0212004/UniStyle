package edu.co.icesi.unistyle.services

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import edu.co.icesi.unistyle.MainActivity
import edu.co.icesi.unistyle.WorkerSelectActivity
import kotlinx.coroutines.tasks.await
import kotlin.math.log

class AuthServices {

    suspend fun signUp(email:String, pass:String) : AuthResult{
        return Firebase.auth.createUserWithEmailAndPassword(email, pass).await()
    }

    suspend fun logIn(email:String, pass:String) : AuthResult{
        return Firebase.auth.signInWithEmailAndPassword(email, pass).await()
    }

    suspend fun checkRole(uid: String) : String{
        var role = ""
        try {
            val workerDocument = Firebase.firestore.collection("worker").document(uid).get().await()
            if (workerDocument.exists()) {
                role = "worker"
            } else {
                val customerDocument = Firebase.firestore.collection("customer").document(uid).get().await()
                if (customerDocument.exists()) {
                    role = "customer"
                } else {
                    Log.e("error", "No se encontró el usuario con el ID: $uid")
                }
            }
        } catch (e: Exception) {
            Log.e("error", e.message.toString())
        }
        return role
    }

}