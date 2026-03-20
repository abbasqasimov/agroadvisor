package com.ram.agroadvisor.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ram.agroadvisor.ui.screens.ai.AIAssistantScreen
import com.ram.agroadvisor.ui.screens.MainScreen
import com.ram.agroadvisor.ui.screens.authentication.*
import com.ram.agroadvisor.ui.screens.home.WeatherScreen
import com.ram.agroadvisor.ui.screens.profile.AppearanceScreen
import com.ram.agroadvisor.ui.screens.profile.ProfileScreen
import com.ram.agroadvisor.ui.screens.profile.AccountSettingsScreen
import com.ram.agroadvisor.ui.theme.ThemeMode

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object SignUp : Screen("signup")
    data object FieldDetails : Screen("field_details")
    data object Main : Screen("main")
    data object Home : Screen("home")
    data object Weather : Screen("weather")
    data object AIAssistant : Screen("ai_assistant")
    data object Irrigation : Screen("irrigation")
    data object CropGuide : Screen("crop_guide")
    data object MarketPrice : Screen("market_price")
    data object Profile : Screen("profile")
    data object Appearance : Screen("appearance_screen")
    data object AccountSettings : Screen("account_settings_screen")
}

@Composable
fun NavGraph(
    startDestination: String = Screen.Welcome.route,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeChange: (ThemeMode) -> Unit = {}
) {
    val navController = rememberNavController()

    // Köməkçi funksiya: Alt ekranlardan Home-a (Main) qayıtmaq üçün
    val navigateToMain = {
        navController.navigate(Screen.Main.route) {
            // Main-ə qayıdanda arxadakı bütün yolu təmizləyirik
            popUpTo(Screen.Main.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onRegisterClick = { navController.navigate(Screen.SignUp.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        // Əsas nöqtə: Login-dən sonra Welcome-u yığından tam silirik
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(Screen.SignUp.route) }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = { navController.navigate(Screen.FieldDetails.route) },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.FieldDetails.route) {
            FieldDetailsScreen(
                onCompleteClick = {
                    navController.navigate(Screen.Main.route) {
                        // Qeydiyyat bitəndə Welcome-u tam silirik
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }

        // --- Alt Ekranlar (BackHandler əlavə edildi) ---

        composable(Screen.Profile.route) {
            BackHandler { navigateToMain() }
            ProfileScreen(
                onAccountSettingsClick = { navController.navigate(Screen.AccountSettings.route) },
                onAppearanceClick = { navController.navigate(Screen.Appearance.route) }
            )
        }

        composable(Screen.AccountSettings.route) {
            BackHandler { navigateToMain() }
            AccountSettingsScreen(onBackClick = { navigateToMain() })
        }

        composable(Screen.Appearance.route) {
            BackHandler { navigateToMain() }
            AppearanceScreen(
                themeMode = themeMode,
                onThemeChange = onThemeChange,
                onBackClick = { navigateToMain() }
            )
        }

        composable(Screen.AIAssistant.route) {
            BackHandler { navigateToMain() }
            AIAssistantScreen(
                mainPadding = PaddingValues(0.dp),
                onBackClick = { navigateToMain() }
            )
        }

        composable(Screen.Weather.route) {
            BackHandler { navigateToMain() }
            WeatherScreen(onBackClick = { navigateToMain() })
        }

        composable(Screen.Home.route) { }
        composable(Screen.Irrigation.route) { }
        composable(Screen.CropGuide.route) { }
        composable(Screen.MarketPrice.route) { }
    }
}