package com.example.unistylejc.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unistylejc.domain.model.Comment
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.domain.model.PaymentMethod
import com.example.unistylejc.domain.model.Reservation
import com.example.unistylejc.domain.model.Service
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.repository.EstablishmentRepository
import com.example.unistylejc.repository.EstablishmentRepositoryImpl
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerEstablishmentViewModel(
    private val establishmentRepository: EstablishmentRepository = EstablishmentRepositoryImpl(),
    private val userRepository: UserRepository = UserRepositoryImpl()
) : ViewModel() {
    private val _loggedCustomer = MutableLiveData<Customer?>()
    val loggedCustomer: LiveData<Customer?> get() = _loggedCustomer

    private val _establishment = MutableLiveData<Establishment?>()
    val establishment: LiveData<Establishment?> get() = _establishment

    private val _selectedWorker = MutableLiveData<Worker?>()
    val selectedWorker: LiveData<Worker?> get() = _selectedWorker

    private val _selectedWorkerServices = MutableLiveData<List<Service>>()
    val selectedWorkerServices: LiveData<List<Service>> get() = _selectedWorkerServices

    private val _paymentMethods = MutableLiveData<List<PaymentMethod>>()
    val paymentMethods: LiveData<List<PaymentMethod>> get() = _paymentMethods

    fun getLoggedCustomer(customerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val customer = userRepository.findCustomerById(customerId)
            withContext(Dispatchers.Main) {
                _loggedCustomer.value = customer
            }
        }
    }


    fun loadEstablishment(establishmentId: String) {
        viewModelScope.launch {
            val establishments = establishmentRepository.loadEstablishmentList()
            _establishment.value = establishments?.find { it?.id == establishmentId }
        }
    }

    fun loadWorker(workerId: String) {
        viewModelScope.launch {
            val worker = userRepository.findWorkerById(workerId)
            _selectedWorker.value = worker
        }
    }

    fun loadWorkerServices(workerId: String) {
        viewModelScope.launch {
            val worker = userRepository.findWorkerById(workerId)
            worker?.let {
                val servicesList = userRepository.loadWorkerServices(it.servicesRef)
                Log.e("AAA",it.toString())
                Log.e("AAAA",servicesList.toString())
                _selectedWorkerServices.value = servicesList
            }
        }
    }

    suspend fun findWorkerById(workerId: String): Worker? {
        return userRepository.findWorkerById(workerId)
    }

    suspend fun findCustomerById(customerId: String): Customer? {
        return userRepository.findCustomerById(customerId)
    }

    suspend fun findCommentById(commentId: String): Comment? {
        return establishmentRepository.findCommentById(commentId)
    }

     fun createReservation(reservation: Reservation) {
        viewModelScope.launch {
            userRepository.createReservation(reservation)
        }
    }

    fun loadPaymentMethods() {
        viewModelScope.launch {
            _paymentMethods.value = userRepository.loadPaymentMethods()
        }
    }
}
