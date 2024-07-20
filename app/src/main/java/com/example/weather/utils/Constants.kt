package com.example.weather.utils

object Constants {
    const val BASE_URL = "https://api.openweathermap.org/"
    private const val ICON_BASE_URL = "https://openweathermap.org/img/wn/"

    /**
     * Generates the full URL for a weather icon based on the icon code.
     */
    fun getIconUrl(iconCode: String): String {
        return "${ICON_BASE_URL}${iconCode}@2x.png"
    }
}
