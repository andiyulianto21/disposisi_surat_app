package com.daylantern.arsipsuratpembinaan

import android.content.SharedPreferences
import javax.inject.Inject

class SessionManager @Inject constructor(private val sharedPref: SharedPreferences) {

    companion object {
        const val USER_TOKEN = "user_token"
    }

    fun saveAuthToken(token: String){
        val editor = sharedPref.edit()
        editor.apply {
            putString(USER_TOKEN, token)
            apply()
        }
    }

    fun fetchAuthToken(): String? {
        return sharedPref.getString(USER_TOKEN, null)
    }

}