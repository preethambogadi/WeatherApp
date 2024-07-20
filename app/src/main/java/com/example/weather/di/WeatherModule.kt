package com.example.weather.di

import android.content.Context
import com.example.weather.repo.WeatherApi
import com.example.weather.repo.WeatherApiClient
import com.example.weather.repo.WeatherRepo
import com.example.weather.repo.WeatherRepoImpl
import com.example.weather.utils.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {

    /**
     * Provides an implementation of the WeatherRepo interface as a singleton.
     * Hilt will create an instance of WeatherRepoImpl and use it wherever WeatherRepo is required.
     */
    @Singleton
    @Provides
    fun provideWeatherRepo(
        apiClient: WeatherApiClient,
        preferencesManager: PreferencesManager
    ): WeatherRepo {
        return WeatherRepoImpl(apiClient, preferencesManager)
    }

    /**
     * Provides an implementation of the PreferencesManager class as a singleton.
     */
    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    /**
     * Provides an implementation of the WeatherApi interface as a singleton.
     * Hilt uses the provided Retrofit instance to create an implementation of WeatherApi.
     */
    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)
}
