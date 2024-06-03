package com.example.unistylejc.viewmodel

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainCustomerViewModel(
    private val userRepository: UserRepository = UserRepositoryImpl(),
    private val establishmentRepository: EstablishmentRepository = EstablishmentRepositoryImpl()
): ViewModel() {

    private val _loggedCustomer = MutableLiveData<Customer?>()
    val loggedCustomer: LiveData<Customer?> get() = _loggedCustomer

    private val _establishments = MutableLiveData<ArrayList<Establishment?>>()
    val establishments: LiveData<ArrayList<Establishment?>> get() = _establishments

    private val _allEstablishments = MutableLiveData<ArrayList<Establishment?>>()
    val allEstablishments: LiveData<ArrayList<Establishment?>> get() = _allEstablishments

    fun getLoggedCustomer(customerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val customer = userRepository.findCustomerById(customerId)
            withContext(Dispatchers.Main) {
                _loggedCustomer.value = customer
            }
        }
    }

    fun getEstablishments() {
        viewModelScope.launch(Dispatchers.IO) {
            val establishments = establishmentRepository.loadEstablishmentList()
            withContext(Dispatchers.Main) {
                _establishments.value = establishments
                _allEstablishments.value = establishments
            }
        }
    }

    fun filterEstablishments(query: String?, city: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val allEstablishments = _allEstablishments.value
            val filteredEstablishments = allEstablishments?.filter { establishment ->
                val matchesQuery = query.isNullOrEmpty() || establishment?.name?.lowercase(Locale.ROOT)?.contains(query.lowercase(Locale.ROOT)) == true
                val matchesCity = city.isNullOrEmpty() || city == "TODAS LAS CIUDADES" || establishment?.city?.lowercase(Locale.ROOT)?.contains(city.lowercase(Locale.ROOT)) == true
                matchesQuery && matchesCity
            }
            withContext(Dispatchers.Main) {
                _establishments.value = filteredEstablishments?.let { ArrayList(it) }
            }
        }
    }

}
