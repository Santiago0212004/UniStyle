package com.example.unistylejc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.repository.EstablishmentRepository
import com.example.unistylejc.repository.EstablishmentRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EstablishmentViewmodel(val repo: EstablishmentRepository = EstablishmentRepositoryImpl()) : ViewModel() {

    private val _establishmentState = MutableLiveData<ArrayList<Establishment?>?>()
    val establishmentState: LiveData<ArrayList<Establishment?>?> get() = _establishmentState

    fun loadEstablishmentList() {
        viewModelScope.launch(Dispatchers.IO) {
            val establishments = repo.loadEstablishmentList()
            withContext(Dispatchers.Main){
                _establishmentState.value = establishments
            }
        }
    }

    fun addWorker(id : String, idWorker: String){
        viewModelScope.launch(Dispatchers.IO){
            repo.addWorker(id, idWorker)
        }
    }
}