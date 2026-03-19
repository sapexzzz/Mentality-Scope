package com.mentality.gamescope.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun MentalityScopeTheme(
    appTheme: String = "RED",
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        appTheme == "DYNAMIC" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> {
            val primary = when (appTheme) {
                "BLUE"   -> Color(0xFF1565C0)
                "GREEN"  -> Color(0xFF2E7D32)
                "PURPLE" -> Color(0xFF6A1B9A)
                "ORANGE" -> Color(0xFFE64A19)
                "TEAL"   -> Color(0xFF00695C)
                else     -> Color(0xFFB71C1C) // RED
            }
            if (darkTheme) darkColorScheme(primary = primary) else lightColorScheme(primary = primary)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun isSystemInDarkTheme(): Boolean {
    return androidx.compose.foundation.isSystemInDarkTheme()
}

