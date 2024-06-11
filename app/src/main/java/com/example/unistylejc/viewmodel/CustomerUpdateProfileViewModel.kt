package com.example.unistylejc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerUpdateProfileViewModel(
    val userRepo: UserRepository = UserRepositoryImpl()
):ViewModel() {

    private val _userState = MutableLiveData<Customer>()
    val userState: LiveData<Customer> get() = _userState
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

    fun updateProfile(name: String, username: String) {
        viewModelScope.launch (Dispatchers.IO) {
            userRepo.updateProfileCustomer(name, username)
        }
    }
}