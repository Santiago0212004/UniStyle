package com.example.unistylejc.repository

import android.net.Uri
import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.PaymentMethod
import com.example.unistylejc.domain.model.Reservation
import com.example.unistylejc.domain.model.Service
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.services.CustomerService
import com.example.unistylejc.services.FileService
import com.example.unistylejc.services.ReservationService
import com.example.unistylejc.services.WorkerService
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
    suspend fun uploadProfilePicture(uri: Uri): Uri?
    suspend fun updateProfilePictureUrl(userId: String, url: String, isWorker: Boolean): Boolean
    suspend fun createReservation(reservation: Reservation)
    suspend fun loadWorkerServices(serviceIds: List<String>): List<Service>

}

class UserRepositoryImpl(
    private val customerServices: CustomerService = CustomerService(),
    private val workerServices: WorkerService = WorkerService(),
    private val fileService: FileService = FileService(),
    private val reservationServices: ReservationService = ReservationService()
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
        }
    }


}
