package com.example.weather.repo

import com.example.weather.model.CompleteWeatherData
import com.example.weather.utils.PreferencesManager
import javax.inject.Inject

/**
 * `WeatherRepoImpl` is an implementation of the `WeatherRepo` interface.
 * This class abstracts the source of data for the rest of the application.
 * Currently, it fetches the data from the `WeatherApiClient`.
 * In the future, this class could also handle data from other sources (like a local database).
 */
class WeatherRepoImpl @Inject constructor(
    private val apiClient: WeatherApiClient,
    private val preferencesManager: PreferencesManager
) : WeatherRepo {

    /**
     * This function communicates with the API client to fetch the weather data.
     */
    override suspend fun getWeatherByCityName(cityName: String): CompleteWeatherData {
        val completeWeatherData = apiClient.getWeatherByCityName(cityName)
        preferencesManager.saveLastSearchedCity(cityName)
        return completeWeatherData
    }

    /**
     * Fetches weather data by geographic coordinates.
     */
    override suspend fun getWeatherByCoordinates(
        latitude: Double,
        longitude: Double
    ): CompleteWeatherData {
        return apiClient.getWeatherByCoordinates(latitude, longitude)
    }
}
