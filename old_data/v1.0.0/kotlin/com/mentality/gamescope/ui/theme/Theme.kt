package com.mentality.gamescope.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Light Color Scheme для Material Design 3
 */
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight40,
    onPrimaryContainer = Primary,
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = SecondaryLight40,
    onSecondaryContainer = Secondary,
    tertiary = Tertiary,
    onTertiary = Color.White,
    tertiaryContainer = TertiaryLight40,
    onTertiaryContainer = Tertiary,
    error = Error,
    onError = Color.White,
    errorContainer = ErrorLight40,
    onErrorContainer = Error,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant
)

/**
 * Dark Color Scheme для Material Design 3
 */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight80,
    onPrimary = Primary,
    primaryContainer = PrimaryLight20,
    onPrimaryContainer = PrimaryLight80,
    secondary = SecondaryLight80,
    onSecondary = Secondary,
    secondaryContainer = SecondaryLight20,
    onSecondaryContainer = SecondaryLight80,
    tertiary = TertiaryLight80,
    onTertiary = Tertiary,
    tertiaryContainer = TertiaryLight20,
    onTertiaryContainer = TertiaryLight80,
    error = ErrorLight80,
    onError = Error,
    errorContainer = ErrorLight20,
    onErrorContainer = ErrorLight80,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

/**
 * Основная функция темы приложения
 */
@Composable
fun MentalityScopeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Для проверки темы системы
@Composable
fun isSystemInDarkTheme(): Boolean {
    return androidx.compose.foundation.isSystemInDarkTheme()
}
