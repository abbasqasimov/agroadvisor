package com.ram.agroadvisor.ui.theme

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

@HiltViewModel
class ThemeViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    private val PREFS_NAME = "agroadvisor_theme_v2"
    private val THEME_MODE_KEY = "theme_mode"

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private fun loadThemeMode(): ThemeMode {
        val savedMode = sharedPreferences.getString(THEME_MODE_KEY, ThemeMode.LIGHT.name)
        return try {
            ThemeMode.valueOf(savedMode ?: ThemeMode.LIGHT.name)
        } catch (e: IllegalArgumentException) {
            ThemeMode.LIGHT
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        sharedPreferences.edit().putString(THEME_MODE_KEY, mode.name).apply()
        _themeMode.value = mode
    }
}
