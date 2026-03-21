package com.mentality.gamescope.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mentality.gamescope.R
import com.mentality.gamescope.receiver.NotificationActionReceiver

/**
 * Вспомогательный класс для управления уведомлениями
 */
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "crosshair_service"
        const val NOTIFICATION_ID = 1
        const val ACTION_TOGGLE_OVERLAY = "com.mentality.gamescope.TOGGLE_OVERLAY"
        const val ACTION_OPEN_APP = "com.mentality.gamescope.OPEN_APP"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * Создать канал уведомлений (для Android 8+)
     */
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Crosshair Service",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Уведомление для сервиса прицела"
                setSound(null, null) // Без звука
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Построить уведомление сервиса
     */
    fun buildNotification(
        isOverlayVisible: Boolean,
        mainActivityClass: Class<*>
    ): NotificationCompat.Builder {
        val toggleAction = if (isOverlayVisible) {
            Pair("Скрыть прицел", ACTION_TOGGLE_OVERLAY)
        } else {
            Pair("Показать прицел", ACTION_TOGGLE_OVERLAY)
        }

        val toggleIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = toggleAction.second
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openIntent = Intent(context, mainActivityClass).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            1,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Mentality Scope")
            .setContentText("Прицел активен")
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(openPendingIntent)
            .addAction(
                R.drawable.ic_launcher,
                toggleAction.first,
                togglePendingIntent
            )
            .addAction(
                R.drawable.ic_launcher,
                "Открыть приложение",
                openPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .setAutoCancel(false)
    }

    /**
     * Показать уведомление
     */
    fun showNotification(
        notification: NotificationCompat.Builder,
        notificationId: Int = NOTIFICATION_ID
    ) {
        notificationManager.notify(notificationId, notification.build())
    }

    /**
     * Скрыть уведомление
     */
    fun cancelNotification(notificationId: Int = NOTIFICATION_ID) {
        notificationManager.cancel(notificationId)
    }

    /**
     * Обновить уведомление
     */
    fun updateNotification(
        notification: NotificationCompat.Builder,
        notificationId: Int = NOTIFICATION_ID
    ) {
        notificationManager.notify(notificationId, notification.build())
    }
}
