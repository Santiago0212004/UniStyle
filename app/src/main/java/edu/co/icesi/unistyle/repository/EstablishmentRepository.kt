package edu.co.icesi.unistyle.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import edu.co.icesi.unistyle.domain.model.Establishment
import edu.co.icesi.unistyle.services.EstablishmentService

interface EstablishmentRepository {
    suspend fun loadEstablishmentList() : ArrayList<String?>?
}

class EstablishmentRepositoryImpl(
    val establishmentServices:EstablishmentService = EstablishmentService()
) : EstablishmentRepository{
    override suspend fun loadEstablishmentList(): ArrayList<String?>? {
        val document = establishmentServices.loadEstablishmentList()
        val establishments = arrayListOf<String?>()
        document.documents.forEach {
            val est = it.toObject(Establishment::class.java)
            establishments.add(est?.name)
        }
        return establishments
    }
}