package com.example.unistylejc.domain.model

import java.io.Serializable

data class Customer(
    var id:String = "",
    var email: String = "",
    var name: String = "",
    var username: String = "",
    var picture: String? = null,
    var reservationRefs: List<String> = listOf<String>(),
    var commentsRef:List<String> = listOf<String>()
) : Serializable {
    override fun toString(): String {
        return name
    }
}