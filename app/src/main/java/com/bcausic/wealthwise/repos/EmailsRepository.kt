package com.bcausic.wealthwise.repos

import com.bcausic.wealthwise.data.room.EmailsDao
import com.bcausic.wealthwise.models.Emails

class EmailsRepository(private val emailsDao: EmailsDao) {
    suspend fun insertEmail(email: String) {
        emailsDao.insert(Emails(email))
    }

    suspend fun getAllEmails(): List<String> {
        return emailsDao.getAll()
    }

    suspend fun deleteAll() {
        emailsDao.deleteAll()
    }
}