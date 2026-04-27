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
import androidx.hilt.navigation.compose.hiltViewModel
import com.ram.agroadvisor.data.local.TokenManager
import com.ram.agroadvisor.ui.navigation.AppNavigation
import com.ram.agroadvisor.ui.navigation.Screen
import com.ram.agroadvisor.ui.theme.AgroAdvisorTheme
import com.ram.agroadvisor.ui.theme.ThemeMode
import com.ram.agroadvisor.ui.theme.ThemeViewModel
import com.ram.agroadvisor.util.NotificationHelper
import com.ram.agroadvisor.worker.WorkManagerSetup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

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

        val startDestination = if (tokenManager.isLoggedIn()) {
            Screen.Home.route
        } else {
            Screen.Welcome.route
        }

        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeMode by themeViewModel.themeMode.collectAsState()
            val systemInDarkTheme = isSystemInDarkTheme()

            val isDarkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemInDarkTheme
            }

            AgroAdvisorTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    startDestination = startDestination,
                    themeMode = themeMode,
                    onThemeChange = { mode -> themeViewModel.setThemeMode(mode) }
                )
            }
        }
    }
}
