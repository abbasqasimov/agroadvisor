package com.ram.agroadvisor.ui.navigation

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
import com.ram.agroadvisor.ui.screens.profile.AppearanceScreen
import com.ram.agroadvisor.ui.screens.profile.ProfileScreen

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object FieldDetails : Screen("field_details") // Yeni əlavə edildi
    object Main : Screen("main")
    object Home : Screen("home")
    object Weather : Screen("weather")
    object AIAssistant : Screen("ai_assistant")
    object Irrigation : Screen("irrigation")
    object CropGuide : Screen("crop_guide")
    object MarketPrice : Screen("market_price")
    object Profile : Screen("profile")
    object Appearance : Screen("appearance_screen")
}

@Composable
fun NavGraph(startDestination: String = Screen.Welcome.route) {
    val navController = rememberNavController()

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
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(Screen.SignUp.route) }
            )
        }

        // SignUp -> FieldDetails-ə keçir
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.FieldDetails.route)
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // FieldDetails -> Main-ə keçir və qeydiyyat prosesini yığından silir
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

        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onAppearanceClick = { navController.navigate(Screen.Appearance.route) }
            )
        }

        composable(Screen.Appearance.route) {
            AppearanceScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.AIAssistant.route) {
            AIAssistantScreen(
                mainPadding = PaddingValues(0.dp),
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Weather.route) {
            WeatherScreen(onBackClick = { navController.popBackStack() })
        }

        // Digər boş marşrutlar
        composable(Screen.Home.route) { }
        composable(Screen.Irrigation.route) { }
        composable(Screen.CropGuide.route) { }
        composable(Screen.MarketPrice.route) { }
    }
}