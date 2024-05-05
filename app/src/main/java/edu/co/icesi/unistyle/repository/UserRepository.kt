package edu.co.icesi.unistyle.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import edu.co.icesi.unistyle.domain.model.Customer
import edu.co.icesi.unistyle.services.CustomerService

interface UserRepository {
    suspend fun loadCustomer() : Customer?
    suspend fun findCustomerById(customerId: String) : Customer?
    fun observeUser(callback:(Customer)->Unit)
}

class UserRepositoryImpl(val customerServices: CustomerService = CustomerService()) : UserRepository{
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
}