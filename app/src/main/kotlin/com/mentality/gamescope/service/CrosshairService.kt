package com.mentality.gamescope.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import com.mentality.gamescope.data.model.CrosshairConfig
import com.mentality.gamescope.data.repository.CrosshairRepository
import com.mentality.gamescope.notification.NotificationHelper
import com.mentality.gamescope.ui.MainActivity
import com.mentality.gamescope.ui.overlay.CrosshairView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Foreground Service для отрисовки прицела поверх других приложений
 * Использует WindowManager для добавления overlay View на экран
 */
class CrosshairService : Service() {

    companion object {
        private const val CROSSHAIR_VIEW_ID = 9999
    }

    private var crosshairView: CrosshairView? = null
    private var windowManager: WindowManager? = null
    private var currentLayoutParams: WindowManager.LayoutParams? = null
    private var isOverlayVisible = true

    private lateinit var repository: CrosshairRepository
    private lateinit var notificationHelper: NotificationHelper

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate() {
        super.onCreate()

        // Инициализировать сервисы
        repository = CrosshairRepository(this)
        notificationHelper = NotificationHelper(this)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Создать канал уведомлений
        notificationHelper.createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "TOGGLE_OVERLAY" -> toggleOverlay()
            else -> startOverlay()
        }

        return START_STICKY
    }

    /**
     * Запустить оверлей и показать Foreground Service уведомление
     */
    private fun startOverlay() {
        // Показать foreground notification immediately
        val notification = notificationHelper.buildNotification(
            isOverlayVisible,
            MainActivity::class.java
        )
        startForeground(
            NotificationHelper.NOTIFICATION_ID,
            notification.build()
        )

        serviceScope.launch {
            try {
                // Читаем начальное значение ignoreCutout
                val ignoreCutout = repository.getIgnoreCutoutFlow().first()

                // Создать CrosshairView с дефолтным конфигом
                if (crosshairView == null) {
                    crosshairView = CrosshairView(this@CrosshairService)
                    crosshairView!!.setConfig(CrosshairConfig.getDefault())
                }

                // Добавить View в WindowManager
                addViewToWindow(crosshairView!!, ignoreCutout)

                // Обновить статус сервиса
                repository.setServiceRunning(true)

                // Подписаться на изменения конфига
                subscribeToConfigChanges()
                // Подписаться на изменения настройки выреза
                subscribeToIgnoreCutoutChanges()
            } catch (e: Exception) {
                e.printStackTrace()
                stopOverlay()
            }
        }
    }

    /**
     * Остановить оверлей и сервис
     */
    private fun stopOverlay() {
        serviceScope.launch {
            try {
                // Удалить View из WindowManager
                if (crosshairView != null && windowManager != null) {
                    try {
                        windowManager!!.removeView(crosshairView)
                    } catch (e: IllegalArgumentException) {
                        // View уже удален
                    }
                    crosshairView = null
                }

                // Обновить статус сервиса
                repository.setServiceRunning(false)

                // Скрыть notification
                notificationHelper.cancelNotification()

                // Остановить foreground
                stopForeground(STOP_FOREGROUND_REMOVE)

                // Остановить сервис
                stopSelf()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Переключить видимость оверлея (скрыть/показать)
     */
    private fun toggleOverlay() {
        isOverlayVisible = !isOverlayVisible

        if (isOverlayVisible) {
            crosshairView?.visibility = android.view.View.VISIBLE
        } else {
            crosshairView?.visibility = android.view.View.INVISIBLE
        }

        // Обновить уведомление
        val notification = notificationHelper.buildNotification(
            isOverlayVisible,
            MainActivity::class.java
        )
        notificationHelper.updateNotification(notification)
    }

    /**
     * Добавить CrosshairView в WindowManager
     */
    private fun addViewToWindow(view: CrosshairView, ignoreCutout: Boolean) {
        if (windowManager == null) return

        val layoutParams = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

            width = (200 * resources.displayMetrics.density).toInt()
            height = (200 * resources.displayMetrics.density).toInt()

            gravity = Gravity.CENTER
            x = 0
            y = 0

            // Игнорировать вырез камеры: окно распространяется на весь физический экран
            // (Android 11+, min SDK = 31)
            layoutInDisplayCutoutMode = if (ignoreCutout) {
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            } else {
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
            }
        }

        currentLayoutParams = layoutParams

        try {
            windowManager!!.addView(view, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Подписаться на изменения настройки выреза камеры
     */
    private fun subscribeToIgnoreCutoutChanges() {
        serviceScope.launch {
            // Пропускаем первое значение — уже применено при добавлении View
            repository.getIgnoreCutoutFlow().collect { ignoreCutout ->
                updateCutoutMode(ignoreCutout)
            }
        }
    }

    /**
     * Обновить режим выреза в уже добавленном оверлее
     */
    private fun updateCutoutMode(ignoreCutout: Boolean) {
        val params = currentLayoutParams ?: return
        val view = crosshairView ?: return
        val wm = windowManager ?: return

        params.layoutInDisplayCutoutMode = if (ignoreCutout) {
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        } else {
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
        }

        try {
            wm.updateViewLayout(view, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Подписаться на изменения конфигурации прицела
     */
    private fun subscribeToConfigChanges() {
        serviceScope.launch {
            repository.getCurrentConfigFlow().collect { config ->
                crosshairView?.setConfig(config)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        // Remove the view synchronously before cancelling the scope
        // (calling stopOverlay() AFTER cancel() would be a no-op since the scope is dead)
        if (crosshairView != null && windowManager != null) {
            try {
                windowManager!!.removeView(crosshairView)
            } catch (e: Exception) {
                // Already removed or was never added
            }
            crosshairView = null
        }
        serviceScope.cancel()
    }
}
