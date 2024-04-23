package edu.co.icesi.unistyle.domain.model

import java.lang.reflect.Array

data class Establishment(
    var address:String,
    var city: String,
    var email: String,
    var name: String,
    var username: String,
    var paymentMethod: Array?,
    var picture: String?,
    var postalcode: String,
    var state: String,
    var workersRef: Array?,
    var comments : Comments,
    var service: Service
)