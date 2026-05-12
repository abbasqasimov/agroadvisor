package com.ram.agroadvisor.ui.screens.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.ram.agroadvisor.data.WeatherApi
import com.ram.agroadvisor.data.local.LocalNotification
import com.ram.agroadvisor.data.local.NotificationRepository
import com.ram.agroadvisor.data.model.AgroRecommendation
import com.ram.agroadvisor.data.model.AgroStatus
import com.ram.agroadvisor.data.model.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val data: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherApi: WeatherApi,
    private val notificationRepository: NotificationRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions

    private val apiKey = "40ab85541de6480e9aa205106252910"

    // Notification delegation
    fun getNotifications(): List<LocalNotification> = notificationRepository.getNotifications()
    fun markAllAsRead() = notificationRepository.markAllAsRead()
    fun clearAllNotifications() = notificationRepository.clearAll()
    fun getUnreadCount(): Int = notificationRepository.getUnreadCount()

    fun fetchWeatherByLocation() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val location = getCurrentLocation()
                if (location != null) {
                    val query = "${location.latitude},${location.longitude}"
                    val response = weatherApi.getForecast(
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
    private suspend fun getCurrentLocation(): Location? {
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
                val response = weatherApi.getForecast(
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
                val response = weatherApi.searchCities(
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
        val current = data.current
        val today = data.forecast.forecastday.firstOrNull()?.day
        val tomorrow = data.forecast.forecastday.getOrNull(1)?.day

        // Today / now signals.
        val temp = current.temp_c
        val feels = current.feelslike_c
        val wind = current.wind_kph
        val humidity = current.humidity
        val uv = current.uv
        val vis = current.vis_km
        val cloud = current.cloud
        val precipNow = current.precip_mm
        val rainToday = today?.daily_chance_of_rain ?: 0

        // Tomorrow signals (fall back to today where missing).
        val rainTomorrow = tomorrow?.daily_chance_of_rain ?: rainToday
        val minTempTomorrow = tomorrow?.mintemp_c ?: temp
        val uvTomorrow = tomorrow?.uv ?: uv

        return listOf(
            // -------- Existing recommendations, enriched with extra signals --------
            AgroRecommendation(
                title = "Çiləmə (Pestisid)",
                emoji = "🌿",
                status = when {
                    wind > 20 -> AgroStatus.BAD
                    rainToday > 50 || precipNow > 0.2 -> AgroStatus.BAD
                    vis < 2.0 -> AgroStatus.BAD
                    temp > 32 || uv > 8 -> AgroStatus.WARNING
                    rainTomorrow > 60 -> AgroStatus.WARNING
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    wind > 20 -> "Külək çox güclüdür (${wind.toInt()} km/s)"
                    precipNow > 0.2 -> "Hazırda yağış var (${precipNow} mm)"
                    rainToday > 50 -> "Bu gün yağış ehtimalı yüksəkdir (%${rainToday})"
                    vis < 2.0 -> "Görmə məsafəsi azdır (${vis} km), duman/sis riski"
                    temp > 32 -> "İsti hava (${temp.toInt()}°C), səhər tezdən etmək məsləhətdir"
                    uv > 8 -> "UV çox yüksəkdir ($uv), pestisid tez parçalanır"
                    rainTomorrow > 60 -> "Sabah yağış var (%${rainTomorrow}), effekt azalacaq"
                    else -> "İdeal şərait — sakit, quru hava"
                }
            ),
            AgroRecommendation(
                title = "Suvarma",
                emoji = "💧",
                status = when {
                    rainToday > 70 || rainTomorrow > 80 -> AgroStatus.BAD
                    rainToday > 40 -> AgroStatus.WARNING
                    humidity > 80 -> AgroStatus.WARNING
                    feels >= 32 -> AgroStatus.WARNING
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    rainToday > 70 -> "Yağış gözlənilir (%${rainToday}), suvarma lazım deyil"
                    rainTomorrow > 80 -> "Sabah güclü yağış olacaq, suvarmanı təxirə salın"
                    rainToday > 40 -> "Yağış ehtimalı var (%${rainToday}), gözləyin"
                    humidity > 80 -> "Rütubət yüksəkdir (%${humidity})"
                    feels >= 32 -> "Hissi temperatur ${feels.toInt()}°C — daha tez-tez sulayın"
                    else -> "Suvarma tövsiyə olunur"
                }
            ),
            AgroRecommendation(
                title = "Gübrələmə",
                emoji = "🌱",
                status = when {
                    rainToday > 60 || precipNow > 0.1 -> AgroStatus.BAD
                    wind > 25 -> AgroStatus.BAD
                    temp < 5 -> AgroStatus.BAD
                    rainTomorrow in 30..60 -> AgroStatus.WARNING
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    precipNow > 0.1 -> "Hazırda yağır — gübrə yuyulacaq"
                    rainToday > 60 -> "Yağış gübrəni yuyacaq (%${rainToday})"
                    wind > 25 -> "Külək (${wind.toInt()} km/s) gübrəni sovuracaq"
                    temp < 5 -> "Soyuqda (${temp.toInt()}°C) bitki gübrəni mənimsəmir"
                    rainTomorrow in 30..60 -> "Sabah qismən yağış var, gübrəni torpağa qarışdırın"
                    else -> "Gübrələmə üçün uyğun gün"
                }
            ),
            AgroRecommendation(
                title = "Məhsul Yığımı",
                emoji = "🌾",
                status = when {
                    rainToday > 50 || precipNow > 0.1 -> AgroStatus.BAD
                    rainTomorrow > 70 -> AgroStatus.WARNING
                    wind > 30 -> AgroStatus.WARNING
                    humidity > 85 -> AgroStatus.WARNING
                    temp in 15.0..30.0 -> AgroStatus.GOOD
                    else -> AgroStatus.WARNING
                },
                reason = when {
                    precipNow > 0.1 -> "Hazırda yağır, yığımı dayandırın"
                    rainToday > 50 -> "Yağışlı havada yığım olmaz (%${rainToday})"
                    rainTomorrow > 70 -> "Sabah yağış var — bu gün yığmağa çalışın"
                    wind > 30 -> "Güclü külək (${wind.toInt()} km/s), diqqətli olun"
                    humidity > 85 -> "Rütubət %${humidity} — məhsulu yaxşı qurudun"
                    temp in 15.0..30.0 -> "İdeal temperatur (${temp.toInt()}°C)"
                    else -> "Temperatur optimal deyil (${temp.toInt()}°C)"
                }
            ),
            AgroRecommendation(
                title = "Şum / Torpaq işləri",
                emoji = "🚜",
                status = when {
                    rainToday > 60 || precipNow > 0.1 -> AgroStatus.BAD
                    temp < 0 || minTempTomorrow < -2 -> AgroStatus.BAD
                    humidity > 85 -> AgroStatus.WARNING
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    precipNow > 0.1 -> "Yağış davam edir, torpaq çox yaş"
                    rainToday > 60 -> "Torpaq çox yaş olacaq (%${rainToday})"
                    temp < 0 -> "Torpaq donmuşdur (${temp.toInt()}°C)"
                    minTempTomorrow < -2 -> "Sabah şaxta gözlənilir (${minTempTomorrow.toInt()}°C)"
                    humidity > 85 -> "Torpaq nəmdir (%${humidity}), çətin işlənər"
                    else -> "Torpaq işləri üçün uyğundur"
                }
            ),
            AgroRecommendation(
                title = "Budama",
                emoji = "✂️",
                status = when {
                    temp < 0 -> AgroStatus.BAD
                    rainToday > 40 || humidity > 85 -> AgroStatus.WARNING
                    wind > 20 -> AgroStatus.WARNING
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    temp < 0 -> "Şaxtada (${temp.toInt()}°C) budama olmaz"
                    rainToday > 40 -> "Rütubətli havada xəstəlik riski (%${rainToday})"
                    humidity > 85 -> "Rütubət %${humidity} — yaralar gec sağalır"
                    wind > 20 -> "Külək var (${wind.toInt()} km/s), diqqətli olun"
                    else -> "Budama üçün ideal şərait"
                }
            ),
            AgroRecommendation(
                title = "Toxum Səpini",
                emoji = "🌰",
                status = when {
                    temp < 8 || minTempTomorrow < 5 -> AgroStatus.BAD
                    temp > 35 -> AgroStatus.BAD
                    rainToday > 70 -> AgroStatus.WARNING
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    temp < 8 -> "Temperatur çox aşağıdır (${temp.toInt()}°C)"
                    minTempTomorrow < 5 -> "Sabah gecə soyuyacaq (${minTempTomorrow.toInt()}°C)"
                    temp > 35 -> "Temperatur çox yüksəkdir (${temp.toInt()}°C)"
                    rainToday > 70 -> "Çox yağış gözlənilir (%${rainToday})"
                    else -> "Səpin üçün uyğun şərait"
                }
            ),

            // -------- New recommendations powered by previously-ignored fields --------
            AgroRecommendation(
                title = "UV Qoruma",
                emoji = "☀️",
                status = when {
                    uv >= 11 || uvTomorrow >= 11 -> AgroStatus.BAD
                    uv >= 8 -> AgroStatus.WARNING
                    uv >= 6 -> AgroStatus.WARNING
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    uv >= 11 -> "Ekstrem UV ($uv) — meyvə günəş yanığı riski"
                    uvTomorrow >= 11 -> "Sabah ekstrem UV gözlənilir ($uvTomorrow)"
                    uv >= 8 -> "Çox yüksək UV ($uv) — gənc bitkiləri kölgələyin"
                    uv >= 6 -> "Yüksək UV ($uv) — işçilər papaq taxsın"
                    else -> "UV təhlükəsizdir ($uv)"
                }
            ),
            AgroRecommendation(
                title = "Don Riski (Sabah)",
                emoji = "❄️",
                status = when {
                    minTempTomorrow <= -2 -> AgroStatus.BAD
                    minTempTomorrow <= 2 -> AgroStatus.WARNING
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    minTempTomorrow <= -2 ->
                        "Sabah gecə ${minTempTomorrow.toInt()}°C — həssas bitkiləri örtün"
                    minTempTomorrow <= 2 ->
                        "Sabah gecə ${minTempTomorrow.toInt()}°C — yüngül don ehtimalı"
                    else -> "Don riski yoxdur (min ${minTempTomorrow.toInt()}°C)"
                }
            ),
            AgroRecommendation(
                title = "İstilik Stresi",
                emoji = "🥵",
                status = when {
                    feels >= 38 -> AgroStatus.BAD
                    feels >= 32 && humidity >= 60 -> AgroStatus.BAD
                    feels >= 30 -> AgroStatus.WARNING
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    feels >= 38 ->
                        "Hissi temperatur ${feels.toInt()}°C — bitki və işçilər üçün təhlükəli"
                    feels >= 32 && humidity >= 60 ->
                        "${feels.toInt()}°C + %${humidity} rütubət — yüksək stres"
                    feels >= 30 ->
                        "${feels.toInt()}°C — bitkiləri sulayın, kölgə təmin edin"
                    else -> "Temperatur rahatdır (${feels.toInt()}°C)"
                }
            ),
            AgroRecommendation(
                title = "Açıq Hava işləri",
                emoji = "👨‍🌾",
                status = when {
                    vis < 1.0 -> AgroStatus.BAD
                    vis < 3.0 -> AgroStatus.WARNING
                    wind > 40 -> AgroStatus.BAD
                    feels < -5 || feels >= 38 -> AgroStatus.BAD
                    cloud < 20 && uv >= 8 -> AgroStatus.WARNING
                    else -> AgroStatus.GOOD
                },
                reason = when {
                    vis < 1.0 -> "Görmə məsafəsi çox azdır (${vis} km)"
                    vis < 3.0 -> "Duman var (${vis} km) — diqqətli olun"
                    wind > 40 -> "Fırtına küləyi (${wind.toInt()} km/s)"
                    feels < -5 -> "Şaxta (${feels.toInt()}°C) — işi qısa saxlayın"
                    feels >= 38 -> "İsti vurma riski (${feels.toInt()}°C)"
                    cloud < 20 && uv >= 8 -> "Buludsuz və UV $uv — uzun müddət açıqda olmayın"
                    else -> "Sahə işləri üçün rahat şərait"
                }
            )
        )
    }
}
