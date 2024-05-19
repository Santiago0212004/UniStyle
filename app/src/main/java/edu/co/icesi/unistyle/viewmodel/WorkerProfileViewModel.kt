package icesi.edu.co.icesiapp241.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.co.icesi.unistyle.domain.model.Worker
import edu.co.icesi.unistyle.repository.UserRepository
import edu.co.icesi.unistyle.repository.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class WorkerProfileViewModel(
    val userRepo: UserRepository = UserRepositoryImpl(),

    ) : ViewModel() {

    //Estado
    private val _userState = MutableLiveData<Worker>()
    val userState:LiveData<Worker> get() = _userState



    //Los eventos de entrada
    fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepo.loadWorker()
            user?.let {
                withContext(Dispatchers.Main) {
                    _userState.value = it
                }
            }
        }
    }

    fun observeUser() {
        userRepo.observeWorker({
            _userState.value = it
        }
        )
    }




}