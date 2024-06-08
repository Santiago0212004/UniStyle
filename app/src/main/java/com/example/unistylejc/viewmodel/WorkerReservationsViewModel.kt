package com.example.unistylejc.viewmodel

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Reservation
import com.example.unistylejc.domain.model.ReservationEntity
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.repository.UserRepository
import com.example.unistylejc.repository.UserRepositoryImpl
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkerReservationsViewModel(
    val userRepo: UserRepository = UserRepositoryImpl(),
    ) : ViewModel() {


    private val _pastReservations = MutableLiveData<List<ReservationEntity>>(emptyList())
    val pastReservations: LiveData<List<ReservationEntity>> = _pastReservations

    private val _futureReservations = MutableLiveData<List<ReservationEntity>>(emptyList())
    val futureReservations: LiveData<List<ReservationEntity>> = _futureReservations


    fun loadWorkerReservations() {
        viewModelScope.launch {

            val (past, future) = userRepo.getWorkerReservations(Firebase.auth.uid!!)
            _pastReservations.value = past
            _futureReservations.value = future
        }
    }
}