package com.example.unistylejc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.repository.EstablishmentRepository
import com.example.unistylejc.repository.EstablishmentRepositoryImpl
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl
import kotlinx.coroutines.launch

class CustomerEstablishmentViewModel(
    private val establishmentRepository: EstablishmentRepository = EstablishmentRepositoryImpl(),
    private val userRepository: UserRepository = UserRepositoryImpl()
) : ViewModel() {
    private val _establishment = MutableLiveData<Establishment?>()
    val establishment: LiveData<Establishment?> get() = _establishment

    private val _selectedWorker = MutableLiveData<Worker?>()
    val selectedWorker: LiveData<Worker?> get() = _selectedWorker

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

    suspend fun findWorkerById(workerId: String): Worker? {
        return userRepository.findWorkerById(workerId)
    }
}
