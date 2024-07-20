package com.example.weather.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * PreferencesManager is responsible for managing user preferences using DataStore.
 * It provides methods to save and retrieve the last searched city.
 */
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val Context.dataStore by preferencesDataStore(name = PreferenceKeys.DATA_STORE_NAME)
    private val dataStore = context.dataStore

    // A Flow that emits the last searched city from the preferences.
    val lastSearchedCity: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.LAST_SEARCHED_CITY_KEY] ?: ""
        }

    // Saves the last searched city to the preferences.
    suspend fun saveLastSearchedCity(city: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_SEARCHED_CITY_KEY] = city
        }
    }
}

