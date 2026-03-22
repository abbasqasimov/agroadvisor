package com.ram.agroadvisor.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ram.agroadvisor.ui.screens.ai.AIAssistantScreen
import com.ram.agroadvisor.ui.screens.MainScreen
import com.ram.agroadvisor.ui.screens.authentication.*
import com.ram.agroadvisor.ui.screens.home.WeatherScreen
import com.ram.agroadvisor.ui.screens.profile.settings.AppearanceScreen
import com.ram.agroadvisor.ui.screens.profile.ProfileScreen
import com.ram.agroadvisor.ui.screens.profile.HelpCenterScreen
import com.ram.agroadvisor.ui.screens.profile.ContactSupportScreen
import com.ram.agroadvisor.ui.screens.profile.settings.AccountSettingsScreen
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

    // Main ekrana qayıtmaq üçün ortaq funksiya
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
        // --- AUTHENTICATION SECTION ---
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

        // --- MAIN APP SECTION ---
        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }

        // --- PROFILE & SETTINGS SECTION ---
        composable(Screen.Profile.route) {
            BackHandler { navigateToMain() }
            ProfileScreen(
                onAccountSettingsClick = { navController.navigate(Screen.AccountSettings.route) },
                onAppearanceClick = { navController.navigate(Screen.Appearance.route) },
                onHelpCenterClick = { navController.navigate(Screen.HelpCenter.route) },
                onContactSupportClick = { navController.navigate(Screen.ContactSupport.route) }
            )
        }

        composable(Screen.AccountSettings.route) {
            BackHandler { navController.popBackStack() }
            AccountSettingsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Appearance.route) {
            BackHandler { navController.popBackStack() }
            AppearanceScreen(
                themeMode = themeMode,
                onThemeChange = onThemeChange,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.HelpCenter.route) {
            BackHandler { navController.popBackStack() }
            HelpCenterScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.ContactSupport.route) {
            BackHandler { navController.popBackStack() }
            ContactSupportScreen(onBackClick = { navController.popBackStack() })
        }

        // --- FEATURES SECTION ---
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

        // --- EMPTY ROUTES (FUTURE IMPLEMENTATION) ---
        composable(Screen.Home.route) { }
        composable(Screen.Irrigation.route) { }
        composable(Screen.CropGuide.route) { }
        composable(Screen.MarketPrice.route) { }
    }
}