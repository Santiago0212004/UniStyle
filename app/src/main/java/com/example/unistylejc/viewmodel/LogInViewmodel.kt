package com.example.unistylejc.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unistylejc.repository.AuthRepository
import com.example.unistylejc.repository.AuthRepositoryImpl
import edu.co.icesi.unistyle.domain.model.AppAuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl

class LogInViewmodel(val repo: AuthRepository = AuthRepositoryImpl(), val authRepo: AuthRepository = AuthRepositoryImpl()) : ViewModel() {

    val authStatus = MutableLiveData<AppAuthState?>()
    val changePasswordSuccess = MutableLiveData<Boolean?>(null)

    fun login(context: Context, email: String, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main) {
                authStatus.value = AppAuthState.Loading("Cargando...")
            }
            val status = repo.login(email, pass)
            withContext(Dispatchers.Main) {
                authStatus.value = status
                if (status is AppAuthState.SuccessLogin) {
                    saveLoginState(context, email, pass, status.role)
                }
            }
        }
    }

    private fun saveLoginState(context: Context, email: String, pass: String, role: String) {
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
        if (email != null && password != null) {
            login(context, email, password)
        }
    }

    fun clearUserSession(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    fun forgotPassword(email: String){
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                changePasswordSuccess.value = authRepo.forgotPassword(email)
            }
        }
    }
}
