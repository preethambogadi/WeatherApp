package com.example.weather.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.weather.R
import com.example.weather.ui.viewmodel.WeatherViewModel
import com.example.weather.utils.PreferencesManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // The viewModel is initialized using the viewModels delegate
    // This delegate creates the ViewModel and handles its lifecycle according to the activity's lifecycle.
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
        val coarseLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

        when {
            fineLocationGranted -> {
                getLocationCoordinates()
            }
            coarseLocationGranted -> {
                getLocationCoordinates()
            }
            else -> {
                showPermissionDeniedMessage()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the UI for the activity using the provided composable function.
        setContent {
            WeatherAppScreen(viewModel)
        }

        useSavedCityOrRequestPermission()
    }

    /**
     * Uses the last saved city to fetch weather or requests location permission if no city is saved.
     */
    private fun useSavedCityOrRequestPermission() {
        lifecycleScope.launch {
            preferencesManager.lastSearchedCity.collect { city ->
                if (city.isNotEmpty()) {
                    viewModel.fetchWeather(city)
                } else {
                    checkAndRequestLocationPermission()
                }
            }
        }
    }

    /**
     * Checks if location permissions are granted. If not, requests the necessary permissions.
     */
    private fun checkAndRequestLocationPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                getLocationCoordinates()
            }
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                getLocationCoordinates()
            }
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    /**
     * Gets the current location coordinates if permissions are granted.
     */
    private fun getLocationCoordinates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                location?.let {
                    viewModel.fetchWeatherByLocation(it.latitude, it.longitude)
                } ?: run {
                    showLocationErrorMessage()
                }
            }
            .addOnFailureListener { exception ->
                showLocationErrorMessage()
            }
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(this, getString(R.string.show_permission_denied_message), Toast.LENGTH_LONG).show()
    }

    private fun showLocationErrorMessage() {
        Toast.makeText(this, getString(R.string.show_location_error_message), Toast.LENGTH_LONG).show()
    }
}
