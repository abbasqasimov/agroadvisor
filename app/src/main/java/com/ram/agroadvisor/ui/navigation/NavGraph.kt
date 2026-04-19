package com.ram.agroadvisor.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ram.agroadvisor.ui.screens.ai.AIAssistantScreen
import com.ram.agroadvisor.ui.screens.MainScreen
import com.ram.agroadvisor.ui.screens.authentication.*
import com.ram.agroadvisor.ui.screens.home.WeatherScreen
import com.ram.agroadvisor.ui.screens.home.WeatherViewModel
import com.ram.agroadvisor.ui.screens.profile.HelpCenterScreen
import com.ram.agroadvisor.ui.screens.profile.ContactSupportScreen
import com.ram.agroadvisor.ui.screens.profile.settings.AccountSettingsScreen
import com.ram.agroadvisor.ui.screens.profile.settings.AppearanceScreen
import com.ram.agroadvisor.ui.screens.resources.CropGuideScreen
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
    data object HelpCenter : Screen("help_center")
    data object ContactSupport : Screen("contact_support")
}

@Composable
fun NavGraph(
    startDestination: String = Screen.Welcome.route,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeChange: (ThemeMode) -> Unit = {}
) {
    val navController = rememberNavController()

    val navigateToMain = {
        navController.navigate(Screen.Main.route) {
            popUpTo(Screen.Main.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // --- AUTH ---
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
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- MAIN ---
        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }

        // --- SETTINGS ---
        composable(Screen.AccountSettings.route) {
            AccountSettingsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Appearance.route) {
            AppearanceScreen(
                themeMode = themeMode,
                onThemeChange = onThemeChange,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.HelpCenter.route) {
            HelpCenterScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.ContactSupport.route) {
            ContactSupportScreen(onBackClick = { navController.popBackStack() })
        }

        // --- FEATURES ---
        composable(Screen.AIAssistant.route) {
            BackHandler { navigateToMain() }
            AIAssistantScreen(
                mainPadding = PaddingValues(0.dp),
                onBackClick = { navigateToMain() }
            )
        }

        composable(Screen.Weather.route) {
            BackHandler { navigateToMain() }
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.Main.route)
            }
            val weatherViewModel: WeatherViewModel = viewModel(parentEntry)
            WeatherScreen(
                onBackClick = { navigateToMain() },
                weatherViewModel = weatherViewModel
            )
        }

        composable(Screen.CropGuide.route) {
            CropGuideScreen(onBackClick = { navController.popBackStack() })
        }

        // --- EMPTY ---
        composable(Screen.Home.route) { }
        composable(Screen.Irrigation.route) { }
        composable(Screen.MarketPrice.route) { }
    }
}