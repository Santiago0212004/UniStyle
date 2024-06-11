package com.example.unistylejc.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unistylejc.repository.AuthRepository
import com.example.unistylejc.repository.AuthRepositoryImpl
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.co.icesi.unistyle.domain.model.AppAuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadImageViewModel(private val userRepository: UserRepository = UserRepositoryImpl(), private val authRepo: AuthRepository = AuthRepositoryImpl()) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val currentRole = MutableLiveData<String>()
    val currentUser = MutableLiveData<FirebaseUser?>()

    fun getRole(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val role = authRepo.getCurrentRole(uid)
            withContext(Dispatchers.Main) {
                currentRole.value = role
            }
        }
    }

    fun getCurrentUser(){
        currentUser.value = auth.currentUser
    }

    fun uploadProfilePicture(uri: Uri, isWorker: Boolean, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val downloadUri = userRepository.uploadProfilePicture(uri)
            if (downloadUri != null) {
                val userId = auth.currentUser?.uid
                val success = userRepository.updateProfilePictureUrl(userId!!, downloadUri.toString(), isWorker)
                callback(success)
            } else {
                callback(false)
            }
        }
    }
}

