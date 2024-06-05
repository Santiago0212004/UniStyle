package com.example.unistylejc.domain.model

import java.io.Serializable

data class Customer(
    var id:String = "",
    var email: String = "",
    var name: String = "",
    var username: String = "",
    var picture: String? = null,
    var reservationRefs: List<String> = listOf() ,
    var commentsRef:List<String> = listOf()
) : Serializable {
    override fun toString(): String {
        return name
    }
}