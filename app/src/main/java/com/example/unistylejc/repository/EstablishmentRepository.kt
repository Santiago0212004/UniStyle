package com.example.unistylejc.repository

import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.services.EstablishmentService

interface EstablishmentRepository {
    suspend fun loadEstablishmentList() : ArrayList<Establishment?>?
    suspend fun addWorker(id : String, idWorker: String)
}

class EstablishmentRepositoryImpl(
    private val establishmentServices: EstablishmentService = EstablishmentService()
) : EstablishmentRepository {
    override suspend fun loadEstablishmentList(): ArrayList<Establishment?>? {
        val document = establishmentServices.loadEstablishmentList()
        val establishments = arrayListOf<Establishment?>()
        document.documents.forEach {
            val est = it.toObject(Establishment::class.java)
            establishments.add(est)
        }
        return establishments
    }

    override suspend fun addWorker(id : String, idWorker: String){
        establishmentServices.addWorker(id,idWorker)
    }
}