package com.example.unistylejc.repository

import android.net.Uri
import android.util.Log
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Establishment
import com.example.unistylejc.domain.model.PaymentMethod
import com.example.unistylejc.domain.model.Reservation
import com.example.unistylejc.domain.model.ReservationEntity
import com.example.unistylejc.domain.model.Service
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.services.CustomerService
import com.example.unistylejc.services.EstablishmentService
import com.example.unistylejc.services.FileService
import com.example.unistylejc.services.ReservationService
import com.example.unistylejc.services.WorkerService
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface UserRepository {
    suspend fun loadCustomer(): Customer?
    suspend fun findCustomerById(customerId: String): Customer?
    fun observeUser(callback: (Customer) -> Unit)
    suspend fun loadWorker(): Worker?

    suspend fun loadPaymentMethods(): List<PaymentMethod>
    suspend fun findWorkerById(workerId: String): Worker?
    fun observeWorker(callback: (Worker) -> Unit)
    suspend fun updateProfileWorker(name: String, username: String)
    suspend fun uploadProfilePicture(uri: Uri): Uri?
    suspend fun updateProfilePictureUrl(userId: String, url: String, isWorker: Boolean): Boolean
    suspend fun createReservation(reservation: Reservation)
    suspend fun loadWorkerServices(serviceIds: List<String>): List<Service>

    suspend fun getCustomerReservations(customerId: String): List<Reservation>
    suspend fun getCustomerReservationsPastFuture(customerId: String): Pair<List<ReservationEntity>, List<ReservationEntity>>
    suspend fun getWorkerReservations(workerId: String): Pair<List<ReservationEntity>, List<ReservationEntity>>
    suspend fun deleteAccount(email: String, pass: String,id:String)
    suspend fun updateProfileCustomer(name: String, username: String)

    suspend fun loadAllWorkerReservations(workerId: String): List<Reservation>
}

class UserRepositoryImpl(
    private val customerServices: CustomerService = CustomerService(),
    private val workerServices: WorkerService = WorkerService(),
    private val fileService: FileService = FileService(),
    private val reservationServices: ReservationService = ReservationService(),
    private val establishmentServices: EstablishmentService = EstablishmentService()
) : UserRepository {
    override suspend fun loadCustomer(): Customer? {
        val document = customerServices.loadCustomer(Firebase.auth.uid!!)
        return document.toObject(Customer::class.java)
    }

    override suspend fun findCustomerById(customerId: String): Customer? {
        return customerServices.loadCustomer(customerId).toObject(Customer::class.java)
    }

    override fun observeUser(callback: (Customer) -> Unit) {
        customerServices.observeUser(Firebase.auth.uid!!) { snapshot ->
            val customer = snapshot?.toObject(Customer::class.java)
            customer?.let {
                callback(it)
            }
        }
    }

    override suspend fun loadWorker(): Worker? {
        val document = workerServices.loadWorker(Firebase.auth.uid!!)
        return document.toObject(Worker::class.java)
    }

    override suspend fun loadPaymentMethods(): List<PaymentMethod> {
        return reservationServices.loadPaymentMethods().toObjects(PaymentMethod::class.java)
    }

    override suspend fun loadWorkerServices(serviceIds: List<String>): List<Service> {
        return workerServices.loadWorkerServices(serviceIds)
    }

    override fun observeWorker(callback: (Worker) -> Unit) {
        workerServices.observeWorker(Firebase.auth.uid!!) { snapshot ->
            val worker = snapshot?.toObject(Worker::class.java)
            worker?.let {
                callback(it)
            }
        }
    }

    override suspend fun updateProfileWorker(name: String, username: String) {
        workerServices.updateProfile(name,username)
    }

    override suspend fun uploadProfilePicture(uri: Uri): Uri? {
        val userId = Firebase.auth.uid ?: return null
        return fileService.uploadProfilePicture(uri, userId)
    }

    override suspend fun updateProfilePictureUrl(userId: String, url: String, isWorker: Boolean): Boolean {
        return try {
            if (isWorker) {
                workerServices.updateProfilePicture(userId, url)
            } else {
                customerServices.updateProfilePicture(userId, url)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun findWorkerById(workerId: String): Worker? {
        val document = workerServices.loadWorker(workerId)
        return document.toObject(Worker::class.java)
    }

    override suspend fun createReservation(reservation: Reservation){
        reservationServices.createReservation(reservation)
        val createdReservation = reservationServices.loadReservation(reservation.id).toObject(Reservation::class.java)
        if(createdReservation != null){
            workerServices.addReservation(reservation.workerId, reservation.id)
            customerServices.addReservation(reservation.customerId, reservation.id)
            establishmentServices.addReservation(reservation.establishmentId,reservation.id)
        }
    }

    override suspend fun getCustomerReservations(customerId: String): List<Reservation> {
        val customer = customerServices.loadCustomer(customerId)
        val customerEntity = customer.toObject(Customer::class.java)
        val reservations = mutableListOf<Reservation>()
        customerEntity?.reservationRefs?.forEach { reservationId ->
            reservationServices.loadReservation(reservationId).let {
                it.toObject(Reservation::class.java)?.let { it1 -> reservations.add(it1) }
            }
        }
        return reservations
    }

    override suspend fun loadAllWorkerReservations(workerId: String): List<Reservation> {
        val worker = workerServices.loadWorker(workerId)
        val customerEntity = worker.toObject(Worker::class.java)
        val reservations = mutableListOf<Reservation>()
        customerEntity?.reservationRefs?.forEach { reservationId ->
            reservationServices.loadReservation(reservationId).let {
                it.toObject(Reservation::class.java)?.let { it1 -> reservations.add(it1) }
            }
        }
        return reservations
    }

    override suspend fun getCustomerReservationsPastFuture(customerId: String): Pair<List<ReservationEntity>, List<ReservationEntity>> {
        val customer = customerServices.loadCustomer(customerId)
        val customerEntity = customer.toObject(Customer::class.java)
        val reservations = mutableListOf<Reservation>()
        customerEntity?.reservationRefs?.forEach { reservationId ->
            reservationServices.loadReservation(reservationId).let {
                it.toObject(Reservation::class.java)?.let { it1 -> reservations.add(it1) }
            }
        }

        val reservationsEntities = mutableListOf<ReservationEntity>()
        reservations.forEach{
            val reservationEntity = ReservationEntity()
            val worker =   findWorkerById(it.workerId)
            worker?.let{ w ->
                reservationEntity.worker=w
            }
            reservationEntity.initDate = it.initDate
            val establishment =establishmentServices.getEstablishmentById(it.establishmentId)
            establishment.let { e ->
                reservationEntity.establishment = e.toObject(Establishment::class.java)
            }
            val service =  workerServices.loadService(it.serviceId)
            service?.let{ s ->
                reservationEntity.service = s.toObject(Service::class.java)
            }
            val paymentMethod=reservationServices.loadPaymentMethod(it.paymentMethodId)
            paymentMethod.let { p ->
                reservationEntity.paymentMethod = p.toObject(PaymentMethod::class.java)
            }

            reservationsEntities.add(reservationEntity)
            Log.e(">>>>", reservationEntity.toString())
        }
        val actualTime= Timestamp.now()
        val pastReservations: List<ReservationEntity> = reservationsEntities.filter { it.initDate!! < actualTime }
        val futureReservations: List<ReservationEntity> = reservationsEntities.filter { it.initDate!! >= actualTime }
        val sortedPastReservations: List<ReservationEntity> = pastReservations.sortedByDescending { it.initDate }
        val sortedFutureReservations: List<ReservationEntity> = futureReservations.sortedBy { it.initDate }

        return Pair(sortedPastReservations, sortedFutureReservations)
    }

    override suspend fun getWorkerReservations(workerId: String): Pair<List<ReservationEntity>, List<ReservationEntity>> {
        val worker = workerServices.loadWorker(workerId)
        val workerEntity = worker.toObject(Worker::class.java)
        val reservations = mutableListOf<Reservation>()

        workerEntity?.reservationRefs?.forEach { reservationId ->
            reservationServices.loadReservation(reservationId).let {
                it.toObject(Reservation::class.java)?.let { it1 -> reservations.add(it1) }
            }
        }
        val reservationsEntities = mutableListOf<ReservationEntity>()
        reservations.forEach{
            val reservationEntity = ReservationEntity()
            val customer =   findCustomerById(it.customerId)
            customer?.let{ c ->
                reservationEntity.client=c
            }
            reservationEntity.initDate = it.initDate
            val establishment =establishmentServices.getEstablishmentById(it.establishmentId)
            establishment.let { e ->
                reservationEntity.establishment = e.toObject(Establishment::class.java)
            }
            val service =  workerServices.loadService(it.serviceId)
            service?.let{ s ->
                reservationEntity.service = s.toObject(Service::class.java)
            }
            val paymentMethod=reservationServices.loadPaymentMethod(it.paymentMethodId)
            paymentMethod.let { p ->
                reservationEntity.paymentMethod = p.toObject(PaymentMethod::class.java)
            }

            reservationsEntities.add(reservationEntity)
            Log.e(">>>>", reservationEntity.toString())
        }



        val actualTime= Timestamp.now()
        val pastReservations: List<ReservationEntity> = reservationsEntities.filter { it.initDate!! < actualTime }
        val futureReservations: List<ReservationEntity> = reservationsEntities.filter { it.initDate!! >= actualTime }
        val sortedPastReservations: List<ReservationEntity> = pastReservations.sortedByDescending { it.initDate }
        val sortedFutureReservations: List<ReservationEntity> = futureReservations.sortedBy { it.initDate }

        return Pair(sortedPastReservations, sortedFutureReservations)
    }


    override suspend fun deleteAccount(email: String, pass: String,id:String) {
        customerServices.deleteAccount(email,pass,id)
    }
    override suspend fun updateProfileCustomer(name: String, username: String) {
        customerServices.updateProfile(name,username)
    }
}