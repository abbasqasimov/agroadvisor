package com.ram.agroadvisor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ram.agroadvisor.ui.navigation.NavGraph
import com.ram.agroadvisor.ui.theme.AgroAdvisorTheme
import com.ram.agroadvisor.ui.theme.ThemeMode
import com.ram.agroadvisor.ui.theme.ThemeViewModel
import com.ram.agroadvisor.ui.theme.ThemeViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(applicationContext)
            )
            val themeMode by themeViewModel.themeMode.collectAsState()
            val systemInDarkTheme = isSystemInDarkTheme()

            val isDarkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemInDarkTheme
            }

            AgroAdvisorTheme(darkTheme = isDarkTheme) {
                NavGraph(
                    themeMode = themeMode,
                    onThemeChange = { mode -> themeViewModel.setThemeMode(mode) }
                )
            }
        }
    }
}