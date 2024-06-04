package com.example.unistylejc.domain.model

import java.lang.reflect.Array

data class Establishment(
    var id : String = "",
    var address:String = "",
    var city: String = "",
    var email: String = "",
    var name: String = "",
    var username: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var paymentMethod: Array? = null,
    var picture: String? = "",
    var postalcode: String = "",
    var state: String = "",
    var workersRef: Array? = null,
    var comments : Comments? = null,
    var service: Service? = null
)