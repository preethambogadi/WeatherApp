package com.example.weather.repo

import com.example.weather.model.CompleteWeatherData

/**
 * `WeatherRepo` is an interface that abstracts the underlying data source of weather data.
 * This allows us to have a separation of concerns in our codebase,
 * making it more maintainable and scalable.
 */
interface WeatherRepo {
    /**
     * Fetches weather data for a city by its name.
     *
     * This function is a suspend function, meaning it is designed to be called from a coroutine
     * for long-running tasks such as network requests. Any class that implements `WeatherRepo`
     * needs to provide an implementation for this method.
     */
    suspend fun getWeatherByCityName(cityName: String): CompleteWeatherData

    /**
     * Fetches weather data by geographic coordinates.
     */
    suspend fun getWeatherByCoordinates(latitude: Double, longitude: Double): CompleteWeatherData
}
