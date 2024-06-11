package com.example.unistylejc.domain.model

import java.io.Serializable


data class CustomerDTO(
    var id:String = "",
    var email: String = "",
) : Serializable {
    override fun toString(): String {
        return id
    }
}