package com.example.unistylejc.viewmodel

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

    private val _workers = MutableLiveData<List<Worker?>>()
    val workers: LiveData<List<Worker?>> get() = _workers

    private val _comments = MutableLiveData<List<Comment?>>()
    val comments: LiveData<List<Comment?>> get() = _comments

    fun getLoggedCustomer(customerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val customer = userRepository.findCustomerById(customerId)
            withContext(Dispatchers.Main) {
                _loggedCustomer.value = customer
            }
        }
    }


    fun loadEstablishment(establishmentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val establishments = establishmentRepository.loadEstablishmentList()
            withContext(Dispatchers.Main){
                _establishment.value = establishments?.find { it?.id == establishmentId }
            }
        }
    }

    fun loadWorker(workerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val worker = userRepository.findWorkerById(workerId)
            withContext(Dispatchers.Main){
                _selectedWorker.value = worker
            }
        }
    }


    fun loadWorkerServices(workerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val worker = userRepository.findWorkerById(workerId)
            worker?.let {
                val servicesList = userRepository.loadWorkerServices(it.servicesRef)
                withContext(Dispatchers.Main){
                    _selectedWorkerServices.value = servicesList
                }
            }
        }
    }

    fun loadEstablishmentWorkers(establishmentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val establishmentWorkers = establishmentRepository.loadEstablishmentWorkers(establishmentId)
            withContext(Dispatchers.Main){
                _workers.value = establishmentWorkers.toList()
            }
        }
    }

    fun loadEstablishmentComments(establishmentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val establishmentComments = establishmentRepository.loadEstablishmentComments(establishmentId)
            withContext(Dispatchers.Main){
                _comments.value = establishmentComments.toList()
            }
        }
    }

    suspend fun findCustomerById(customerId: String): Customer? {
        return userRepository.findCustomerById(customerId)
    }

    suspend fun findWorkerById(workerId: String): Worker? {
        return userRepository.findWorkerById(workerId)
    }

    fun createReservation(reservation: Reservation) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.createReservation(reservation)
        }
    }

    fun addComment(comment: Comment){
        viewModelScope.launch(Dispatchers.IO) {
            establishmentRepository.addComment(comment)
            withContext(Dispatchers.Main){
                loadEstablishment(comment.establishmentRef)
                loadEstablishmentComments(comment.establishmentRef)
            }
        }
    }

    fun loadPaymentMethods() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){
                _paymentMethods.value = userRepository.loadPaymentMethods()
            }
        }
    }
}
