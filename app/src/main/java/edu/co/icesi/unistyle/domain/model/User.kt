package edu.co.icesi.unistyle.domain.model

import java.lang.reflect.Array

data class User(
    var id:String,
    var email: String,
    var name: String,
    var username: String,
    var password: String,
    var picture: String?,
    var reservationRefs: Array?,
    var commentsRef:Array?
)
