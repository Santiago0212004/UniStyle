package com.example.unistylejc.domain.model

import java.lang.reflect.Array

data class Service(
    var name : String,
    var price: Double?,
    var workersRefs: Array?
)