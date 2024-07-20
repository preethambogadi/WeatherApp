package com.example.weather.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weather.R
import com.example.weather.model.CompleteWeatherData
import com.example.weather.model.WeatherResponse
import com.example.weather.ui.viewmodel.WeatherViewModel
import com.example.weather.utils.Constants

/**
 * WeatherAppScreen is a composable function that will the main screen of the Weather app.
 * */
@Composable
fun WeatherAppScreen(viewModel: WeatherViewModel) {
    var cityName by remember { mutableStateOf("") } // State to hold the city name input by the user
    val uiState by viewModel.uiState.observeAsState() // Observes the UI state from the ViewModel
    var showSnackBar by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current // Access the current context

    Scaffold(
        scaffoldState = scaffoldState,
        content = { padding ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
                SearchBar(
                    cityName = cityName,
                    onCityNameChange = { cityName = it },
                    onSearch = {
                        if (cityName.isEmpty()) {
                            showSnackBar = true
                        } else {
                            viewModel.fetchWeather(cityName.trim())
                        }
                    }
                )

                // Handling different UI states: Loading, Success, and Error
                when (uiState) {
                    is WeatherViewModel.UiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    is WeatherViewModel.UiState.Success -> {
                        val completeWeatherData = (uiState as WeatherViewModel.UiState.Success).data
                        LazyColumn(// Display weather data in a LazyColumn
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            item {
                                WeatherDisplay(completeWeatherData)
                            }
                        }
                    }
                    is WeatherViewModel.UiState.Error -> {
                        Text(
                            text = (uiState as WeatherViewModel.UiState.Error).exception.message ?: stringResource(
                                R.string.unknown_error
                            ),
                            color = MaterialTheme.colors.error,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp)
                        )
                    }
                    else -> {}
                }
            }

            if (showSnackBar) {
                LaunchedEffect(scaffoldState.snackbarHostState) {
                    scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.enter_city_name))
                    showSnackBar = false
                }
            }
        }
    )
}

/**
 * SearchBar is a composable function that provides a text field for the user to enter a city name
 * and a search icon button to trigger the search action.
 */
@Composable
fun SearchBar(
    cityName: String,
    onCityNameChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    TextField(
        value = cityName,
        onValueChange = onCityNameChange,
        label = { Text(stringResource(R.string.enter_city_name_label)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        singleLine = true, // Ensures the text field is a single line
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_label))
            }
        }
    )
}

/**
 * WeatherDisplay is a composable function that displays the weather data in various cards.
 */
@Composable
fun WeatherDisplay(data: CompleteWeatherData) {
    Column(
        modifier = Modifier
            .fillMaxWidth() // Sets the column to fill the width of the parent
    ) {
        GeneralInfoCard(data)
        TemperatureCard(data.weatherResponse)
        WindCard(data.weatherResponse)
        AdditionalInfoCard(data.weatherResponse)
    }
}

/**
 * GeneralInfoCard is a composable function that will display general weather information in a card.
 */
@Composable
fun GeneralInfoCard(data: CompleteWeatherData) {
    val weatherResponse = data.weatherResponse
    val cityName = data.geocodingResponse.name ?: stringResource(R.string.unknown_country)
    val countryName = data.geocodingResponse.country ?: stringResource(R.string.unknown_country)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Image with a background color to stand out and to not blend with the background
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    .padding(8.dp)
            ) {
                val iconUrl = Constants.getIconUrl(weatherResponse.weather[0].icon)
                AsyncImage(
                    model = iconUrl,
                    contentDescription = "Weather icon",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.weather_in, cityName, countryName),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
            )

            data.geocodingResponse.state?.let {// Display state if available
                Text(
                    text = stringResource(R.string.state, it),
                    style = MaterialTheme.typography.body1.copy(color = Color.Gray)
                )
            }

            Text(
                text = stringResource(R.string.condition, weatherResponse.weather[0].description),
                style = MaterialTheme.typography.body1.copy(color = Color.Gray)
            )
        }
    }
}

/**
 * TemperatureCard is a composable function that displays temperature-related weather information in a card.
 *
 * @param weatherResponse The WeatherResponse object containing weather data.
 */
@Composable
fun TemperatureCard(weatherResponse: WeatherResponse) {
    val (tempCelsius, tempFahrenheit) = convertTemperature(weatherResponse.main.temp)
    val (feelsLikeCelsius, feelsLikeFahrenheit) = convertTemperature(weatherResponse.main.feels_like)
    val (tempMinCelsius, tempMinFahrenheit) = convertTemperature(weatherResponse.main.temp_min)
    val (tempMaxCelsius, tempMaxFahrenheit) = convertTemperature(weatherResponse.main.temp_max)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.temperature_details),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            WeatherInfoRow(
                label = stringResource(R.string.temperature),
                value = "$tempCelsius / $tempFahrenheit"
            )
            WeatherInfoRow(
                label = stringResource(R.string.feels_like),
                value = "$feelsLikeCelsius / $feelsLikeFahrenheit"
            )
            WeatherInfoRow(
                label = stringResource(R.string.min_temperature),
                value = "$tempMinCelsius / $tempMinFahrenheit"
            )
            WeatherInfoRow(
                label = stringResource(R.string.max_temperature),
                value = "$tempMaxCelsius / $tempMaxFahrenheit"
            )
            WeatherInfoRow(
                label = stringResource(R.string.humidity),
                value = "${weatherResponse.main.humidity}%"
            )
            WeatherInfoRow(
                label = stringResource(R.string.pressure),
                value = "${weatherResponse.main.pressure} hPa"
            )
        }
    }
}

/**
 * WindCard is a composable function that displays wind-related weather information in a card.
 */
@Composable
fun WindCard(weatherResponse: WeatherResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.wind_details),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            WeatherInfoRow(label = stringResource(R.string.wind), value = "${weatherResponse.wind.speed} m/s at ${weatherResponse.wind.deg}°")
            weatherResponse.wind.gust.let {
                WeatherInfoRow(label = stringResource(R.string.wind_gust), value = "$it m/s")
            }
        }
    }
}

/**
 * This is a composable function that displays additional weather information in a card.
 */
@Composable
fun AdditionalInfoCard(weatherResponse: WeatherResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.additional_information),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            WeatherInfoRow(label = stringResource(R.string.visibility), value = "${weatherResponse.visibility / 1000.0} km")
            WeatherInfoRow(label = stringResource(R.string.clouds), value = "${weatherResponse.clouds.all}%")
            weatherResponse.rain?.`1h`?.let {
                WeatherInfoRow(label = stringResource(R.string.rain), value = stringResource(
                    R.string.mm_in_the_last_hour,
                    it
                ))
            }

            Spacer(modifier = Modifier.height(8.dp))

            val sunriseTime = formatUnixTime(weatherResponse.sys.sunrise, weatherResponse.timezone)
            val sunsetTime = formatUnixTime(weatherResponse.sys.sunset, weatherResponse.timezone)
            WeatherInfoRow(label = stringResource(R.string.sunrise), value = sunriseTime)
            WeatherInfoRow(label = stringResource(R.string.sunset), value = sunsetTime)
        }
    }
}

/**
 * Displays a label and its corresponding value in a row.
 */
@Composable
fun WeatherInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
        Text(text = value, style = MaterialTheme.typography.body1.copy(color = Color.Gray))
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherDisplayPreview() {
    WeatherDisplay(data = sampleCompleteWeatherData)
}



