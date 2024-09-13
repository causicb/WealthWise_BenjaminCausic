package com.bcausic.wealthwise.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emails")
data class Emails(
    @PrimaryKey
    val email: String
)
