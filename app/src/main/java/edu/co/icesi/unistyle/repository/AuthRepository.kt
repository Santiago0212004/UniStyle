package edu.co.icesi.unistyle.repository


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import edu.co.icesi.unistyle.domain.model.AppAuthState
import edu.co.icesi.unistyle.domain.model.Customer
import edu.co.icesi.unistyle.domain.model.Worker
import edu.co.icesi.unistyle.services.AuthServices
import edu.co.icesi.unistyle.services.CustomerService
import edu.co.icesi.unistyle.services.WorkerService

interface AuthRepository {
    suspend fun signupUser(customer: Customer, pass: String): AppAuthState
    suspend fun signupWorker(worker: Worker, pass: String): AppAuthState
    suspend fun login(email: String, pass: String): AppAuthState

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


                return AppAuthState.Success(it.uid)
            } ?: run {
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


                return AppAuthState.Success(it.uid)
            } ?: run {
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

}