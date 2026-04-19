package com.ram.agroadvisor.data

import com.ram.agroadvisor.data.model.SearchResult
import com.ram.agroadvisor.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") city: String,
        @Query("days") days: Int = 14,
        @Query("aqi") aqi: String = "no"
    ): WeatherResponse

    @GET("v1/search.json")
    suspend fun searchCities(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): List<SearchResult>
}