package com.example.weather.ui

import com.example.weather.model.Clouds
import com.example.weather.model.CompleteWeatherData
import com.example.weather.model.Coord
import com.example.weather.model.GeocodingResponse
import com.example.weather.model.Main
import com.example.weather.model.Sys
import com.example.weather.model.Weather
import com.example.weather.model.WeatherResponse
import com.example.weather.model.Wind
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

val sampleWeatherResponse = WeatherResponse(
    coord = Coord(lon = -0.1257, lat = 51.5085),
    weather = listOf(Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
    base = "stations",
    main = Main(temp = 15.0, feels_like = 14.0, temp_min = 13.0, temp_max = 17.0, pressure = 1012, humidity = 76, sea_level = null, grnd_level = null),
    visibility = 10000,
    wind = Wind(speed = 3.6, deg = 350, gust = 7.2),
    rain = null,
    clouds = Clouds(all = 0),
    dt = 1605182400,
    sys = Sys(type = 1, id = 1414, country = "GB", sunrise = 1605166710, sunset = 1605200085),
    timezone = 0,
    id = 2643743,
    name = "London",
    cod = 200
)

val sampleGeocodingResponse = GeocodingResponse(
    name = "London",
    lat = 51.5085,
    lon = -0.1257,
    country = "GB",
    state = "England"
)

val sampleCompleteWeatherData = CompleteWeatherData(
    weatherResponse = sampleWeatherResponse,
    geocodingResponse = sampleGeocodingResponse
)

fun convertTemperature(kelvin: Double): Pair<String, String> {
    val celsius = kelvin - 273.15
    val fahrenheit = celsius * 9 / 5 + 32
    return Pair(String.format("%.2f°C", celsius), String.format("%.2f°F", fahrenheit))
}

/**
 * Converts Unix time to a formatted time string.
 *
 * @param unixTime The Unix time to be converted.
 * @param timezoneOffset The timezone offset in seconds.
 * @return A formatted time string in "hh:mm a" format.
 */
fun formatUnixTime(unixTime: Long, timezoneOffset: Int): String {
    val date = Date((unixTime + timezoneOffset) * 1000L)
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(date)
}

