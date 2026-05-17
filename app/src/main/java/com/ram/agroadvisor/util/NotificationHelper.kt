package com.ram.agroadvisor.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ram.agroadvisor.MainActivity

object NotificationHelper {

    private const val CHANNEL_ID = "weather_channel"
    private const val CHANNEL_NAME = "Hava Bildirişləri"
    private const val CHANNEL_DESC = "Hava məlumatı və kənd təsərrüfatı bildirişləri"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Builds a PendingIntent that re-launches MainActivity, brings any
     * existing task to the front (so we don't stack duplicates), and is
     * mutability-flagged for Android 12+ compliance.
     */
    private fun buildContentIntent(context: Context, requestCode: Int): PendingIntent {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            // FLAG_ACTIVITY_NEW_TASK is required when starting from a non-Activity context.
            // SINGLE_TOP + CLEAR_TOP avoid creating a second MainActivity if one is already
            // running — instead the existing instance is brought forward.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(context, requestCode, launchIntent, flags)
    }

    fun sendWeatherNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = 1001
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(buildContentIntent(context, notificationId))
            .setAutoCancel(true)
            .build()
        manager.notify(notificationId, notification)
    }

    fun sendCriticalWeatherAlert(
        context: Context,
        message: String,
        notificationId: Int = 1002
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Kritik Hava Xəbərdarlığı")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(buildContentIntent(context, notificationId))
            .setAutoCancel(true)
            .build()
        manager.notify(notificationId, notification)
    }

}
