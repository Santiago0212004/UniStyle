package edu.co.icesi.unistyle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.co.icesi.unistyle.domain.model.Establishment
import edu.co.icesi.unistyle.repository.EstablishmentRepository
import edu.co.icesi.unistyle.repository.EstablishmentRepositoryImpl
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