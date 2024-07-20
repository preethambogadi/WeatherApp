package com.example.weather.model

/**
 * GeocodingResponse is a data class that represents the response from the geocoding API.
 */
data class GeocodingResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String?,
    val state: String?
)
