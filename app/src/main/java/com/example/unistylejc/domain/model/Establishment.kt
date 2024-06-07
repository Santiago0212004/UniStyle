package com.example.unistylejc.domain.model


data class Establishment(
    var id: String = "",
    var address:String = "",
    var city: String = "",
    var email: String = "",
    var name: String = "",
    var username: String = "",
    var description: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var picture: String? = "",
    var postalcode: String = "",
    var state: String = "",
    var workersRefs: List<String> = listOf<String>(),
    var commentsRef: List<String> = listOf<String>(),
    var photos: List<String> = listOf<String>(),
    var score: Double = 0.0
)