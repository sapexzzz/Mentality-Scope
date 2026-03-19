package com.mentality.gamescope.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ─── Цветовые схемы для каждого акцента ──────────────────────────────────────

private fun buildLightScheme(primary: Color, onPrimary: Color) = lightColorScheme(
    primary         = primary,
    onPrimary       = onPrimary,
    primaryContainer = primary.copy(alpha = 0.2f),
    onPrimaryContainer = primary
)

private fun buildDarkScheme(primary: Color, onPrimary: Color) = darkColorScheme(
    primary              = primary,
    onPrimary            = onPrimary,
    primaryContainer     = primary.copy(alpha = 0.3f),
    onPrimaryContainer   = primary,
    secondary            = Color(0xFF3D3D3D),
    onSecondary          = Color(0xFFEEEEEE),
    secondaryContainer   = Color(0xFF2A2A2A),
    onSecondaryContainer = Color.White
)

// AMOLED: полностью чёрный фон, белый текст
private fun buildAmoledScheme(primary: Color, onPrimary: Color) = darkColorScheme(
    primary              = primary,
    onPrimary            = onPrimary,
    primaryContainer     = primary.copy(alpha = 0.25f),
    onPrimaryContainer   = primary,
    secondary            = Color(0xFF252525),   // тёмные кнопки неактивных стилей
    onSecondary          = Color(0xFFEEEEEE),   // белый текст на них
    secondaryContainer   = Color(0xFF1A1A1A),
    onSecondaryContainer = Color(0xFFDDDDDD),
    tertiary             = Color(0xFF3A3A3A),
    onTertiary           = Color(0xFFFFFFFF),
    background           = Color.Black,
    onBackground         = Color.White,
    surface              = Color(0xFF0D0D0D),   // карточки чуть светлее фона
    onSurface            = Color(0xFFF0F0F0),
    surfaceVariant       = Color(0xFF1A1A1A),
    onSurfaceVariant     = Color(0xFFCCCCCC),
    inverseSurface       = Color(0xFFF0F0F0),
    inverseOnSurface     = Color.Black,
    outline              = Color(0xFF404040),
    outlineVariant       = Color(0xFF2A2A2A),
    error                = Color(0xFFFF6B6B),
    onError              = Color.Black
)

private fun primaryPair(appTheme: String): Pair<Color, Color> = when (appTheme) {
    "BLUE"    -> Color(0xFF1565C0) to Color.White
    "GREEN"   -> Color(0xFF2E7D32) to Color.White
    "PURPLE"  -> Color(0xFF6A1B9A) to Color.White
    "ORANGE"  -> Color(0xFFE64A19) to Color.White
    "TEAL"    -> Color(0xFF00695C) to Color.White
    else      -> Color(0xFFB71C1C) to Color.White  // RED
}

// ─── Тема ─────────────────────────────────────────────────────────────────────

@Composable
fun MentalityScopeTheme(
    appTheme: String = "RED",
    darkMode: String = "SYSTEM",   // "SYSTEM" | "LIGHT" | "DARK" | "AMOLED"
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemDark = isSystemInDarkTheme()
    val isDark = when (darkMode) {
        "LIGHT"  -> false
        "DARK"   -> true
        "AMOLED" -> true
        else     -> systemDark  // SYSTEM
    }

    val colorScheme = when {
        appTheme == "DYNAMIC" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            when (darkMode) {
                "AMOLED" -> {
                    val base = dynamicDarkColorScheme(context)
                    buildAmoledScheme(base.primary, base.onPrimary)
                }
                "LIGHT"  -> dynamicLightColorScheme(context)
                "DARK"   -> dynamicDarkColorScheme(context)
                else     -> if (systemDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
        }
        else -> {
            val (primary, onPrimary) = primaryPair(appTheme)
            when {
                darkMode == "AMOLED" -> buildAmoledScheme(primary, onPrimary)
                isDark               -> buildDarkScheme(primary, onPrimary)
                else                 -> buildLightScheme(primary, onPrimary)
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


