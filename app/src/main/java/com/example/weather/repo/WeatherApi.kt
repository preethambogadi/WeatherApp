package com.example.weather.repo

import com.example.weather.model.GeocodingResponse
import com.example.weather.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * This interface defines the Retrofit HTTP operations for the Weather API.
 * Retrofit will create the implementation of this interface.
 */
interface WeatherApi {
    /**
     * This function fetches the weather data for a specific city.
     */
    @GET("data/2.5/weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): WeatherResponse

    /**
     * This function fetches the coordinates of a specific city based on a query string.
     */
    @GET("geo/1.0/direct")
    suspend fun getCoordinates(
        @Query("q") query: String,
        @Query("limit") limit: Int = 1, // max limit is 5 (will get up to 5 different locations on the same city name)
        @Query("appid") apiKey: String
    ): List<GeocodingResponse>
}
