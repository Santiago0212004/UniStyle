package com.example.unistylejc.domain.model

import java.io.Serializable

data class Customer(
    var id:String = "",
    var email: String = "",
    var name: String = "",
    var username: String = "",
    var picture: String? = "https://firebasestorage.googleapis.com/v0/b/unistyle-940e2.appspot.com/o/user.png?alt=media&token=1b93b86f-5718-4a71-8fe8-e7f5dd198a72",
    var reservationRefs: List<String> = listOf<String>(),
    var commentsRef:List<String> = listOf<String>()
) : Serializable {
    override fun toString(): String {
        return name
    }
}