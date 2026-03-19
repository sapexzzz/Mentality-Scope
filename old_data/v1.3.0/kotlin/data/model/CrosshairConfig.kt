package com.mentality.gamescope.data.model

/**
 * Модель конфигурации прицела
 */
data class CrosshairConfig(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "Custom",
    val style: CrosshairStyle = CrosshairStyle.CROSSHAIR,
    val color: String = "#FFFFFF",
    val size: Float = 1.0f,
    val alpha: Float = 0.9f,
    val thickness: Float = 2.0f,
    val lineLength: Float = 1.0f,  // множитель длины линий: 0.2..2.0
    val gapSize: Float = 0.15f,    // зазор от центра (доля от scaledSize): 0.0..0.5
    val isPreset: Boolean = false
) {
    companion object {
        private val presets = listOf(
            CrosshairConfig(
                id = "preset_csgo",
                name = "CS GO",
                style = CrosshairStyle.CROSSHAIR,
                color = "#FF0000",
                size = 1.0f,
                alpha = 0.9f,
                thickness = 2.0f,
                lineLength = 1.0f,
                gapSize = 0.20f,
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
                lineLength = 0.7f,
                gapSize = 0.25f,
                isPreset = true
            ),
            CrosshairConfig(
                id = "preset_pubg",
                name = "PUBG",
                style = CrosshairStyle.DOT,
                color = "#FFFF00",
                size = 1.2f,
                alpha = 0.8f,
                thickness = 3.0f,
                lineLength = 1.0f,
                gapSize = 0.15f,
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
                lineLength = 1.0f,
                gapSize = 0.15f,
                isPreset = true
            ),
            CrosshairConfig(
                id = "preset_classic",
                name = "Classic",
                style = CrosshairStyle.CIRCLE,
                color = "#4FC3F7",
                size = 0.9f,
                alpha = 0.95f,
                thickness = 1.5f,
                lineLength = 1.0f,
                gapSize = 0.15f,
                isPreset = true
            )
        )

        fun getPresets(): List<CrosshairConfig> = presets
        fun getPresetById(id: String): CrosshairConfig? = presets.find { it.id == id }
        fun getDefault(): CrosshairConfig = presets[3]
    }
}
