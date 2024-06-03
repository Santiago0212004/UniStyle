package com.example.unistylejc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkerProfileViewModel(
    val userRepo: UserRepository = UserRepositoryImpl(),

    ) : ViewModel() {

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

}