package com.ram.agroadvisor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ram.agroadvisor.data.local.TokenManager
import com.ram.agroadvisor.ui.navigation.NavGraph
import com.ram.agroadvisor.ui.navigation.Screen
import com.ram.agroadvisor.ui.theme.AgroAdvisorTheme
import com.ram.agroadvisor.ui.theme.ThemeMode
import com.ram.agroadvisor.ui.theme.ThemeViewModel
import com.ram.agroadvisor.ui.theme.ThemeViewModelFactory
import com.ram.agroadvisor.util.NotificationHelper
import com.ram.agroadvisor.worker.WorkManagerSetup

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationHelper.createNotificationChannel(this)
            WorkManagerSetup.scheduleWeatherNotifications(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationHelper.createNotificationChannel(this)
                WorkManagerSetup.scheduleWeatherNotifications(this)
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            NotificationHelper.createNotificationChannel(this)
            WorkManagerSetup.scheduleWeatherNotifications(this)
        }

        // Token varsa birbaşa Main-ə get
        val startDestination = if (TokenManager.isLoggedIn(this)) {
            Screen.Main.route
        } else {
            Screen.Welcome.route
        }

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
                    startDestination = startDestination,
                    themeMode = themeMode,
                    onThemeChange = { mode -> themeViewModel.setThemeMode(mode) }
                )
            }
        }
    }
}