package com.example.unistylejc.domain.model

data class Service(
    var id: String = "",
    var name : String = "",
    var price: Double? = null ,
    var workersRefs: List<String>?= listOf()
)