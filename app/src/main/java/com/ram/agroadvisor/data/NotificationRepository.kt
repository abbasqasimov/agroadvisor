package com.ram.agroadvisor.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class LocalNotification(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val time: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

object NotificationRepository {

    private const val PREFS_NAME = "notifications_prefs"
    private const val KEY_NOTIFICATIONS = "notifications_list"
    private const val MAX_NOTIFICATIONS = 20
    private val gson = Gson()

    fun saveNotification(context: Context, notification: LocalNotification) {
        val list = getNotifications(context).toMutableList()
        list.add(0, notification)
        if (list.size > MAX_NOTIFICATIONS) list.removeAt(list.size - 1)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NOTIFICATIONS, gson.toJson(list)).apply()
    }

    fun getNotifications(context: Context): List<LocalNotification> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_NOTIFICATIONS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<LocalNotification>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun markAllAsRead(context: Context) {
        val list = getNotifications(context).map { it.copy(isRead = true) }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NOTIFICATIONS, gson.toJson(list)).apply()
    }

    fun clearAll(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_NOTIFICATIONS).apply()
    }

    fun getUnreadCount(context: Context): Int {
        return getNotifications(context).count { !it.isRead }
    }
}