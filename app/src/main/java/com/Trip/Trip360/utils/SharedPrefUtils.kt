package com.Trip.Trip360.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

object SharedPrefUtils {

    private const val PREF_NAME = "ClientDataPref"

    // Keys for SharedPreferences
    const val KEY_COLOR = "color"
    private const val KEY_EMAIL = "email"
    const val KEY_LOGO_URL = "logoURL"
    const val KEY_LOGO_LOCAL_FILE_PATH = "logoLocalFilePath"
    private const val KEY_PASSWORD = "password"
    const val KEY_PHONE_NO = "phoneNo"
    private const val KEY_USER_ID = "userId"
    private const val KEY_USERNAME = "username"
    const val KEY_WEB_NAME = "webName"
    const val KEY_WEB_URL = "webURL"
    const val USER_LOGGED_IN = "userLoggedIn"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun <T> getValue(context: Context, key: String, defaultValue: T): T {
        val sharedPreferences = getSharedPreferences(context)
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue) as T
            is Long -> sharedPreferences.getLong(key, defaultValue) as T
            else -> throw IllegalArgumentException("Unsupported data type")
        }
    }

    fun storeClientData(
        context: Context,
        color: String?,
        email: String?,
        logoURL: String?,
        password: String?,
        phoneNo: String?,
        userId: String,
        username: String?,
        webName: String?,
        webURL: String?,
        userLoggedIn: Boolean
    ) {
        val editor = getSharedPreferences(context).edit()

        editor.putString(KEY_COLOR, color)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_LOGO_URL, logoURL)
        editor.putString(KEY_PASSWORD, password)
        editor.putString(KEY_PHONE_NO, phoneNo)
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_WEB_NAME, webName)
        editor.putString(KEY_WEB_URL, webURL)
        editor.putBoolean(USER_LOGGED_IN, userLoggedIn)
        editor.apply()
    }

    fun storeLogoFileRef(path: String, context: Context) {
        getSharedPreferences(context).edit().putString(KEY_LOGO_LOCAL_FILE_PATH, path).apply()
    }

}