package com.bcausic.wealthwise.data.sharedprefs

import android.content.Context
import com.bcausic.wealthwise.WealthWiseApp
import com.bcausic.wealthwise.helpers.EMPTY_STRING
import com.bcausic.wealthwise.helpers.SHARED_PREFS_NAME
import com.bcausic.wealthwise.helpers.USER_ID

class SharedPrefsManager {
    private val sharedPrefs = WealthWiseApp.application.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    private val userEditor = sharedPrefs.edit()

    fun saveUserId(id: String) {
        userEditor.putString(USER_ID, id)
        userEditor.apply()
    }

    fun getUserId() = sharedPrefs.getString(USER_ID, EMPTY_STRING)
}