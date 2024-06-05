package com.example.unistylejc.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.repository.EstablishmentRepository
import com.example.unistylejc.repository.EstablishmentRepositoryImpl
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerDiscoverViewmodel(
    val establishmentRepository: EstablishmentRepositoryImpl = EstablishmentRepositoryImpl(),
    val userRepository: UserRepository = UserRepositoryImpl()) : ViewModel() {

    private val _reservedEstablishments = MutableLiveData<List<Establishment?>>(emptyList())
    val reservedEstablishments: LiveData<List<Establishment?>> get() = _reservedEstablishments

    private val _unreservedEstablishments = MutableLiveData<List<Establishment?>>(emptyList())
    val unreservedEstablishments: LiveData<List<Establishment?>>get() = _unreservedEstablishments
    private val _userState = MutableLiveData<Customer>()
    val userState:LiveData<Customer> get() = _userState



    //Los eventos de entrada
    fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.loadCustomer()
            user?.let {
                withContext(Dispatchers.Main) {
                    _userState.value = it
                }
            }
        }
    }

    fun observeUser() {
        userRepository.observeUser {
            _userState.value = it
        }
    }
    fun loadEstablishments(uid:String) {
        viewModelScope.launch {
            val reservations= userRepository.getCustomerReservations(uid)
            val (reserved, unreserved) = establishmentRepository.getReservedAndUnreservedEstablishments(uid,reservations)
            Log.e(">>>>>", reserved.toString())
            Log.e(">>>>>", unreserved.toString())
            _reservedEstablishments.value = reserved
            _unreservedEstablishments.value = unreserved

        }
    }
}
