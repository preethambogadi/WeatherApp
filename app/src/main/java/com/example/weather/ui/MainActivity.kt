package com.example.weather.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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

        Log.d("MainActivity", "Permissions result: fineLocationGranted=$fineLocationGranted, coarseLocationGranted=$coarseLocationGranted")

        when {
            fineLocationGranted -> {
                Log.d("MainActivity", "Fine location permission granted")
                getLocationCoordinates()
            }
            coarseLocationGranted -> {
                Log.d("MainActivity", "Coarse location permission granted")
                getLocationCoordinates()
            }
            else -> {
                Log.d("MainActivity", "Location permissions denied")
                showPermissionDeniedMessage()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")

        // The setContent method is part of Jetpack Compose. It sets the UI for the activity using
        // the provided composable function.
        setContent {
            WeatherAppScreen(viewModel)
        }

        useSavedCityOrRequestPermission()
    }

    private fun useSavedCityOrRequestPermission() {
        lifecycleScope.launch {
            preferencesManager.lastSearchedCity.collect { city ->
                if (city.isNotEmpty()) {
                    Log.d("MainActivity", "Using last searched city: $city")
                    viewModel.fetchWeather(city)
                } else {
                    Log.d("MainActivity", "No saved city found, requesting location permission")
                    checkAndRequestLocationPermission()
                }
            }
        }
    }

    private fun checkAndRequestLocationPermission() {
        Log.d("MainActivity", "Checking location permissions")

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Log.d("MainActivity", "Fine location permission already granted")
                getLocationCoordinates()
            }
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                Log.d("MainActivity", "Coarse location permission already granted")
                getLocationCoordinates()
            }
            else -> {
                Log.d("MainActivity", "Requesting location permissions")
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun getLocationCoordinates() {
        Log.d("MainActivity", "Getting last known location")

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Location permissions not granted, cannot get location")
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                location?.let {
                    Log.d("MainActivity", "Location obtained: latitude=${it.latitude}, longitude=${it.longitude}, accuracy=${it.accuracy}")
                    viewModel.fetchWeatherByLocation(it.latitude, it.longitude)
                } ?: run {
                    Log.d("MainActivity", "Location is null")
                    showLocationErrorMessage()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Failed to get location: ${exception.message}")
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
