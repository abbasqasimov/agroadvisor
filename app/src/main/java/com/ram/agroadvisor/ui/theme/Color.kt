package com.ram.agroadvisor.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Colors
object LightColors {
    // Primary Colors
    val PrimaryGreen = Color(0xFF2E7D32)
    val LightGreen = Color(0xFF4CAF50)
    val DarkGreen = Color(0xFF1B5E20)

    // Background Colors
    val Background = Color(0xFFF5F5F5)
    val Surface = Color.White
    val CardBackground = Color.White

    // Text Colors
    val TextPrimary = Color.Black
    val TextSecondary = Color.Gray
    val TextOnPrimary = Color.White

    // Accent Colors
    val AccentBlue = Color(0xFF2196F3)
    val AccentYellow = Color(0xFFFBC02D)
    val AccentPurple = Color(0xFF9C27B0)
    val AccentOrange = Color(0xFFFF9800)
    val AccentPink = Color(0xFFE91E63)
    val AccentSunYellow = Color(0xFFFFEB3B)
    val AccentLightGreen = Color(0xFF81C784)
    val AccentWarning = Color(0xFFFFC107)

    // Feature Card Backgrounds (light tints)
    val GreenTint = Color(0xFFE8F5E9)
    val BlueTint = Color(0xFFE3F2FD)
    val YellowTint = Color(0xFFFFF9C4)
    val PurpleTint = Color(0xFFF3E5F5)

    // Functional Colors
    val Error = Color.Red
    val Success = Color(0xFF4CAF50)
    val OnlineIndicator = Color(0xFF4CAF50)

    // Divider & Border
    val Divider = Color.LightGray.copy(alpha = 0.3f)
    val Border = Color.LightGray
}

// Dark Theme Colors
object DarkColors {
    // Primary Colors
    val PrimaryGreen = Color(0xFF66BB6A)
    val LightGreen = Color(0xFF81C784)
    val DarkGreen = Color(0xFF388E3C)

    // Background Colors
    val Background = Color(0xFF121212)
    val Surface = Color(0xFF1E1E1E)
    val CardBackground = Color(0xFF2C2C2C)

    // Text Colors
    val TextPrimary = Color.White
    val TextSecondary = Color(0xFFB0B0B0)
    val TextOnPrimary = Color.Black

    // Accent Colors
    val AccentBlue = Color(0xFF64B5F6)
    val AccentYellow = Color(0xFFFFD54F)
    val AccentPurple = Color(0xFFBA68C8)
    val AccentOrange = Color(0xFFFFB74D)
    val AccentPink = Color(0xFFF06292)
    val AccentSunYellow = Color(0xFFFFF176)
    val AccentLightGreen = Color(0xFFA5D6A7)
    val AccentWarning = Color(0xFFFFCA28)

    // Feature Card Backgrounds (dark tints)
    val GreenTint = Color(0xFF1B5E20).copy(alpha = 0.3f)
    val BlueTint = Color(0xFF0D47A1).copy(alpha = 0.3f)
    val YellowTint = Color(0xFFF57F17).copy(alpha = 0.3f)
    val PurpleTint = Color(0xFF4A148C).copy(alpha = 0.3f)

    // Functional Colors
    val Error = Color(0xFFEF5350)
    val Success = Color(0xFF66BB6A)
    val OnlineIndicator = Color(0xFF66BB6A)

    // Divider & Border
    val Divider = Color.Gray.copy(alpha = 0.2f)
    val Border = Color.Gray
}