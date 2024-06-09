package com.example.unistylejc.domain.model

import com.google.firebase.Timestamp

data class CommentEntity (
    var id: String = "",
    var content : String = "",
    var date : Timestamp? = null,
    var score: Double = 0.0,
    var customer: Customer? = null,
    var workerRef: String = "",
    var establishmentRef: String = "",
    var responseEntity: ResponseEntity? = null
)
