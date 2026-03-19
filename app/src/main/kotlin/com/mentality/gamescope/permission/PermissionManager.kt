package com.mentality.gamescope.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Менеджер для управления разрешениями приложения
 */
class PermissionManager(private val context: Context) {

    /**
     * Проверить, может ли приложение рисовать поверх других приложений
     */
    fun canDrawOverlays(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    /**
     * Открыть Settings для разрешения SYSTEM_ALERT_WINDOW
     */
    fun requestDrawOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        context.startActivity(intent)
    }

    /**
     * Получить список разрешений для запроса
     */
    fun getRequiredPermissions(): List<String> {
        val permissions = mutableListOf<String>()

        // POST_NOTIFICATIONS требуется на Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        return permissions
    }

    /**
     * Получить разрешение для SYSTEM_ALERT_WINDOW через ActivityResultLauncher
     */
    fun registerDrawOverlayPermissionLauncher() {
        // Для SYSTEM_ALERT_WINDOW используется специальный механизм через Settings
        requestDrawOverlayPermission()
    }

    /**
     * Разрешение уже запрошено?
     */
    fun isPermissionAlreadyGranted(): Boolean {
        return canDrawOverlays()
    }

    /**
     * Получить текстовое описание для требуемого разрешения
     */
    fun getPermissionDescription(permission: String): String {
        return when (permission) {
            Manifest.permission.POST_NOTIFICATIONS -> "Разрешение на отправку уведомлений для управления сервисом"
            Manifest.permission.SYSTEM_ALERT_WINDOW -> "Разрешение на отображение прицела поверх других приложений"
            else -> "Требуемое разрешение"
        }
    }
}
