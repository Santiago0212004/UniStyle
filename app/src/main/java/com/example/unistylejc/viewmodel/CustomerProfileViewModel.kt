package com.example.unistylejc.viewmodel

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerProfileViewModel(
    val userRepo: UserRepository = UserRepositoryImpl(),

    ) : ViewModel() {

    //Estado
    private val _userState = MutableLiveData<Customer>()
    val userState: LiveData<Customer> get() = _userState


    //Los eventos de entrada
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
}