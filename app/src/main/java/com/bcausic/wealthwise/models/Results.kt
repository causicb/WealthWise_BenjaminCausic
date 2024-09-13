package com.bcausic.wealthwise.models

import com.google.firebase.Timestamp

data class Results(
    val Type: String,
    val Amount: String,
    val Description: String,
    val Year: String,
    val Month: String,
    var Day: String,
    val Hour: String,
    val Minute: String,
    val Second: String,
    val Timestamp: Timestamp
)
