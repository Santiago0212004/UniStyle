package com.example.unistylejc.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.repository.AuthRepository
import com.example.unistylejc.repository.AuthRepositoryImpl
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl
import edu.co.icesi.unistyle.domain.model.AppAuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkerChangePasswordViewmodel(
    val userRepo: UserRepository = UserRepositoryImpl(),
    val authRepo: AuthRepository = AuthRepositoryImpl(),

    ) : ViewModel() {
    val authStatus = MutableLiveData<AppAuthState?>()
    //Estado
    private val _userState = MutableLiveData<Worker>()
    val userState:LiveData<Worker> get() = _userState



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

    fun reauthenticate(password: String){
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){
                authStatus.value = AppAuthState.Loading("Cargando...")
            }
            userState.value?.email?.let{
                val status = authRepo.login(it,password) //10s
                withContext(Dispatchers.Main){authStatus.value = status}
            }

        }
    }

    fun changePassword(newPassword: String) {
        viewModelScope.launch (Dispatchers.IO) {
            authRepo.changePassword(newPassword)
        }
    }


}