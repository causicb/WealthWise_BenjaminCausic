package com.bcausic.wealthwise.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bcausic.wealthwise.models.Emails

@Database(entities = [Emails::class], version = 1, exportSchema = false)
abstract class EmailsDatabase: RoomDatabase() {
    abstract fun emailsDao(): EmailsDao
}