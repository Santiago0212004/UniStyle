package com.example.unistylejc.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.repository.AuthRepository
import com.example.unistylejc.repository.AuthRepositoryImpl
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkerProfileViewModel(
    val userRepo: UserRepository = UserRepositoryImpl(),
    val authRepo: AuthRepository = AuthRepositoryImpl()
    ) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    //Estado
    private val _userState = MutableLiveData<Worker>()
    val userState:LiveData<Worker> get() = _userState

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> get() = _errorState



    //Los eventos de entrada
    fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepo.loadWorker()
            user?.let {
                withContext(Dispatchers.Main) {
                    _userState.value = it
                }
            }
        }
    }

    fun observeUser() {
        userRepo.observeWorker {
            _userState.value = it
        }
    }

    fun uploadProfilePicture(uri: Uri, isWorker: Boolean, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val downloadUri = userRepo.uploadProfilePicture(uri)
            if (downloadUri != null) {
                val userId = auth.currentUser?.uid
                val success = userRepo.updateProfilePictureUrl(userId!!, downloadUri.toString(), isWorker)
                callback(success)
            } else {
                callback(false)
            }
        }
    }

    fun signOut(){
        authRepo.signOut()
    }

    fun deleteEstablishmentFromWorker(email: String, pass: String, id:String, onSuccess: () -> Unit){
        viewModelScope.launch (Dispatchers.IO) {
            try {
                userRepo.deleteEstablishmentFromWorker(email, pass, id)
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main) {
                    _errorState.value = e.message ?: "Error al desvincular"
                }
            }
        }
    }

    fun clearError() {
        _errorState.value = null
    }

}