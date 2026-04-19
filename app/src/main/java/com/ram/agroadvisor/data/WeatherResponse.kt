package com.ram.agroadvisor.data.model

data class WeatherResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)

data class Location(
    val name: String,
    val country: String
)

data class Current(
    val temp_c: Double,
    val feelslike_c: Double,
    val humidity: Int,
    val wind_kph: Double,
    val vis_km: Double,
    val uv: Double,
    val precip_mm: Double,
    val cloud: Int,
    val condition: Condition
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day
)

data class Day(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val daily_chance_of_rain: Int,
    val condition: Condition,
    val uv: Double
)

data class SearchResult(
    val name: String,
    val region: String,
    val country: String
)