package com.mentality.gamescope.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mentality.gamescope.notification.NotificationHelper
import com.mentality.gamescope.service.CrosshairService

/**
 * BroadcastReceiver для обработки экшнов из уведомления сервиса
 */
class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            NotificationHelper.ACTION_TOGGLE_OVERLAY -> {
                // Отправили команду на toggle в сервис
                val serviceIntent = Intent(context, CrosshairService::class.java).apply {
                    action = "TOGGLE_OVERLAY"
                }
                context.startService(serviceIntent)
            }
            NotificationHelper.ACTION_OPEN_APP -> {
                // Приложение откроется через PendingIntent в самом NotificationHelper
            }
        }
    }
}
