package com.arenamix.app.data

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    private const val PREF_NAME = "arenamix_prefs"
    private const val KEY_TOKEN    = "jwt_token"
    private const val KEY_USERNAME = "username"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_TOKEN, value).apply()

    var username: String?
        get() = prefs.getString(KEY_USERNAME, null)
        set(value) = prefs.edit().putString(KEY_USERNAME, value).apply()

    val isLoggedIn: Boolean get() = token != null

    fun logout() {
        prefs.edit().clear().apply()
    }
}
