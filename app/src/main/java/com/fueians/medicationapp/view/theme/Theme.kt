package com.fueians.medicationapp.view.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LightColorScheme = lightColorScheme(
    primary = Blue500,
    onPrimary = White,

    background = Grey50,
    onBackground = Black,

    surface = White,
    onSurface = Black,

    surfaceVariant = Grey100,
    onSurfaceVariant = Grey700,

    outline = Grey300,

    error = Red500,
    onError = White,

    secondary = Yellow500,
    onSecondary = Black,

    tertiary = Green500,
    onTertiary = White,
    tertiaryContainer = Green500.copy(alpha = 0.12f),
    onTertiaryContainer = Green500
)

private val DarkColorScheme = darkColorScheme(
    // Primary Blue
    primary = Blue300,          // slightly lighter for dark background
    onPrimary = Black,          // text/icons on primary should be dark for contrast

    // Background
    background = Grey900,       // dark grey background
    onBackground = Grey50,      // light text on dark background

    // Surface
    surface = Grey800,          // dark surface
    onSurface = Grey50,         // light text
    surfaceVariant = Grey700,   // subtle variation
    onSurfaceVariant = Grey100, // readable text

    // Outline
    outline = Grey600,

    // Error
    error = Red600,
    onError = White,

    // Secondary (warnings / network)
    secondary = Yellow500,
    onSecondary = Grey900,      // dark text on yellow

    // Tertiary (success)
    tertiary = Green300,             // brighter green for dark mode
    onTertiary = Grey900,            // dark text
    tertiaryContainer = Green500.copy(alpha = 0.25f),
    onTertiaryContainer = White
)



@Composable
fun AppTheme(
    useDarkTheme: Boolean = AppThemeState.isDark,
    content: @Composable () -> Unit
) {
    val darkTheme = useDarkTheme || isSystemInDarkTheme()
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes(),
        content = content
    )
}



object AppThemeState {
    // false = light, true = dark
    var isDark by mutableStateOf(false)
}