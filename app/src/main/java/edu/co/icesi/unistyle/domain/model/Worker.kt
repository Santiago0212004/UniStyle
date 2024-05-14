package edu.co.icesi.unistyle.domain.model

import java.lang.reflect.Array

data class Worker(
    var id:String ="",
    var email: String="",
    var name: String="",
    var username: String="",
    var picture: String?="",
    var reservationRefs: Array?= null,
    var commentsRef: Array?= null,
    var establishmentRef: String?= null,
    var servicesRef: Array?= null
)
