package com.mentality.gamescope.data.model

/**
 * Enum для стилей прицела
 */
enum class CrosshairStyle {
    DOT,          // Простая точка
    CROSSHAIR,    // Крестик (+ форма)
    CIRCLE;       // Окружность

    companion object {
        fun fromString(value: String?): CrosshairStyle {
            return try {
                valueOf(value?.uppercase() ?: "CROSSHAIR")
            } catch (e: Exception) {
                CROSSHAIR
            }
        }
    }

    fun getDisplayName(): String {
        return when (this) {
            DOT -> "Точка"
            CROSSHAIR -> "Крестик"
            CIRCLE -> "Круг"
        }
    }
}
