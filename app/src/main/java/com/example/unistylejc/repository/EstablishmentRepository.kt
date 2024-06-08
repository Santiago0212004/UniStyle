package com.example.unistylejc.repository

import com.example.unistylejc.domain.model.Comment
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.domain.model.Reservation
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.services.CommentService
import com.example.unistylejc.services.CustomerService
import com.example.unistylejc.services.EstablishmentService
import com.example.unistylejc.services.WorkerService

interface EstablishmentRepository {
    suspend fun loadEstablishmentList() : ArrayList<Establishment?>?
    suspend fun addWorker(id : String, idWorker: String)
    suspend fun getEstablishmentById(id: String): Establishment?
    suspend fun getReservedAndUnreservedEstablishments(
        customerId: String,
        customerReservations: List<Reservation>
    ): Pair<List<Establishment?>, List<Establishment?>>
    suspend fun findCommentById(commentId: String): Comment?
    suspend fun addComment(comment: Comment)
    suspend fun loadEstablishmentWorkers(establishmentId: String): ArrayList<Worker?>
    suspend fun loadEstablishmentComments(establishmentId: String): ArrayList<Comment?>
}

class EstablishmentRepositoryImpl(
    private val establishmentServices: EstablishmentService = EstablishmentService(),
    private val commentServices: CommentService = CommentService(),
    private val customerServices: CustomerService = CustomerService(),
    private val workerServices: WorkerService = WorkerService()
) : EstablishmentRepository {
    override suspend fun loadEstablishmentList(): ArrayList<Establishment?> {
        val document = establishmentServices.loadEstablishmentList()
        val establishments = arrayListOf<Establishment?>()
        document.documents.forEach {
            val est = it.toObject(Establishment::class.java)
            establishments.add(est)
        }
        return establishments
    }

    override suspend fun loadEstablishmentWorkers(establishmentId: String): ArrayList<Worker?>{
        val document = establishmentServices.getEstablishmentById(establishmentId)
        val establishment = document.toObject(Establishment::class.java)
        val workers = arrayListOf<Worker?>()
        establishment?.workersRefs?.forEach{
            if(it!=""){
                val worker = workerServices.loadWorker(it).toObject(Worker::class.java)
                workers.add(worker)
            }
        }
        return workers
    }
    override suspend fun loadEstablishmentComments(establishmentId: String): ArrayList<Comment?>{
        val document = establishmentServices.getEstablishmentById(establishmentId)
        val establishment = document.toObject(Establishment::class.java)
        val comments = arrayListOf<Comment?>()
        establishment?.commentsRef?.forEach{
            if(it != ""){
                val comment = commentServices.loadComment(it).toObject(Comment::class.java)
                comments.add(comment)
            }
        }
        return comments
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
    override suspend fun addComment(comment: Comment){
        commentServices.addComment(comment)
        val addedComment = commentServices.loadComment(comment.id).toObject(Reservation::class.java)
        if(addedComment != null){
            establishmentServices.addComment(comment.establishmentRef,comment)
            workerServices.addComment(comment.workerRef,comment)
            customerServices.addComment(comment.customerRef,comment)
        }
    }

}