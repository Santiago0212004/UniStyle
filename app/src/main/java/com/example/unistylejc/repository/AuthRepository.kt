package com.example.unistylejc.repository


import com.example.unistylejc.domain.model.Customer
import com.example.unistylejc.domain.model.Worker
import com.example.unistylejc.services.AuthServices
import com.example.unistylejc.services.CustomerService
import com.example.unistylejc.services.WorkerService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import edu.co.icesi.unistyle.domain.model.AppAuthState

interface AuthRepository {
    suspend fun signupUser(customer: Customer, pass: String): AppAuthState
    suspend fun signupWorker(worker: Worker, pass: String): AppAuthState
    suspend fun login(email: String, pass: String): AppAuthState

    fun changePassword(newPassword: String)
}

class AuthRepositoryImpl(
    val authServices: AuthServices = AuthServices(),
    val customerServices: CustomerService = CustomerService(),
    val workerServices: WorkerService = WorkerService()
) : AuthRepository {
    override suspend fun signupUser(customer: Customer, pass: String): AppAuthState {
        try {
            val result = authServices.signUp(customer.email, pass)
            result.user?.let {
                customer.id = it.uid
                customerServices.createUser(customer)
                val role = authServices.checkRole(it.uid)
                return AppAuthState.SuccessLogin(it.uid, role)
            }?: run {
                return AppAuthState.Error("Something went wrong")
            }
        } catch (ex: FirebaseAuthException) {
            return AppAuthState.Error(ex.errorCode)
        }
    }

    override suspend fun signupWorker(worker: Worker, pass: String): AppAuthState {
        try {
            val result = authServices.signUp(worker.email, pass)
            result.user?.let {
                worker.id = it.uid
                workerServices.createWorker(worker)
                val role = authServices.checkRole(it.uid)
                return AppAuthState.SuccessLogin(it.uid, role)
            }?: run {
                return AppAuthState.Error("Something went wrong")
            }
        } catch (ex: FirebaseAuthException) {
            return AppAuthState.Error(ex.errorCode)
        }
    }

    override suspend fun login(email: String, pass: String) : AppAuthState{
        try {
            var role =""
            val result = authServices.logIn(email, pass)
            result.user?.let {
                role = authServices.checkRole(it.uid)
                return  AppAuthState.SuccessLogin(it.uid,role)
            }?: run {
                return AppAuthState.Error("Something went wrong")
            }
        }catch (ex: FirebaseAuthException){
            return AppAuthState.Error(ex.errorCode)
        }
    }

    override fun changePassword(newPassword: String) {
        authServices.changePassword(newPassword)
    }

}