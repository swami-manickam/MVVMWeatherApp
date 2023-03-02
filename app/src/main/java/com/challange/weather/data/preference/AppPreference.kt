package com.challange.weather.data.preference

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class AppPreference @Inject constructor(context: Context) {

    companion object {
        private const val PREFERENCE_NAME = "WEATHER_APP_PREF"
        private const val USERNAME = "USERNAME"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun clearAppPreference() {
        preferences.edit().clear().apply()
    }

    var userName: String
        set(value) = preferences.edit().putString(USERNAME, value).apply()
        get() = preferences.getString(USERNAME, "") ?: ""


    fun clearLoggedShopDetails() {
        preferences.edit().remove(userName).apply()
    }

}