package edu.co.icesi.unistyle.domain.model

import java.lang.reflect.Array

data class Establishment(
    var id : String = "",
    var address:String = "",
    var city: String = "",
    var email: String = "",
    var name: String = "",
    var username: String = "",
    var paymentMethod: Array? = null,
    var picture: String? = "",
    var postalcode: String = "",
    var state: String = "",
    var workersRef: Array? = null,
    var comments : Comments? = null,
    var service: Service? = null
)