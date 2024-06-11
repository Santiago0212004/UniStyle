package com.example.unistylejc.services

import android.net.Uri
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class FileService {
    private val storage: FirebaseStorage = Firebase.storage

    suspend fun uploadProfilePicture(uri: Uri, userId: String): Uri? {
        val storageRef = storage.reference.child("profile_pictures/$userId.jpg")
        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await()
    }
}
