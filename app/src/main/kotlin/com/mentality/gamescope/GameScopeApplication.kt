package com.mentality.gamescope

import android.app.Application

/**
 * Application класс для инициализации Mentality Scope
 * Может использоваться для глобальной инициализации (Crashlytics, Analytics, и т.д.)
 */
class GameScopeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Инициализировать глобальные ресурсы
        // Здесь можно добавить Crashlytics, Firebase, и т.д.
    }
}
