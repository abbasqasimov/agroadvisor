package com.ram.agroadvisor.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ram.agroadvisor.ui.screens.ai.AIAssistantScreen
import com.ram.agroadvisor.ui.screens.authentication.FieldDetailsScreen
import com.ram.agroadvisor.ui.screens.authentication.LoginScreen
import com.ram.agroadvisor.ui.screens.authentication.SignUpScreen
import com.ram.agroadvisor.ui.screens.authentication.WelcomeScreen
import com.ram.agroadvisor.ui.screens.calculator.CalculatorScreen
import com.ram.agroadvisor.ui.screens.home.HomeScreen
import com.ram.agroadvisor.ui.screens.home.WeatherScreen
import com.ram.agroadvisor.ui.screens.plus.PlusScreen
import com.ram.agroadvisor.ui.screens.profile.ContactSupportScreen
import com.ram.agroadvisor.ui.screens.profile.HelpCenterScreen
import com.ram.agroadvisor.ui.screens.profile.ProfileScreen
import com.ram.agroadvisor.ui.screens.profile.settings.AccountSettingsScreen
import com.ram.agroadvisor.ui.screens.profile.settings.AppearanceScreen
import com.ram.agroadvisor.ui.screens.resources.CropGuideScreen
import com.ram.agroadvisor.ui.theme.ThemeMode

fun NavGraphBuilder.navGraph(
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    // --- AUTH ---
    composable(Screen.Welcome.route) { WelcomeScreen() }
    composable(Screen.Login.route) { LoginScreen() }
    composable(Screen.SignUp.route) { SignUpScreen() }
    composable(Screen.FieldDetails.route) { FieldDetailsScreen() }

    // --- BOTTOM NAV TABS ---
    composable(Screen.Home.route) { HomeScreen() }
    composable(Screen.Calculator.route) { CalculatorScreen() }
    composable(Screen.Analysis.route) { PlusScreen() }
    composable(Screen.Profile.route) { ProfileScreen() }

    // --- FULL-SCREEN ---
    composable(Screen.Weather.route) { WeatherScreen() }
    composable(
        route = Screen.AIAssistant.route,
        arguments = listOf(
            navArgument(Screen.AIAssistant.ARG_PREFILL) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val raw = backStackEntry.arguments?.getString(Screen.AIAssistant.ARG_PREFILL)
        // The query value was URL-encoded by routeWithPrefill (spaces -> "+",
        // unicode -> %xx). Decode it so the input field shows the human form.
        val prefill = raw?.let { java.net.URLDecoder.decode(it, "UTF-8") }
        AIAssistantScreen(prefillMessage = prefill)
    }
    composable(Screen.CropGuide.route) { CropGuideScreen() }

    // --- SETTINGS ---
    composable(Screen.AccountSettings.route) { AccountSettingsScreen() }
    composable(Screen.Appearance.route) {
        AppearanceScreen(themeMode = themeMode, onThemeChange = onThemeChange)
    }
    composable(Screen.HelpCenter.route) { HelpCenterScreen() }
    composable(Screen.ContactSupport.route) { ContactSupportScreen() }

}
