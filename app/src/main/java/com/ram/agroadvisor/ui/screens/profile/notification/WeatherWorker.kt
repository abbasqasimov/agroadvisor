package com.ram.agroadvisor.worker

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.ram.agroadvisor.data.RetrofitInstance
import com.ram.agroadvisor.util.NotificationHelper
import kotlinx.coroutines.tasks.await

class WeatherWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val apiKey = "40ab85541de6480e9aa205106252910"

    override suspend fun doWork(): Result {
        return try {
            NotificationHelper.createNotificationChannel(context)

            val location = getLocation()
            val query = if (location != null) {
                "${location.latitude},${location.longitude}"
            } else {
                "Baku"
            }

            val weather = RetrofitInstance.api.getForecast(
                apiKey = apiKey,
                city = query,
                days = 1
            )

            val temp = weather.current.temp_c
            val condition = weather.current.condition.text
            val wind = weather.current.wind_kph
            val rain = weather.forecast.forecastday.firstOrNull()?.day?.daily_chance_of_rain ?: 0
            val locationName = weather.location.name

            val criticalAlerts = mutableListOf<String>()
            if (rain > 70) criticalAlerts.add("🌧️ Yağış ehtimalı $rain%")
            if (wind > 40) criticalAlerts.add("💨 Güclü külək ${wind} km/h")
            if (temp < 0) criticalAlerts.add("🥶 Şaxta ${temp}°C")
            if (temp > 38) criticalAlerts.add("🌡️ Həddindən artıq isti ${temp}°C")

            if (criticalAlerts.isNotEmpty()) {
                NotificationHelper.sendCriticalWeatherAlert(
                    context = context,
                    message = "$locationName: ${criticalAlerts.joinToString(", ")}",
                    notificationId = 1002
                )
            }

            val agroTip = when {
                rain > 70 -> "Suvarma lazım deyil, yağış gözlənilir"
                wind > 20 -> "Çiləmə tövsiyə edilmir, külək güclüdür"
                temp > 35 -> "Sahə işlərini səhər tezdən aparın"
                else -> "Sahə işləri üçün uyğun gün"
            }

            NotificationHelper.sendWeatherNotification(
                context = context,
                title = "🌤️ $locationName — $temp°C, $condition",
                message = agroTip,
                notificationId = 1001
            )

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLocation(): Location? {
        return try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val token = CancellationTokenSource()
            client.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                token.token
            ).await()
        } catch (e: Exception) {
            null
        }
    }
}