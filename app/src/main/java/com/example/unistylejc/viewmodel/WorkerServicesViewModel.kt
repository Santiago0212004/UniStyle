package com.example.unistylejc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.domain.model.Service
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class WorkerServicesViewModel(private val userRepository: UserRepository = UserRepositoryImpl()): ViewModel() {
    private val _loggedWorker = MutableLiveData<Worker?>()
    val loggedWorker: LiveData<Worker?> get() = _loggedWorker

    private val _workerServices = MutableLiveData<List<Service?>?>()
    val workerServices: LiveData<List<Service?>?> get() = _workerServices

    fun getLoggedWorker() {
        val workerId = FirebaseAuth.getInstance().currentUser?.uid
        viewModelScope.launch(Dispatchers.IO) {
            val customer = workerId?.let { userRepository.findWorkerById(it) }
            withContext(Dispatchers.Main) {
                _loggedWorker.value = customer
            }
        }
    }

    fun loadWorkerServices() {
        viewModelScope.launch(Dispatchers.IO) {
            val worker = _loggedWorker.value?.let { userRepository.findWorkerById(it.id) }
            worker?.let {
                val servicesList = userRepository.loadWorkerServices(it.servicesRef)
                withContext(Dispatchers.Main){
                    _workerServices.value = servicesList
                }
            }
        }
    }

    fun addServiceToWorker(name: String, price: Double){
        viewModelScope.launch(Dispatchers.IO) {

            val service = Service(UUID.randomUUID().toString(), name, price,
                _loggedWorker.value?.let { listOf(it.id) })

            _loggedWorker.value?.let { userRepository.addServiceToWorker(service, it.id) }
        }
    }
}