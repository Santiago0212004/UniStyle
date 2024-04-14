package edu.co.icesi.unistyle.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import edu.co.icesi.unistyle.domain.model.AuthStatus
import edu.co.icesi.unistyle.services.AuthServices

interface AuthRepository {

    suspend fun signup(email: String, pass: String) : Int
}

class AuthRepositoryImpl(val service: AuthServices = AuthServices()): AuthRepository {
    override suspend fun signup(email: String, pass: String) : Int {
        try {
            val result = service.signUp(email, pass)
            result.user?.let{
                return AuthStatus.SUCCESS
            } ?: run{
                return  AuthStatus.Error
            }
        }catch (ex: FirebaseAuthException){
            return  AuthStatus.Error
        }
    }
}