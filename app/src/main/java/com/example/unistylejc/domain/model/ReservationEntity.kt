package com.example.unistylejc.domain.model

import com.google.firebase.Timestamp

data class ReservationEntity (
    var id: String = "",
    var worker: Worker? = null,
    var client: Customer? = null,
    var service: Service?= null,
    var establishment: Establishment?= null,
    var initDate: Timestamp?= null,
    var paymentMethod: PaymentMethod?= null
)