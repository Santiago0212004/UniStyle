package edu.co.icesi.unistyle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.co.icesi.unistyle.domain.model.AppAuthState
import edu.co.icesi.unistyle.repository.AuthRepository
import edu.co.icesi.unistyle.repository.AuthRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupViewmodel(val repo:AuthRepository = AuthRepositoryImpl()) : ViewModel() {

    val authStatus = MutableLiveData<AppAuthState?>()

    fun signup(email: String, pass: String, toString: String, toString1: String) {
        viewModelScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main){
                authStatus.value = AppAuthState.Loading("Cargando...")
            }
            val status = repo.signup(email,pass) //10s
            withContext(Dispatchers.Main){authStatus.value = status}

        }
    }

}