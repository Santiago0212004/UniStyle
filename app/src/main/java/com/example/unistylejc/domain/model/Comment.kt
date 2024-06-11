package com.example.unistylejc.domain.model

import com.google.firebase.Timestamp

data class Comment (
    var id: String = "",
    var content : String = "",
    var date : Timestamp? = null,
    var score: Double = 0.0,
    var customerRef : String = "",
    var workerRef: String = "",
    var establishmentRef: String = "",
    var responseRef: String? = ""
)
