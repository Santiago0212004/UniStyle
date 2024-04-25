package edu.co.icesi.unistyle.domain.model

import android.widget.DatePicker

data class Comments (
    var content : String,
    var customerRef : String?,
    var date : DatePicker?,
    var id: String,
    var score: Int,
    var workerRef: String?
)
