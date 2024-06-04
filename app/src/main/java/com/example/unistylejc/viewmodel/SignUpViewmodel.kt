package com.example.unistylejc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.repository.AuthRepository
import com.example.unistylejc.repository.AuthRepositoryImpl
import com.example.unistylejc.repository.EstablishmentRepository
import com.example.unistylejc.repository.EstablishmentRepositoryImpl
import edu.co.icesi.unistyle.domain.model.AppAuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpViewmodel(
    private val authRepo: AuthRepository = AuthRepositoryImpl(),
    private val estRepo: EstablishmentRepository = EstablishmentRepositoryImpl()
) : ViewModel() {
    val authStatus = MutableLiveData<AppAuthState>()

    private val _establishmentState = MutableLiveData<ArrayList<Establishment?>?>()
    val establishmentState: LiveData<ArrayList<Establishment?>?> get() = _establishmentState

    fun signupUser(customer: Customer, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = authRepo.signupUser(customer, pass)
            withContext(Dispatchers.Main) { authStatus.value = status }
        }
    }

    fun signupWorker(worker: Worker, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = authRepo.signupWorker(worker, pass)
            withContext(Dispatchers.Main) { authStatus.value = status }
        }
    }

    fun loadEstablishmentList() {
        viewModelScope.launch(Dispatchers.IO) {
            val establishments = estRepo.loadEstablishmentList()
            withContext(Dispatchers.Main) {
                _establishmentState.value = establishments
            }
        }
    }
}
