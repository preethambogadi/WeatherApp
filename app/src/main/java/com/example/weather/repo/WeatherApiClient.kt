package com.example.weather.repo

import android.content.Context
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.model.CompleteWeatherData
import com.example.weather.model.GeocodingResponse
import com.example.weather.utils.ApiErrorHandler
import com.example.weather.utils.exceptions.LocationNotFoundException
import com.example.weather.utils.exceptions.MalformedDataException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * WeatherApiClient is a wrapper class over the Retrofit
 * interface WeatherApi and abstracts network calls
 */
class WeatherApiClient @Inject constructor(private val weatherApi: WeatherApi,
                                           @ApplicationContext private val context: Context) {

    /**
     * Fetches weather data for a city by its name.
     */
    suspend fun getWeatherByCityName(cityName: String): CompleteWeatherData {
        return try {
            val apiKey = BuildConfig.WEATHER_API_KEY
            val coordinatesResponse = weatherApi.getCoordinates(cityName, apiKey = apiKey)

            if (coordinatesResponse.isEmpty()) {
                throw LocationNotFoundException(context.getString(R.string.location_not_found, cityName))
            }

            val coordinates = coordinatesResponse.first()
            getWeatherData(coordinates.lat, coordinates.lon)
        } catch (e: Exception) {
            throw ApiErrorHandler.handleException(e, context)
        }
    }

    /**
     * Fetches weather data by geographic coordinates.
     * @return A CompleteWeatherData object containing both weather and geocoding data.
     */
    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double): CompleteWeatherData {
        return getWeatherData(latitude, longitude)
    }

    /**
     * Fetches weather data using latitude and longitude.
     */
    private suspend fun getWeatherData(latitude: Double, longitude: Double): CompleteWeatherData {
        val apiKey = BuildConfig.WEATHER_API_KEY
        try {
            val weatherResponse = weatherApi.getWeatherByCoordinates(latitude, longitude, apiKey)
            if (weatherResponse.name.isEmpty() || weatherResponse.main.temp.isNaN() || weatherResponse.weather.isEmpty()) {
                throw MalformedDataException(context.getString(R.string.malformed_data))
            }

            val geocodingResponse = GeocodingResponse(
                name = weatherResponse.name,
                lat = latitude,
                lon = longitude,
                country = weatherResponse.sys.country,
                state = null // Assuming we don't have state data from the weather API
            )

            return CompleteWeatherData(weatherResponse, geocodingResponse)
        } catch (e: Exception) {
            throw ApiErrorHandler.handleException(e, context)
        }
    }
}
