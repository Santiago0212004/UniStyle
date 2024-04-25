package edu.co.icesi.unistyle.domain.model

import java.io.Serializable
import java.lang.reflect.Array

data class User(
    var id:String,
    var email: String,
    var name: String,
    var username: String,
    var picture: String?,
    var reservationRefs: Array? ,
    var commentsRef:Array?
) : Serializable {
    override fun toString(): String {
        return name
    }
}