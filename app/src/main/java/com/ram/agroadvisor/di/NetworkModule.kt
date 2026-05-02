package com.ram.agroadvisor.di

import com.ram.agroadvisor.data.WeatherApi
import com.ram.agroadvisor.data.local.AuthInterceptor
import com.ram.agroadvisor.data.remote.AgroApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val AGRO_BASE_URL = "https://agroadvisor-api-14f883.azurewebsites.net/"
    private const val WEATHER_BASE_URL = "https://api.weatherapi.com/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    /** Plain client for unauthenticated 3rd-party APIs (e.g. weather). */
    @Provides
    @Singleton
    @Named("weather")
    fun provideWeatherOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    /** Authenticated client for the AgroAdvisor backend (adds Bearer token). */
    @Provides
    @Singleton
    @Named("agro")
    fun provideAgroOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            // Image analysis can take a while server-side.
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @Named("weather")
    fun provideWeatherRetrofit(@Named("weather") okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("agro")
    fun provideAgroRetrofit(@Named("agro") okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(AGRO_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideWeatherApi(@Named("weather") retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)

    @Provides
    @Singleton
    fun provideAgroApi(@Named("agro") retrofit: Retrofit): AgroApi =
        retrofit.create(AgroApi::class.java)
}
