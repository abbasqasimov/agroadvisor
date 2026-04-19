package com.ram.agroadvisor.ui.screens.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.ram.agroadvisor.data.RetrofitInstance
import com.ram.agroadvisor.data.model.AgroRecommendation
import com.ram.agroadvisor.data.model.AgroStatus
import com.ram.agroadvisor.data.model.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val data: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

class WeatherViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions

    private val apiKey = "40ab85541de6480e9aa205106252910"

    fun fetchWeatherByLocation(context: Context) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val location = getCurrentLocation(context)
                if (location != null) {
                    val query = "${location.latitude},${location.longitude}"
                    val response = RetrofitInstance.api.getForecast(
                        apiKey = apiKey,
                        city = query,
                        days = 14
                    )
                    _uiState.value = WeatherUiState.Success(response)
                } else {
                    fetchWeather("Baku")
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "Xəta baş verdi")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(context: Context): Location? {
        return try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val cancellationToken = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).await()
        } catch (e: Exception) {
            null
        }
    }

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val response = RetrofitInstance.api.getForecast(
                    apiKey = apiKey,
                    city = city,
                    days = 14
                )
                _uiState.value = WeatherUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "Xəta baş verdi")
            }
        }
    }

    fun searchSuggestions(query: String) {
        if (query.length < 2) {
            _suggestions.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.searchCities(
                    apiKey = apiKey,
                    query = query
                )
                _suggestions.value = response.map { "${it.name}, ${it.country}" }
            } catch (e: Exception) {
                _suggestions.value = emptyList()
            }
        }
    }

    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }

    fun getAgriculturalRecommendations(data: WeatherResponse): List<AgroRecommendation> {
        val temp = data.current.temp_c
        val rain = data.forecast.forecastday.firstOrNull()?.day?.daily_chance_of_rain ?: 0
        val wind = data.current.wind_kph
        val humidity = data.current.humidity
        val uv = data.current.uv

        return listOf(
            AgroRecommendation(
                title = "Çiləmə (Pestisid)",
                emoji = "🌿",
                status = when {
                    wind > 20 -> AgroStatus.BAD
                    rain > 50 -> AgroStatus.BAD
                    temp > 35 -> AgroStatus.WARNING
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    wind > 20 -> "Külək çox güclüdür (${wind} km/h)"
                    rain > 50 -> "Yağış ehtimalı yüksəkdir ($rain%)"
                    temp > 35 -> "Həddindən artıq isti, səhər tezdən edin"
                    else -> "İdeal şərait"
                }
            ),
            AgroRecommendation(
                title = "Suvarma",
                emoji = "💧",
                status = when {
                    rain > 70 -> AgroStatus.BAD
                    rain > 40 -> AgroStatus.WARNING
                    humidity > 80 -> AgroStatus.WARNING
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    rain > 70 -> "Yağış gözlənilir, suvarma lazım deyil"
                    rain > 40 -> "Yağış ehtimalı var, gözləyin"
                    humidity > 80 -> "Rütubət yüksəkdir"
                    else -> "Suvarma tövsiyə olunur"
                }
            ),
            AgroRecommendation(
                title = "Gübrələmə",
                emoji = "🌱",
                status = when {
                    rain > 60 -> AgroStatus.BAD
                    wind > 25 -> AgroStatus.BAD
                    temp < 5 -> AgroStatus.BAD
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    rain > 60 -> "Yağış gübrəni yuyacaq"
                    wind > 25 -> "Külək gübrəni sovuracaq"
                    temp < 5 -> "Soyuqda bitki gübrəni mənimsəmir"
                    else -> "Gübrələmə üçün uyğun gün"
                }
            ),
            AgroRecommendation(
                title = "Məhsul Yığımı",
                emoji = "🌾",
                status = when {
                    rain > 50 -> AgroStatus.BAD
                    wind > 30 -> AgroStatus.WARNING
                    temp in 15.0..30.0 -> AgroStatus.GOOD
                    else -> AgroStatus.WARNING
                },
                reason = when {
                    rain > 50 -> "Yağışlı havada yığım olmaz"
                    wind > 30 -> "Güclü külək var, diqqətli olun"
                    temp in 15.0..30.0 -> "İdeal temperatur"
                    else -> "Temperatur optimal deyil"
                }
            ),
            AgroRecommendation(
                title = "Şum / Torpaq işləri",
                emoji = "🚜",
                status = when {
                    rain > 60 -> AgroStatus.BAD
                    humidity > 85 -> AgroStatus.WARNING
                    temp < 0 -> AgroStatus.BAD
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    rain > 60 -> "Torpaq çox yaş olacaq"
                    humidity > 85 -> "Torpaq nəmdir, çətin işlənər"
                    temp < 0 -> "Torpaq donmuşdur"
                    else -> "Torpaq işləri üçün uyğundur"
                }
            ),
            AgroRecommendation(
                title = "Budama",
                emoji = "✂️",
                status = when {
                    rain > 40 -> AgroStatus.WARNING
                    wind > 20 -> AgroStatus.WARNING
                    temp < 0 -> AgroStatus.BAD
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    rain > 40 -> "Rütubətli havada xəstəlik riski"
                    wind > 20 -> "Külək var, diqqətli olun"
                    temp < 0 -> "Şaxtada budama olmaz"
                    else -> "Budama üçün ideal şərait"
                }
            ),
            AgroRecommendation(
                title = "Toxum Səpini",
                emoji = "🌰",
                status = when {
                    temp < 8 -> AgroStatus.BAD
                    temp > 35 -> AgroStatus.BAD
                    rain > 70 -> AgroStatus.WARNING
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    temp < 8 -> "Temperatur çox aşağıdır ($temp°C)"
                    temp > 35 -> "Temperatur çox yüksəkdir ($temp°C)"
                    rain > 70 -> "Çox yağış gözlənilir"
                    else -> "Səpin üçün uyğun şərait"
                }
            )
        )
    }
}