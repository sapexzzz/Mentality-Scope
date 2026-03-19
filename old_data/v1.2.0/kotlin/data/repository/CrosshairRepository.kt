package com.mentality.gamescope.data.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.mentality.gamescope.data.model.CrosshairConfig
import com.mentality.gamescope.data.model.CrosshairStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "crosshair_prefs")

/**
 * Repository для управления конфигурацией прицела через DataStore
 */
class CrosshairRepository(private val context: Context) {

    companion object {
        private const val CURRENT_CONFIG_KEY = "current_config"
        private const val CUSTOM_CONFIGS_KEY = "custom_configs"
        private const val AUTO_START_KEY = "auto_start"
        private const val IS_SERVICE_RUNNING_KEY = "is_service_running"
        private const val APP_THEME_KEY = "app_theme"
        private const val APP_LANGUAGE_KEY = "app_language"
    }

    private val dataStore = context.dataStore

    /**
     * Получить текущий конфиг как Flow
     */
    fun getCurrentConfigFlow(): Flow<CrosshairConfig> = dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(CURRENT_CONFIG_KEY)]?.let { json ->
            deserializeConfig(json)
        } ?: CrosshairConfig.getDefault()
    }

    /**
     * Сохранить текущий конфиг
     */
    suspend fun saveCurrentConfig(config: CrosshairConfig) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(CURRENT_CONFIG_KEY)] = serializeConfig(config)
        }
    }

    /**
     * Получить список всех кастомных конфигов
     */
    fun getCustomConfigsFlow(): Flow<List<CrosshairConfig>> = dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(CUSTOM_CONFIGS_KEY)]?.let { json ->
            deserializeConfigList(json)
        } ?: emptyList()
    }

    /**
     * Добавить новый кастомный конфиг
     */
    suspend fun addCustomConfig(config: CrosshairConfig) {
        val currentConfigs = getCustomConfigsList()
        val newConfigs = currentConfigs + config
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(CUSTOM_CONFIGS_KEY)] = serializeConfigList(newConfigs)
        }
    }

    /**
     * Удалить кастомный конфиг по ID
     */
    suspend fun deleteCustomConfig(configId: String) {
        val currentConfigs = getCustomConfigsList()
        val newConfigs = currentConfigs.filter { it.id != configId }
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(CUSTOM_CONFIGS_KEY)] = serializeConfigList(newConfigs)
        }
    }

    /**
     * Обновить существующий кастомный конфиг
     */
    suspend fun updateCustomConfig(config: CrosshairConfig) {
        val currentConfigs = getCustomConfigsList()
        val newConfigs = currentConfigs.map { if (it.id == config.id) config else it }
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(CUSTOM_CONFIGS_KEY)] = serializeConfigList(newConfigs)
        }
    }

    /**
     * Получить список кастомных конфигов (sync)
     */
    private suspend fun getCustomConfigsList(): List<CrosshairConfig> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(CUSTOM_CONFIGS_KEY)]?.let { json ->
                deserializeConfigList(json)
            } ?: emptyList()
        }.first()
    }

    /**
     * Сохранить флаг автозапуска сервиса
     */
    suspend fun setAutoStart(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(AUTO_START_KEY)] = enabled
        }
    }

    /**
     * Получить флаг автозапуска
     */
    fun getAutoStartFlow(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey(AUTO_START_KEY)] ?: false
    }

    /**
     * Тема приложения
     */
    fun getAppThemeFlow(): Flow<String> = dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(APP_THEME_KEY)] ?: "RED"
    }

    suspend fun setAppTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(APP_THEME_KEY)] = theme
        }
    }

    /**
     * Язык приложения
     */
    fun getAppLanguageFlow(): Flow<String> = dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(APP_LANGUAGE_KEY)] ?: "SYSTEM"
    }

    suspend fun setAppLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(APP_LANGUAGE_KEY)] = language
        }
    }

    /**
     * Сохранить статус запуска сервиса
     */
    suspend fun setServiceRunning(isRunning: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(IS_SERVICE_RUNNING_KEY)] = isRunning
        }
    }

    /**
     * Получить статус запуска сервиса
     */
    fun getServiceRunningFlow(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey(IS_SERVICE_RUNNING_KEY)] ?: false
    }

    /**
     * Очистить все настройки
     */
    suspend fun clearAllSettings() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Получить встроенные пресеты
     */
    fun getPresets(): List<CrosshairConfig> = CrosshairConfig.getPresets()

    private fun serializeConfig(config: CrosshairConfig): String {
        return try {
            JSONObject().apply {
                put("id", config.id)
                put("name", config.name)
                put("style", config.style.name)
                put("color", config.color)
                put("size", config.size.toDouble())
                put("alpha", config.alpha.toDouble())
                put("thickness", config.thickness.toDouble())
                put("lineLength", config.lineLength.toDouble())
                put("gapSize", config.gapSize.toDouble())
                put("isPreset", config.isPreset)
            }.toString()
        } catch (e: Exception) {
            "{}"
        }
    }

    private fun deserializeConfig(json: String): CrosshairConfig {
        return try {
            val obj = JSONObject(json)
            CrosshairConfig(
                id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                name = obj.optString("name", "Custom"),
                style = CrosshairStyle.fromString(obj.optString("style", "CROSSHAIR")),
                color = obj.optString("color", "#FFFFFF"),
                size = obj.optDouble("size", 1.0).toFloat(),
                alpha = obj.optDouble("alpha", 0.9).toFloat(),
                thickness = obj.optDouble("thickness", 2.0).toFloat(),
                lineLength = obj.optDouble("lineLength", 1.0).toFloat(),
                gapSize = obj.optDouble("gapSize", 0.15).toFloat(),
                isPreset = obj.optBoolean("isPreset", false)
            )
        } catch (e: Exception) {
            CrosshairConfig.getDefault()
        }
    }

    private fun serializeConfigList(configs: List<CrosshairConfig>): String {
        return try {
            JSONArray().apply {
                configs.forEach { config ->
                    put(JSONObject().apply {
                        put("id", config.id)
                        put("name", config.name)
                        put("style", config.style.name)
                        put("color", config.color)
                        put("size", config.size.toDouble())
                        put("alpha", config.alpha.toDouble())
                        put("thickness", config.thickness.toDouble())
                        put("lineLength", config.lineLength.toDouble())
                        put("gapSize", config.gapSize.toDouble())
                        put("isPreset", config.isPreset)
                    })
                }
            }.toString()
        } catch (e: Exception) {
            "[]"
        }
    }

    private fun deserializeConfigList(json: String): List<CrosshairConfig> {
        return try {
            val array = JSONArray(json)
            val result = mutableListOf<CrosshairConfig>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                result.add(
                    CrosshairConfig(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        name = obj.optString("name", "Custom"),
                        style = CrosshairStyle.fromString(obj.optString("style", "CROSSHAIR")),
                        color = obj.optString("color", "#FFFFFF"),
                        size = obj.optDouble("size", 1.0).toFloat(),
                        alpha = obj.optDouble("alpha", 0.9).toFloat(),
                        thickness = obj.optDouble("thickness", 2.0).toFloat(),
                        lineLength = obj.optDouble("lineLength", 1.0).toFloat(),
                        gapSize = obj.optDouble("gapSize", 0.15).toFloat(),
                        isPreset = obj.optBoolean("isPreset", false)
                    )
                )
            }
            result
        } catch (e: Exception) {
            emptyList()
        }
    }
}
