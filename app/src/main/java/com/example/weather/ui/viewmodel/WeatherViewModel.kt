package com.example.weather.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.CompleteWeatherData
import com.example.weather.repo.WeatherRepo
import com.example.weather.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepo
) : ViewModel() {

    // The _uiState MutableLiveData object represents the UI state and is private,
    // as we only want to expose an immutable version of this state to observers
    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    /**
     * fetchWeather fetches the weather data from the repository and updates the UI state accordingly
     */
    fun fetchWeather(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.postValue(UiState.Loading) // shows loading spinner
            try {
                val completeWeatherData = repository.getWeatherByCityName(cityName)
                _uiState.postValue(UiState.Success(completeWeatherData))
            } catch (e: Exception) {
                _uiState.postValue(UiState.Error(e)) // shows the error
            }
        }
    }


    /**
     * Fetches the weather data from the repository by geographic coordinates and updates the UI state accordingly.
     *
     * @param latitude The latitude of the location
     * @param longitude The longitude of the location
     */
    fun fetchWeatherByLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.postValue(UiState.Loading)
            try {
                val completeWeatherData = repository.getWeatherByCoordinates(latitude, longitude)
                _uiState.postValue(UiState.Success(completeWeatherData))
            } catch (e: Exception) {
                _uiState.postValue(UiState.Error(e))
            }
        }
    }

    /**
     * UiState represents the different possible states that the UI can be in
     */
    sealed class UiState {
        // Loading indicates that the data is currently being fetched
        object Loading : UiState()
        // Success indicates that the data has been successfully fetched
        data class Success(val data: CompleteWeatherData) : UiState()
        // Error indicates that an error occurred while fetching the data
        data class Error(val exception: Throwable) : UiState()
    }
}

