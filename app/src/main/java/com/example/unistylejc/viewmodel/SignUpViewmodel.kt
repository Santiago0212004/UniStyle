package com.example.unistylejc.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.repository.AuthRepository
import com.example.unistylejc.repository.AuthRepositoryImpl
import edu.co.icesi.unistyle.domain.model.AppAuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpViewmodel(val repo: AuthRepository = AuthRepositoryImpl()) : ViewModel() {
    val authStatus = MutableLiveData<AppAuthState>()

    fun signupUser(customer: Customer, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = repo.signupUser(customer, pass)
            withContext(Dispatchers.Main) { authStatus.value = status }
        }
    }

    fun signupWorker(worker: Worker, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = repo.signupWorker(worker, pass)
            withContext(Dispatchers.Main) { authStatus.value = status }
        }
    }
}

