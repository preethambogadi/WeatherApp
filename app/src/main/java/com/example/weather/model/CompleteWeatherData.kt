package com.example.weather.model

/**
 * CompleteWeatherData is a data class that combines the weather response
 * and geocoding response into a single object.
 */
data class CompleteWeatherData(
    val weatherResponse: WeatherResponse,
    val geocodingResponse: GeocodingResponse
)