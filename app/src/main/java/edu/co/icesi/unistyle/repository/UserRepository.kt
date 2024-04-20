package edu.co.icesi.unistyle.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import edu.co.icesi.unistyle.domain.model.User
import edu.co.icesi.unistyle.services.UserService

interface UserRepository {
    suspend fun loadUser() : User?
    fun observeUser(callback:(User)->Unit)
}

class UserRepositoryImpl(
    val userServices: UserService = UserService()) : UserRepository{
    override suspend fun loadUser(): User? {
        val document = userServices.loadUser(Firebase.auth.uid!!)
        //Document -> Obj
        val user = document.toObject(User::class.java)
        return user
    }
    override fun observeUser(callback: (User) -> Unit) {
        userServices.observeUser(Firebase.auth.uid!!){ snapshot ->
            val user = snapshot?.toObject(User::class.java)
            user?.let{
                callback(it)
            }
        }
    }
}