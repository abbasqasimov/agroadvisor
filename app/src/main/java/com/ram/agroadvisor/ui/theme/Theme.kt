package com.ram.agroadvisor.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = LightColors.PrimaryGreen,
    onPrimary = LightColors.TextOnPrimary,
    primaryContainer = LightColors.LightGreen,
    onPrimaryContainer = LightColors.TextOnPrimary,

    secondary = LightColors.AccentBlue,
    onSecondary = Color.White,
    secondaryContainer = LightColors.BlueTint,
    onSecondaryContainer = LightColors.TextPrimary,

    tertiary = LightColors.AccentYellow,
    onTertiary = Color.Black,
    tertiaryContainer = LightColors.YellowTint,
    onTertiaryContainer = LightColors.TextPrimary,

    error = LightColors.Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = LightColors.Background,
    onBackground = LightColors.TextPrimary,

    surface = LightColors.Surface,
    onSurface = LightColors.TextPrimary,
    surfaceVariant = LightColors.CardBackground,
    onSurfaceVariant = LightColors.TextSecondary,

    outline = LightColors.Border,
    outlineVariant = LightColors.Divider
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkColors.PrimaryGreen,
    onPrimary = DarkColors.TextOnPrimary,
    primaryContainer = DarkColors.DarkGreen,
    onPrimaryContainer = DarkColors.TextPrimary,

    secondary = DarkColors.AccentBlue,
    onSecondary = Color.Black,
    secondaryContainer = DarkColors.BlueTint,
    onSecondaryContainer = DarkColors.TextPrimary,

    tertiary = DarkColors.AccentYellow,
    onTertiary = Color.Black,
    tertiaryContainer = DarkColors.YellowTint,
    onTertiaryContainer = DarkColors.TextPrimary,

    error = DarkColors.Error,
    onError = Color.Black,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = DarkColors.Background,
    onBackground = DarkColors.TextPrimary,

    surface = DarkColors.Surface,
    onSurface = DarkColors.TextPrimary,
    surfaceVariant = DarkColors.CardBackground,
    onSurfaceVariant = DarkColors.TextSecondary,

    outline = DarkColors.Border,
    outlineVariant = DarkColors.Divider
)

@Composable
fun AgroAdvisorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}