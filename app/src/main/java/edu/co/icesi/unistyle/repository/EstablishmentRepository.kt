package edu.co.icesi.unistyle.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import edu.co.icesi.unistyle.domain.model.Establishment
import edu.co.icesi.unistyle.services.EstablishmentService

interface EstablishmentRepository {
    suspend fun loadEstablishmentList() : ArrayList<Establishment?>?
    suspend fun addWorker(id : String, idWorker: String)
}

class EstablishmentRepositoryImpl(
    val establishmentServices:EstablishmentService = EstablishmentService()
) : EstablishmentRepository{
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