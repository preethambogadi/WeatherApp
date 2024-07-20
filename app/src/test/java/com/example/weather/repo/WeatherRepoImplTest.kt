package com.example.weather.repo

import com.example.weather.model.CompleteWeatherData
import com.example.weather.utils.PreferencesManager
import com.example.weather.utils.exceptions.LocationNotFoundException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class WeatherRepoImplTest {

    @Mock
    private lateinit var apiClient: WeatherApiClient

    @Mock
    private lateinit var preferencesManager: PreferencesManager

    private lateinit var weatherRepoImpl: WeatherRepoImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        weatherRepoImpl = WeatherRepoImpl(apiClient, preferencesManager)
    }

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun `getWeatherByCityName with valid city should return CompleteWeatherData and save city name`() = runTest {
        val cityName = "Dallas"
        val completeWeatherData = mock(CompleteWeatherData::class.java)

        `when`(apiClient.getWeatherByCityName(cityName)).thenReturn(completeWeatherData)

        val result = weatherRepoImpl.getWeatherByCityName(cityName)

        assert(result == completeWeatherData)
        verify(apiClient).getWeatherByCityName(cityName)
        verify(preferencesManager).saveLastSearchedCity(cityName)
    }

    @Test
    fun `getWeatherByCoordinates with valid coordinates should return CompleteWeatherData`() = runTest {
        val latitude = 32.7767
        val longitude = -96.7970
        val completeWeatherData = mock(CompleteWeatherData::class.java)

        `when`(apiClient.getWeatherByCoordinates(latitude, longitude)).thenReturn(completeWeatherData)

        val result = weatherRepoImpl.getWeatherByCoordinates(latitude, longitude)

        assert(result == completeWeatherData)
        verify(apiClient).getWeatherByCoordinates(latitude, longitude)
    }

    @Test
    fun `getWeatherByCityName with exception should not save city name`() = runTest {
        val cityName = "InvalidCity"
        val exception = Exception("City not found")

        `when`(apiClient.getWeatherByCityName(cityName)).thenThrow(RuntimeException(exception))

        val thrown = try {
            weatherRepoImpl.getWeatherByCityName(cityName)
            null
        } catch (e: RuntimeException) {
            e
        }

        assert(thrown?.cause == exception)
        assert(thrown?.cause?.message == "City not found")
        verify(apiClient).getWeatherByCityName(cityName)
        verify(preferencesManager, never()).saveLastSearchedCity(cityName)
    }

    @Test
    fun `getWeatherByCoordinates with exception should throw exception`() = runTest {
        val latitude = 0.0
        val longitude = 0.0
        val exception = LocationNotFoundException("Location not found")

        `when`(apiClient.getWeatherByCoordinates(latitude, longitude)).thenThrow(RuntimeException(exception))

        val thrown = try {
            weatherRepoImpl.getWeatherByCoordinates(latitude, longitude)
            null
        } catch (e: RuntimeException) {
            e
        }

        assert(thrown?.cause == exception)
        verify(apiClient).getWeatherByCoordinates(latitude, longitude)
    }
}