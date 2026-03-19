package com.mentality.gamescope.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentality.gamescope.data.model.CrosshairConfig
import com.mentality.gamescope.data.repository.CrosshairRepository
import com.mentality.gamescope.service.CrosshairService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для управления состоянием приложения Mentality Scope
 * Управляет конфигурацией прицела, состоянием сервиса и сохранением пресетов
 */
class CrosshairViewModel(
    private val context: Context,
    private val repository: CrosshairRepository
) : ViewModel() {

    // Текущей конфиг прицела
    private val _currentConfig = MutableStateFlow(CrosshairConfig.getDefault())
    val currentConfig: StateFlow<CrosshairConfig> = _currentConfig.asStateFlow()

    // Статус запуска сервиса
    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    // Список кастомных конфигов
    private val _customConfigs = MutableStateFlow<List<CrosshairConfig>>(emptyList())
    val customConfigs: StateFlow<List<CrosshairConfig>> = _customConfigs.asStateFlow()

    // Встроенные пресеты
    private val _presets = MutableStateFlow(CrosshairConfig.getPresets())
    val presets: StateFlow<List<CrosshairConfig>> = _presets.asStateFlow()

    // Флаг автозапуска сервиса
    private val _autoStart = MutableStateFlow(false)
    val autoStart: StateFlow<Boolean> = _autoStart.asStateFlow()

    init {
        // Загрузить конфиг при инициализации
        viewModelScope.launch {
            repository.getCurrentConfigFlow().collect { config ->
                _currentConfig.value = config
            }
        }

        // Загрузить статус сервиса
        viewModelScope.launch {
            repository.getServiceRunningFlow().collect { running ->
                _isServiceRunning.value = running
            }
        }

        // Загрузить кастомные конфиги
        viewModelScope.launch {
            repository.getCustomConfigsFlow().collect { configs ->
                _customConfigs.value = configs
            }
        }

        // Загрузить автозапуск
        viewModelScope.launch {
            repository.getAutoStartFlow().collect { enabled ->
                _autoStart.value = enabled
            }
        }
    }

    /**
     * Запустить сервис прицела
     */
    fun startService() {
        viewModelScope.launch {
            try {
                val serviceIntent = Intent(context, CrosshairService::class.java)
                context.startForegroundService(serviceIntent)
                _isServiceRunning.value = true
                repository.setServiceRunning(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Остановить сервис прицела
     */
    fun stopService() {
        viewModelScope.launch {
            try {
                val serviceIntent = Intent(context, CrosshairService::class.java)
                context.stopService(serviceIntent)
                _isServiceRunning.value = false
                repository.setServiceRunning(false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Переключить состояние сервиса (вкл/выкл)
     */
    fun toggleService() {
        if (_isServiceRunning.value) {
            stopService()
        } else {
            startService()
        }
    }

    /**
     * Обновить текущую конфигурацию прицела
     */
    fun updateConfig(config: CrosshairConfig) {
        viewModelScope.launch {
            _currentConfig.value = config
            repository.saveCurrentConfig(config)
        }
    }

    /**
     * Установить размер прицела
     */
    fun setSize(size: Float) {
        val newConfig = _currentConfig.value.copy(size = size.coerceIn(0.5f, 3.0f))
        updateConfig(newConfig)
    }

    /**
     * Установить альфа (прозрачность)
     */
    fun setAlpha(alpha: Float) {
        val newConfig = _currentConfig.value.copy(alpha = alpha.coerceIn(0.3f, 1.0f))
        updateConfig(newConfig)
    }

    /**
     * Установить толщину линий
     */
    fun setThickness(thickness: Float) {
        val newConfig = _currentConfig.value.copy(thickness = thickness.coerceIn(1.0f, 4.0f))
        updateConfig(newConfig)
    }

    /**
     * Установить цвет
     */
    fun setColor(color: String) {
        val newConfig = _currentConfig.value.copy(color = color)
        updateConfig(newConfig)
    }

    /**
     * Установить стиль прицела
     */
    fun setStyle(style: com.mentality.gamescope.data.model.CrosshairStyle) {
        val newConfig = _currentConfig.value.copy(style = style)
        updateConfig(newConfig)
    }

    /**
     * Загрузить пресет по ID
     */
    fun loadPreset(presetId: String) {
        viewModelScope.launch {
            val preset = CrosshairConfig.getPresetById(presetId) ?: return@launch
            updateConfig(preset)
        }
    }

    /**
     * Сохранить текущую конфигурацию как пользовательский конфиг
     */
    fun saveCustomConfig(name: String) {
        viewModelScope.launch {
            val customConfig = _currentConfig.value.copy(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                isPreset = false
            )
            repository.addCustomConfig(customConfig)
        }
    }

    /**
     * Загрузить кастомный конфиг
     */
    fun loadCustomConfig(configId: String) {
        viewModelScope.launch {
            val config = _customConfigs.value.find { it.id == configId } ?: return@launch
            updateConfig(config)
        }
    }

    /**
     * Удалить кастомный конфиг
     */
    fun deleteCustomConfig(configId: String) {
        viewModelScope.launch {
            repository.deleteCustomConfig(configId)
        }
    }

    /**
     * Установить автозапуск сервиса
     */
    fun setAutoStart(enabled: Boolean) {
        viewModelScope.launch {
            _autoStart.value = enabled
            repository.setAutoStart(enabled)
        }
    }

    /**
     * Очистить все настройки
     */
    fun clearAllSettings() {
        viewModelScope.launch {
            stopService()
            repository.clearAllSettings()
            _currentConfig.value = CrosshairConfig.getDefault()
            _customConfigs.value = emptyList()
            _autoStart.value = false
        }
    }
}
