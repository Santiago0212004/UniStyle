package edu.co.icesi.unistyle.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import edu.co.icesi.unistyle.domain.model.AppAuthState
import edu.co.icesi.unistyle.domain.model.AuthStatus
import edu.co.icesi.unistyle.domain.model.User
import edu.co.icesi.unistyle.domain.model.Worker
import edu.co.icesi.unistyle.services.AuthServices
import edu.co.icesi.unistyle.services.UserService

interface AuthRepository {
    suspend fun signupUser(user: User, pass: String): AppAuthState
    suspend fun signupWorker(worker: Worker, pass: String): AppAuthState
    suspend fun login(email: String, pass: String): AppAuthState
}

class AuthRepositoryImpl( val authServices: AuthServices = AuthServices(),
                          val userServices: UserService = UserService()) : AuthRepository {
    override suspend fun signupUser(user: User, pass: String): AppAuthState {
        try {
            val result = authServices.signUp(user.email, pass)
            result.user?.let {
                user.id = it.uid
                userServices.createUser(user)


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
                userServices.createWorker(worker)


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
            val result = authServices.logIn(email, pass)
            result.user?.let {
                return  AppAuthState.Success(it.uid)
            }?: run {
                return AppAuthState.Error("Something went wrong")
            }
        }catch (ex: FirebaseAuthException){
            return AppAuthState.Error(ex.errorCode)
        }
    }

}