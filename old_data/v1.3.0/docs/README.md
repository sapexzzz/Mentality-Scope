# 🎯 Mentality Scope — Crosshair Overlay для мобильных шутеров

## 📱 Краткое описание

**Mentality Scope** — полнофункциональное Android приложение-помощник прицеливания (Crosshair Overlay) для мобильных шутеров. Приложение отображает кастомизируемый прицел поверх других приложений через Overlay, работает как Foreground Service на Android 12+, с современным Material Design 3 UI на Jetpack Compose.

**Package:** `com.mentality.gamescope`  
**Target SDK:** 35 (Android 15)  
**Min SDK:** 31 (Android 12)  
**Версия:** 1.0.0  
**Дата создания:** 19 марта 2026 г.

---

## ✨ Ключевые особенности

### 🎨 **Кастомизируемый прицел**
- **3 стиля:** Точка (DOT), Крестик (CROSSHAIR), Круг (CIRCLE)
- **Параметры:** цвет, размер, толщина линий, прозрачность (alpha)
- **5 встроенных пресетов:** CS GO, Valorant, PUBG, Default, Classic
- **Кастомные конфиги:** Сохранение и загрузка собственных конфигураций

### 🚀 **Производительность**
- Оптимизирован на 60 FPS
- Минимальное потребление ресурсов благодаря WindowManager.FLAG_NOT_FOCUSABLE
- Efficient rendering через Canvas invalidation

### 🔒 **Безопасность и разрешения**
- Полная поддержка Android 12+ runtime permissions
- SYSTEM_ALERT_WINDOW управляется через Settings
- POST_NOTIFICATIONS (Android 13+) с graceful fallback
- FOREGROUND_SERVICE type: systemAlertWindow

### 🎛️ **Управление сервисом**
- Persistent Notification с кнопками управления
- Toggle (скрыть/показать) прицел из notification
- Автозапуск при включении телефона (опционально)

### 🎨 **Material Design 3 UI**
- **3 экрана** с NavigationBar: Home, Configs, Settings
- **Dark Mode поддержка** с динамическими цветами (Android 12+)
- **Современные компоненты:** Sliders, Switches, Cards, AlertDialogs
- **Полностью на Jetpack Compose** (без XML layouts)

### 💾 **Персистентность данных**
- **DataStore** для сохранения конфигов
- Асинхронные операции через Coroutines
- JSON-based сериализация

### 🏗️ **Архитектура**
- **MVVM** с ViewModel и StateFlow
- **Repository Pattern** для доступа к данным
- **Coroutines** для асинхронности
- **Dependency Injection** через Factory

---

## 📦 Структура файлов

### Основные компоненты

```
com/mentality/gamescope/
├── ui/
│   ├── MainActivity.kt                    # Entry point
│   ├── screen/
│   │   ├── HomeScreen.kt                  # Главный экран (вкл/выкл, настройки)
│   │   ├── ConfigsScreen.kt               # Управление конфигами
│   │   └── SettingsScreen.kt              # Системные настройки
│   ├── component/
│   │   └── CrosshairPreview.kt            # Компонент предпросмотра прицела
│   ├── overlay/
│   │   └── CrosshairView.kt               # Custom View для отрисовки (Canvas)
│   └── theme/
│       ├── Theme.kt                       # Material Design 3 тема
│       ├── Color.kt                       # Цветовая палитра
│       └── Typography.kt                  # Типография
├── service/
│   └── CrosshairService.kt                # Foreground Service + WindowManager
├── data/
│   ├── model/
│   │   ├── CrosshairConfig.kt             # Модель конфигурации + 5 пресетов
│   │   └── CrosshairStyle.kt              # Enum стилей прицела
│   └── repository/
│       └── CrosshairRepository.kt         # DataStore управление
├── viewmodel/
│   ├── CrosshairViewModel.kt              # MVVM State Management
│   └── CrosshairViewModelFactory.kt       # Factory для DI
├── notification/
│   └── NotificationHelper.kt              # Persistent Notification
├── permission/
│   └── PermissionManager.kt               # Управление разрешениями
├── receiver/
│   └── NotificationActionReceiver.kt      # Обработка экшнов из notification
└── GameScopeApplication.kt                # Application класс
```

### Ресурсные файлы

```
res/
├── values/
│   ├── strings.xml                        # Локализация (RU)
│   ├── colors.xml                         # Цвета light theme
│   └── themes.xml                         # Material 3 стили
├── values-night/
│   └── colors.xml                         # Цвета dark theme
├── drawable/
│   ├── ic_launcher.xml                    # Иконка приложения
│   └── ic_launcher_foreground.xml         # Foreground иконка
└── xml/
    ├── data_extraction_rules.xml
    └── backup_scheme.xml
```

---

## 🎯 Встроенные пресеты

| Название | Стиль | Цвет | Размер | Толщина | Альфа |
|----------|-------|------|--------|---------|-------|
| **CS GO** | Крестик | Красный (#FF0000) | 1.0 | 2.0 | 0.9 |
| **Valorant** | Крестик | Зеленый (#00FF00) | 0.8 | 2.0 | 0.85 |
| **PUBG** | Точка | Желтый (#FFFF00) | 1.2 | 2.5 | 0.8 |
| **Default** | Крестик | Белый (#FFFFFF) | 1.0 | 2.0 | 0.9 |
| **Classic** | Круг | Синий (#0000FF) | 0.9 | 1.5 | 0.95 |

---

## 🛠️ Технологический стек

### Kotlin & Framework
- **Kotlin** 1.9.21
- **Jetpack Compose** 2023.10.01 (Material 3)
- **AndroidX** (latest)

### Architecture & Async
- **MVVM** для State Management
- **ViewModel** + **StateFlow** для reaktivity
- **Coroutines** для асинхронности
- **DataStore** для персистентности

### UI & Graphics
- **Material Design 3** компоненты
- **Jetpack Compose** (полностью на Compose, без XML)
- **Canvas** для отрисовки прицела
- **WindowManager** для Overlay

### Permissions & Services
- **ActivityResultContracts** для runtime permissions
- **Foreground Service** с Notification
- **BroadcastReceiver** для управления

### Target Platform
- **Target SDK:** 35 (Android 15)
- **Min SDK:** 31 (Android 12)
- **Поддержка Java:** 17+

---

## 📋 Требования

### Software
- **Android Studio** 2023.1.0+ (или cli tools)
- **Java 17+**
- **Gradle 8.2+**
- **Android SDK 31** и **Android SDK 35**

### Hardware
- **Устройство/эмулятор:** Android 12+ (API 31+)
- **Памяти:** Минимум 2GB для эмулятора

---

## 🚀 Быстрый старт

### 1. Откройте проект в Android Studio

```bash
# В Android Studio
File → Open → /home/mentality/scripts/android_application
```

### 2. Дождитесь синхронизации Gradle

Android Studio автоматически загрузит все зависимости.

### 3. Соберите приложение

```bash
# В Android Studio
Build → Build Bundle(s)/APK(s) → Build APK(s)
```

### 4. Запустите на устройстве

```bash
# Debug
./gradlew installDebug

# Или через adb
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 📖 Подробная документация

1. **[BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md)**
   - Пошаговая инструкция по сборке
   - Типичные ошибки и решения
   - Тестирование на эмуляторе и устройстве

2. **[ARCHITECTURE.md](ARCHITECTURE.md)**
   - Подробная архитектура MVVM
   - Data flow диаграммы
   - Компоненты и их ответственность
   - Оптимизация производительности

---

## 🔑 Key Implementation Details

### WindowManager для Overlay

```kotlin
val layoutParams = WindowManager.LayoutParams().apply {
    type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY  // Android 5+
    flags = FLAG_NOT_FOCUSABLE or FLAG_NOT_TOUCHABLE  // Non-interactive
    gravity = Gravity.CENTER
    x = 0
    y = 0
}
windowManager.addView(crosshairView, layoutParams)
```

### Foreground Service (Android 12+)

```kotlin
override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    startForeground(NOTIFICATION_ID, notification.build())
    return START_STICKY
}
```

### StateFlow для Reactivity

```kotlin
// ViewModel
val currentConfig: StateFlow<CrosshairConfig> = ...

// UI (Compose)
val config by viewModel.currentConfig.collectAsState()
CrosshairView.setConfig(config)  // Автоматически обновляется
```

### DataStore для Persistence

```kotlin
repository.getCurrentConfigFlow().collect { config →
    // Реактивное обновление при изменении в DataStore
}

repository.saveCurrentConfig(newConfig)  // Асинхронное сохранение
```

---

## 🎮 Использование

### На главном экране (Home)

1. **Включить/выключить сервис** — Switch "Активировать прицел"
2. **Настроить прицел:**
   - **Размер** — слайдер 0.5x - 3.0x
   - **Толщина** — слайдер 1.0 - 4.0
   - **Прозрачность** — слайдер 30% - 100%
   - **Цвет** — 6 предустановленных цветов
   - **Стиль** — Точка, Крестик, Круг
3. **Предпросмотр** — видеть текущий прицел в Card

### На экране конфигов (Configs)

1. **Встроенные пресеты** — горизонтальная лента (CS GO, Valorant, PUBG, Default, Classic)
2. **Мои конфиги** — список сохраненных конфигураций
3. **Сохранить текущий** — кнопка "Сохранить" + диалог ввода имени
4. **Загрузить конфиг** — кнопка "Использовать"
5. **Удалить** — иконка корзины

### На экране настроек (Settings)

1. **Автозапуск** — включить запуск при старте телефона
2. **О приложении** — версия и разработчик
3. **Очистить все** — красная кнопка с подтверждением

---

## ⚠️ Важные замечания

### Разрешения при первом запуске

1. **SYSTEM_ALERT_WINDOW:** Приложение откроет Settings → Applications → Mentality Scope → Display over other apps → Allow
2. **POST_NOTIFICATIONS** (Android 13+): Runtime request — разрешить отправку уведомлений

### Foreground Service Notification

- Уведомление **не удаляется** (persistent)
- Можно **скрыть/показать** прицель через кнопку в notification
- Можно **открыть приложение** из notification

### Performance

- Оверлей работает на **60 FPS**
- Минимальное потребление батареи благодаря FLAG_NOT_FOCUSABLE
- Canvas отрисовка оптимизирована

---

## 🔮 Возможные расширения в будущем

- ✨ Google Play Store публикация
- 📊 Analytics & Crash reporting
- 🎯 Поддержка жестов (pinch-zoom, drag)
- 🎤 Voice commands для toggle
- 🌐 Cloud синхронизация конфигов
- 🎮 Интеграция с популярными шутерами
- 📈 Advanced statistics

---

## 📞 Контакты & поддержка

**Разработчик:** Mentality Team  
**Email:** (может быть добавлено)  
**GitHub:** (может быть добавлено)  
**Версия:** 1.0.0 (19.03.2026)

---

## 📄 Лицензия

Проект создан в учебных целях. Используйте на свой риск.

---

## ✅ Всё готово к разработке!

Проект полностью реализован и готов к:
- ✅ Сборке APK/AAB
- ✅ Публикации в Google Play Store
- ✅ Дальнейшему развитию и обновлениям
- ✅ Интеграции сторонних сервисов

**Начните с:** [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md)  
**Изучите архитектуру:** [ARCHITECTURE.md](ARCHITECTURE.md)
