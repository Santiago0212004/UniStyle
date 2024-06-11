package com.example.unistylejc.viewmodel

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.unistylejc.domain.model.CommentEntity
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.repository.AuthRepository
import com.example.unistylejc.repository.AuthRepositoryImpl
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkerCommunityViewmodel(
    val userRepo: UserRepository = UserRepositoryImpl(),

) : ViewModel() {

    //Estado
    private val _userState = MutableLiveData<Worker>()
    val userState: LiveData<Worker> get() = _userState

    private val _commentListState = MutableLiveData<List<CommentEntity>>()
    val commentListState: LiveData<List<CommentEntity>> get() = _commentListState



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
        userRepo.observeWorker {
            _userState.value = it
        }
    }

    suspend fun loadComments(){
        viewModelScope.launch(Dispatchers.IO) {
            val workerComments = userRepo.getWorkerComments()
            withContext(Dispatchers.Main) {
                _commentListState.value = workerComments
            }
        }
    }

    fun sendResponse(commentId: String, commenterId: String, content:String ){
        viewModelScope.launch(Dispatchers.IO) {
            userRepo.sendResponse(commentId, commenterId, content)
        }
    }

}