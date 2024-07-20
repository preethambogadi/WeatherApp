package com.example.weather.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.weather.model.CompleteWeatherData
import com.example.weather.repo.WeatherRepo
import com.example.weather.utils.LocationNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor


@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: WeatherRepo

    @Mock
    private lateinit var observer: Observer<WeatherViewModel.UiState>

    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherViewModel(repository)
        viewModel.uiState.observeForever(observer)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun `fetchWeather with valid city name should update UiState to Success`() = runTest {
        val completeWeatherData = mock(CompleteWeatherData::class.java)
        `when`(repository.getWeatherByCityName("Dallas")).thenReturn(completeWeatherData)

        viewModel.fetchWeather("Dallas")

        advanceUntilIdle() // Wait for coroutine to finish

        verify(observer).onChanged(WeatherViewModel.UiState.Loading)
        verify(observer).onChanged(WeatherViewModel.UiState.Success(completeWeatherData))
    }

    @Test
    fun `fetchWeather with invalid city name should update UiState to Error`() = runTest {
        val exception = LocationNotFoundException("City not found")
        doAnswer { throw exception }.`when`(repository).getWeatherByCityName(any())

        viewModel.fetchWeather("12345")

        advanceUntilIdle()

        val inOrder = inOrder(observer)
        inOrder.verify(observer).onChanged(WeatherViewModel.UiState.Loading)
        val errorStateCaptor = argumentCaptor<WeatherViewModel.UiState.Error>()
        inOrder.verify(observer).onChanged(errorStateCaptor.capture())
        assert(errorStateCaptor.firstValue.exception === exception)
    }

    @Test
    fun `fetchWeatherByLocation with valid coordinates should update UiState to Success`() = runTest {
        val completeWeatherData = mock(CompleteWeatherData::class.java)
        `when`(repository.getWeatherByCoordinates(any(), any())).thenReturn(completeWeatherData)

        viewModel.fetchWeatherByLocation(32.7767, -96.7970)

        advanceUntilIdle()

        verify(observer).onChanged(WeatherViewModel.UiState.Loading)
        verify(observer).onChanged(WeatherViewModel.UiState.Success(completeWeatherData))
    }

    @Test
    fun `fetchWeatherByLocation with invalid coordinates should update UiState to Error`() = runTest {
        val exception = LocationNotFoundException("Location not found")
        doAnswer { throw exception }.`when`(repository).getWeatherByCoordinates(any(), any())

        viewModel.fetchWeatherByLocation(0.0, 0.0)

        advanceUntilIdle()

        val inOrder = inOrder(observer)
        inOrder.verify(observer).onChanged(WeatherViewModel.UiState.Loading)
        val errorStateCaptor = argumentCaptor<WeatherViewModel.UiState.Error>()
        inOrder.verify(observer).onChanged(errorStateCaptor.capture())
        assert(errorStateCaptor.firstValue.exception === exception)

    }
}