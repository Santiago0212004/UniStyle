package edu.co.icesi.unistyle.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import edu.co.icesi.unistyle.domain.model.AppAuthState
import edu.co.icesi.unistyle.domain.model.AuthStatus
import edu.co.icesi.unistyle.services.AuthServices

interface AuthRepository {
    suspend fun signup(email: String, pass: String): AppAuthState
    suspend fun login(email: String, pass: String): AppAuthState
}

class AuthRepositoryImpl(val service:AuthServices = AuthServices()):AuthRepository{
    override suspend fun signup(email:String, pass:String) : AppAuthState {
        try {
            val result = service.signUp(email, pass)
            result.user?.let {
                return AppAuthState.Success(it.uid)
            } ?: run {
                return AppAuthState.Error("Something went wrong")
            }
        }catch (ex: FirebaseAuthException){
            return AppAuthState.Error(ex.errorCode)
        }
    }

    override suspend fun login(email: String, pass: String) : AppAuthState{
        try {
            val result = service.logIn(email, pass)
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