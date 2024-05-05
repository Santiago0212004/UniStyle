package edu.co.icesi.unistyle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.co.icesi.unistyle.domain.model.AppAuthState
import edu.co.icesi.unistyle.domain.model.Customer
import edu.co.icesi.unistyle.domain.model.Worker
import edu.co.icesi.unistyle.repository.AuthRepository
import edu.co.icesi.unistyle.repository.AuthRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpViewmodel(val repo:AuthRepository = AuthRepositoryImpl()) : ViewModel() {

    val authStatus = MutableLiveData<AppAuthState>()

    fun signupUser(customer: Customer, pass:String) {
        viewModelScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main){
                authStatus.value = AppAuthState.Loading("Cargando...")
            }
            val status = repo.signupUser(customer, pass)
            withContext(Dispatchers.Main){authStatus.value = status}

        }
    }

    fun signupWorker(worker: Worker, pass:String) {
        viewModelScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main){
                authStatus.value = AppAuthState.Loading("Cargando...")
            }
            val status = repo.signupWorker(worker, pass)
            withContext(Dispatchers.Main){authStatus.value = status}

        }
    }

}