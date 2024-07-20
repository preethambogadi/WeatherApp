package com.example.weather.utils

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    const val DATA_STORE_NAME = "settings"
    val LAST_SEARCHED_CITY_KEY = stringPreferencesKey("last_searched_city")
}