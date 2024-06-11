package com.example.unistylejc.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.repository.AuthRepository
import com.example.unistylejc.repository.AuthRepositoryImpl
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerProfileViewModel(
    val userRepo: UserRepository = UserRepositoryImpl(),
    val authRepo: AuthRepository = AuthRepositoryImpl()

    ) : ViewModel() {

    //Estado
    private val _userState = MutableLiveData<Customer>()
    val userState: LiveData<Customer> get() = _userState

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> get() = _errorState

    fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepo.loadCustomer()
            user?.let {
                withContext(Dispatchers.Main) {
                    _userState.value = it
                }
            }
        }
    }

    fun observeUser() {
        userRepo.observeUser {
            _userState.value = it
        }
    }

    fun signOut(){
        authRepo.signOut()
    }

    fun deleteAccount(email: String, pass: String, id:String, onSuccess: () -> Unit){
        viewModelScope.launch (Dispatchers.IO) {
            try {
                userRepo.deleteAccount(email, pass, id)
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main) {
                    _errorState.value = e.message ?: "Error al eliminar la cuenta"
                }
            }
        }
    }

    fun clearError() {
        _errorState.value = null
    }
    fun uploadProfilePicture(uri: Uri, isWorker: Boolean, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val downloadUri = userRepo.uploadProfilePicture(uri)
            Log.e(">>>", downloadUri.toString())
            if (downloadUri != null) {
                val userId = Firebase.auth.uid
                val success = userRepo.updateProfilePictureUrl(userId!!, downloadUri.toString(), isWorker = false)
                callback(success)
            } else {
                callback(false)
            }
        }
    }
}