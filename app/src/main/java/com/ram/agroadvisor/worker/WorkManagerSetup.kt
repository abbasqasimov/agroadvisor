package com.ram.agroadvisor.worker

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkManagerSetup {

    fun scheduleWeatherNotifications(context: Context) {
        scheduleMorningWeather(context)
        scheduleAfternoonWeather(context)
    }

    // Səhər 7:00
    private fun scheduleMorningWeather(context: Context) {
        val delay = getDelayUntilHour(7)
        val request = PeriodicWorkRequestBuilder<WeatherWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "morning_weather",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    // Günorta 13:00
    private fun scheduleAfternoonWeather(context: Context) {
        val delay = getDelayUntilHour(13)
        val request = PeriodicWorkRequestBuilder<WeatherWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "afternoon_weather",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun getDelayUntilHour(hour: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_MONTH, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    // Test üçün — dərhal işlət
    fun sendTestNotification(context: Context) {
        val request = OneTimeWorkRequestBuilder<WeatherWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }
}