package com.example.unistylejc.domain.model

import java.io.Serializable
import java.lang.reflect.Array

data class Customer(
    var id:String = "",
    var email: String = "",
    var name: String = "",
    var username: String = "",
    var picture: String? = null,
    var reservationRefs: Array? = null ,
    var commentsRef:Array? = null
) : Serializable {
    override fun toString(): String {
        return name
    }
}