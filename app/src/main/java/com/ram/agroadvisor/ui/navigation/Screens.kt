package com.ram.agroadvisor.ui.navigation

sealed class Screen(val route: String) {
    // --- Auth ---
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object SignUp : Screen("signup")
    data object FieldDetails : Screen("field_details")

    // --- Bottom nav tabs ---
    data object Home : Screen("home")
    data object Calculator : Screen("calculator")
    data object Analysis : Screen("analysis")
    data object Profile : Screen("profile")

    // --- Full-screen / overlay (no bottom bar) ---
    data object Weather : Screen("weather")
    data object AIAssistant : Screen("ai_assistant")
    data object CropGuide : Screen("crop_guide")
    data object Appearance : Screen("appearance_screen")
    data object AccountSettings : Screen("account_settings_screen")
    data object HelpCenter : Screen("help_center")
    data object ContactSupport : Screen("contact_support")
    data object PrivacySecurity : Screen("privacy_security")
}
