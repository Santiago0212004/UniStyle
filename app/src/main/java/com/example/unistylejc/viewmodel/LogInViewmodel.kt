package com.example.unistylejc.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unistylejc.repository.AuthRepository
import com.example.unistylejc.repository.AuthRepositoryImpl
import edu.co.icesi.unistyle.domain.model.AppAuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogInViewmodel(val repo: AuthRepository = AuthRepositoryImpl()) : ViewModel()  {

    val authStatus = MutableLiveData<AppAuthState?>()

    fun login(email: String, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main){
                authStatus.value = AppAuthState.Loading("Cargando...")
            }
            val status = repo.login(email,pass)
            withContext(Dispatchers.Main){authStatus.value = status}

        }
    }
}