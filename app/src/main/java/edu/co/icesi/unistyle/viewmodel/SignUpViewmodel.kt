package edu.co.icesi.unistyle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.co.icesi.unistyle.domain.model.AuthStatus
import edu.co.icesi.unistyle.repository.AuthRepository
import edu.co.icesi.unistyle.repository.AuthRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpViewmodel (val repo: AuthRepository = AuthRepositoryImpl()) : ViewModel() {

    val authStatus = MutableLiveData<Int>()

    fun signup(email: String, pass: String){
        viewModelScope.launch (Dispatchers.IO) {
            withContext(Dispatchers.Main){authStatus.value = AuthStatus.LOADING}
            val status = repo.signup(email,pass)
            withContext(Dispatchers.Main){authStatus.value = status}
        }
    }
}