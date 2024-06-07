package com.example.unistylejc.repository

import com.example.unistylejc.domain.model.Comment
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.domain.model.Reservation
import com.example.unistylejc.services.CommentService
import com.example.unistylejc.services.EstablishmentService

interface EstablishmentRepository {
    suspend fun loadEstablishmentList() : ArrayList<Establishment?>?
    suspend fun addWorker(id : String, idWorker: String)
    suspend fun getEstablishmentById(id: String): Establishment?
    suspend fun getReservedAndUnreservedEstablishments(
        customerId: String,
        customerReservations: List<Reservation>
    ): Pair<List<Establishment?>, List<Establishment?>>
    suspend fun findCommentById(commentId: String): Comment?
}

class EstablishmentRepositoryImpl(
    private val establishmentServices: EstablishmentService = EstablishmentService(),
    private val commentServices: CommentService = CommentService()
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

    override suspend fun getEstablishmentById(id:String): Establishment?{
        return establishmentServices.getEstablishmentById(id).toObject(Establishment::class.java)
    }

    override suspend fun findCommentById(commentId: String): Comment? {
        val document = commentServices.loadComment(commentId)
        return document.toObject(Comment::class.java)
    }

    override suspend fun getReservedAndUnreservedEstablishments(customerId: String, customerReservations:List<Reservation>): Pair<List<Establishment?>, List<Establishment?>> {
        val reservedEstablishmentIds = customerReservations.map { it.establishmentId }.toSet()

        val allEstablishments = establishmentServices.loadEstablishmentList()
        val establishments = arrayListOf<Establishment?>()
        allEstablishments.documents.forEach {
            val est = it.toObject(Establishment::class.java)
            establishments.add(est)
        }

        val reservedEstablishments = establishments.filter { it?.id in reservedEstablishmentIds }
        val unreservedEstablishments = establishments.filter { it?.id !in reservedEstablishmentIds }

        return Pair(reservedEstablishments, unreservedEstablishments)
    }
}