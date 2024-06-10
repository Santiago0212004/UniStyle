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
import android.content.Context
import android.content.SharedPreferences

class SignUpViewmodel(
    private val authRepo: AuthRepository = AuthRepositoryImpl(),
    private val estRepo: EstablishmentRepository = EstablishmentRepositoryImpl()
) : ViewModel() {
    val authStatus = MutableLiveData<AppAuthState>()

    private val _establishmentState = MutableLiveData<ArrayList<Establishment?>?>()
    val establishmentState: LiveData<ArrayList<Establishment?>?> get() = _establishmentState

    fun signupUser(context: Context, customer: Customer, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = authRepo.signupUser(customer, pass)
            withContext(Dispatchers.Main) {
                authStatus.value = status
                if (status is AppAuthState.SuccessLogin) {
                    saveSignUpState(context, customer.email, pass, "customer")
                }
            }
        }
    }

    fun signupWorker(context: Context, worker: Worker, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val status = authRepo.signupWorker(worker, pass)
            withContext(Dispatchers.Main) {
                authStatus.value = status
                if (status is AppAuthState.SuccessLogin) {
                    saveSignUpState(context, worker.email, pass, "worker")
                }
            }
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

    private fun saveSignUpState(context: Context, email: String, pass: String, role: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", pass)
        editor.putString("role", role)
        editor.apply()
    }

    fun loadUserSession(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)
        val role = sharedPreferences.getString("role", null)
        if (email != null && password != null && role != null) {
            if (role == "customer") {
                val customer = Customer(id = "", email = email, name = "", username = "", reservationRefs = listOf(), commentsRef = listOf())
                signupUser(context, customer, password)
            } else {
                val worker = Worker(id = "", email = email, name = "", username = "", description = "", reservationRefs = listOf(), commentsRef = listOf(), establishmentRef = "")
                signupWorker(context, worker, password)
            }
        }
    }

    fun clearUserSession(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}
