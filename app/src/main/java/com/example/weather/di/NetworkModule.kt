package com.example.weather.di

import com.example.weather.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides a HttpLoggingInterceptor object as a singleton.
     * Used for logging network requests and responses, particularly in debug mode.
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return loggingInterceptor
    }

    /**
     * Provides an OkHttpClient object as a singleton.
     * Configured with a logging interceptor to aid in debugging and appropriate timeout settings.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor(loggingInterceptor)
        clientBuilder.readTimeout(30, TimeUnit.SECONDS)
        clientBuilder.connectTimeout(30, TimeUnit.SECONDS)
        return clientBuilder.build()
    }

    /**
     * Provides a Retrofit object as a singleton.
     * Configured for use with the OpenWeatherMap API, using Gson for JSON deserialization.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
            .build()
    }
}
