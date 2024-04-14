package edu.co.icesi.unistyle.domain.model

sealed class AppAuthState {
    data class Loading(val message : String): AppAuthState()
    data class Error(val message : String): AppAuthState()
    data class Success(val userID: String) : AppAuthState()

}