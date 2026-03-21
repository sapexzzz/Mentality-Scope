package com.mentality.gamescope.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mentality.gamescope.data.repository.CrosshairRepository
import com.mentality.gamescope.service.CrosshairService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver для запуска сервиса при загрузке системы
 * Запускает CrosshairService только если включен автозапуск в настройках
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = CrosshairRepository(context)
                val autoStart = repository.getAutoStartFlow().first()
                if (autoStart) {
                    val serviceIntent = Intent(context, CrosshairService::class.java)
                    context.startForegroundService(serviceIntent)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
