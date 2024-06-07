package com.example.unistylejc.domain.model


data class Worker(
    var id:String ="",
    var email: String="",
    var name: String="",
    var username: String="",
    var description: String="",
    var picture: String?=null,
    var reservationRefs: List<String> = listOf<String>(),
    var commentsRef: List<String> = listOf<String>(),
    var establishmentRef: String = "",
    var servicesRef: List<String> = listOf<String>(),
    var score: Double = 0.0
)
