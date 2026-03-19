package com.mentality.gamescope.data.model

/**
 * Модель конфигурации прицела
 * @param id Уникальный идентификатор конфига
 * @param name Имя конфига
 * @param style Стиль прицела (DOT, CROSSHAIR, CIRCLE)
 * @param color Цвет в формате #RRGGBB (например, #FF0000 для красного)
 * @param size Размер прицела (0.5f - 3.0f), где 1.0f = базовый размер
 * @param alpha Прозрачность (0.3f - 1.0f)
 * @param thickness Толщина линий для крестика (1.0f - 4.0f)
 * @param isPreset True если это встроенный пресет, False если пользовательский конфиг
 */
data class CrosshairConfig(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "Custom",
    val style: CrosshairStyle = CrosshairStyle.CROSSHAIR,
    val color: String = "#FFFFFF", // Hex color
    val size: Float = 1.0f,
    val alpha: Float = 0.9f,
    val thickness: Float = 2.0f,
    val isPreset: Boolean = false
) {
    companion object {
        /**
         * Встроенные пресеты для популярных игр
         */
        private val presets = listOf(
            CrosshairConfig(
                id = "preset_csgo",
                name = "CS GO",
                style = CrosshairStyle.CROSSHAIR,
                color = "#FF0000",
                size = 1.0f,
                alpha = 0.9f,
                thickness = 2.0f,
                isPreset = true
            ),
            CrosshairConfig(
                id = "preset_valorant",
                name = "Valorant",
                style = CrosshairStyle.CROSSHAIR,
                color = "#00FF00",
                size = 0.8f,
                alpha = 0.85f,
                thickness = 2.0f,
                isPreset = true
            ),
            CrosshairConfig(
                id = "preset_pubg",
                name = "PUBG",
                style = CrosshairStyle.DOT,
                color = "#FFFF00",
                size = 1.2f,
                alpha = 0.8f,
                thickness = 2.5f,
                isPreset = true
            ),
            CrosshairConfig(
                id = "preset_default",
                name = "Default",
                style = CrosshairStyle.CROSSHAIR,
                color = "#FFFFFF",
                size = 1.0f,
                alpha = 0.9f,
                thickness = 2.0f,
                isPreset = true
            ),
            CrosshairConfig(
                id = "preset_classic",
                name = "Classic",
                style = CrosshairStyle.CIRCLE,
                color = "#0000FF",
                size = 0.9f,
                alpha = 0.95f,
                thickness = 1.5f,
                isPreset = true
            )
        )

        fun getPresets(): List<CrosshairConfig> = presets

        fun getPresetById(id: String): CrosshairConfig? =
            presets.find { it.id == id }

        fun getDefault(): CrosshairConfig = presets[3] // Default preset
    }
}
