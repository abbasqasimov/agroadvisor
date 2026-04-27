package com.ram.agroadvisor.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class LocalNotification(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val time: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Singleton
class NotificationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()
    private val prefs get() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveNotification(notification: LocalNotification) {
        val list = getNotifications().toMutableList()
        list.add(0, notification)
        if (list.size > MAX_NOTIFICATIONS) list.removeAt(list.size - 1)
        prefs.edit().putString(KEY_NOTIFICATIONS, gson.toJson(list)).apply()
    }

    fun getNotifications(): List<LocalNotification> {
        val json = prefs.getString(KEY_NOTIFICATIONS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<LocalNotification>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun markAllAsRead() {
        val list = getNotifications().map { it.copy(isRead = true) }
        prefs.edit().putString(KEY_NOTIFICATIONS, gson.toJson(list)).apply()
    }

    fun clearAll() = prefs.edit().remove(KEY_NOTIFICATIONS).apply()

    fun getUnreadCount(): Int = getNotifications().count { !it.isRead }

    companion object {
        private const val PREFS_NAME = "notifications_prefs"
        private const val KEY_NOTIFICATIONS = "notifications_list"
        private const val MAX_NOTIFICATIONS = 20
    }
}
