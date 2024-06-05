package com.example.unistylejc.repository

import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.services.CustomerService
import com.example.unistylejc.services.WorkerService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

interface UserRepository {
    suspend fun loadCustomer() : Customer?
    suspend fun findCustomerById(customerId: String) : Customer?
    fun observeUser(callback:(Customer)->Unit)
    suspend fun loadWorker(): Worker?
    fun observeWorker(callback: (Worker) -> Unit)
    suspend fun updateProfileWorker(name: String, username: String)
}

class UserRepositoryImpl(val customerServices: CustomerService = CustomerService(), val workerServices : WorkerService = WorkerService()) :
    UserRepository {
    override suspend fun loadCustomer(): Customer? {
        val document = customerServices.loadCustomer(Firebase.auth.uid!!)
        return document.toObject(Customer::class.java)
    }

    override suspend fun findCustomerById(customerId: String): Customer? {
        return customerServices.loadCustomer(customerId).toObject(Customer::class.java)
    }

    override fun observeUser(callback: (Customer) -> Unit) {
        customerServices.observeUser(Firebase.auth.uid!!){ snapshot ->
            val customer = snapshot?.toObject(Customer::class.java)
            customer?.let{
                callback(it)
            }
        }
    }

    override suspend fun loadWorker(): Worker? {
        val document = workerServices.loadWorker(Firebase.auth.uid!!)
        val user = document.toObject(Worker::class.java)
        /*
        user?.profilePic?.let {
            user.profilePic = fileServices.downloadImage(it).toString()
        }
         */
        return user
    }

    override fun observeWorker(callback: (Worker) -> Unit) {
        workerServices.observeWorker(Firebase.auth.uid!!){ snapshot ->
            val worker = snapshot?.toObject(Worker::class.java)
            worker?.let{
                callback(it)
            }
        }
    }

    override suspend fun updateProfileWorker(name: String, username: String) {
        workerServices.updateProfile(name,username)
    }
}