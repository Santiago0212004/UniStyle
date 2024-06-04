package com.example.unistylejc.domain.model


data class Establishment(
    var id: String = "",
    var address:String = "",
    var city: String = "",
    var email: String = "",
    var name: String = "",
    var username: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var paymentMethod: List<String> = listOf<String>(),
    var picture: String? = "",
    var postalcode: String = "",
    var state: String = "",
    var workersRefs: List<String> = listOf<String>(),
    var comments: Comments? = null,
    var service: Service? = null
)