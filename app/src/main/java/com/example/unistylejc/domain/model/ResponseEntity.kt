package com.example.unistylejc.domain.model

data class ResponseEntity (
    var commentRef: String= "",
    var commenterW: Worker? = null,
    var content : String = "",
)