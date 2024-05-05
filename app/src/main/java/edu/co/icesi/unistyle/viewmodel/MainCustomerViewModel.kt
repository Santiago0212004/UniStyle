package edu.co.icesi.unistyle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.co.icesi.unistyle.domain.model.Customer
import edu.co.icesi.unistyle.domain.model.Establishment
import edu.co.icesi.unistyle.repository.EstablishmentRepository
import edu.co.icesi.unistyle.repository.EstablishmentRepositoryImpl
import edu.co.icesi.unistyle.repository.UserRepository
import edu.co.icesi.unistyle.repository.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainCustomerViewModel(
    private val userRepository: UserRepository = UserRepositoryImpl(),
    private val establishmentRepository: EstablishmentRepository = EstablishmentRepositoryImpl()
): ViewModel() {

    private val _loggedCustomer = MutableLiveData<Customer?>();
    val loggedCustomer: LiveData<Customer?> get() = _loggedCustomer

    private val _establishments = MutableLiveData(arrayListOf<Establishment?>())
    val establishments:LiveData<ArrayList<Establishment?>> get() = _establishments

    fun getLoggedCustomer(customerId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val customer = userRepository.findCustomerById(customerId)
            withContext(Dispatchers.Main){
                 _loggedCustomer.value = customer
            }
        }
    }
    fun getEstablishments(){
        viewModelScope.launch(Dispatchers.IO){
            val establishments = establishmentRepository.loadEstablishmentList()
            withContext(Dispatchers.Main){
                _establishments.value = establishments
            }
        }
    }

    fun filterEstablishments(query: String?){
        viewModelScope.launch(Dispatchers.IO){
            if(query != null){
                val filteredEstablishments = ArrayList<Establishment?>()
                for(establishment in _establishments.value!!){
                    if (establishment != null) {
                        if(establishment.name.lowercase(Locale.ROOT).contains(query)){
                            filteredEstablishments.add(establishment)
                        }
                    }
                    withContext(Dispatchers.Main){
                        _establishments.value = filteredEstablishments
                    }
                }
            }
        }
    }

}