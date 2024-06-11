package com.example.unistylejc.domain.model

import com.google.firebase.Timestamp

data class Reservation (
    var id: String = "",
    var customerId: String = "",
    var workerId: String = "",
    var serviceId: String = "",
    var establishmentId: String = "",
    var initDate: Timestamp? = null,
    var finishDate: Timestamp? = null,
    var paymentMethodId: String = ""
)