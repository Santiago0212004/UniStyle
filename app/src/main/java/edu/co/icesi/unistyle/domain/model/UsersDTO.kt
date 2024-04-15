package edu.co.icesi.unistyle.domain.model

import java.io.Serializable
import java.lang.reflect.Array


data class UsersDTO(
    var id:String = "",
    var email: String = "",
    var password: String = ""
) : Serializable {
    override fun toString(): String {
        return id
    }
}