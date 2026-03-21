# 🎯 Mentality Scope — Crosshair Overlay для мобильных шутеров

> **Версия:** 1.5.0 | **Дата:** 20 июня 2025 г. | **Min SDK:** 31 (Android 12+)

## 📱 Краткое описание

**Mentality Scope** — полнофункциональное Android-приложение прицел-оверлей (Crosshair Overlay) для мобильных шутеров. Отображает кастомизируемый прицел поверх любых приложений через WindowManager Overlay, работает как Foreground Service, поддерживает AMOLED/Dark/Light/System темы, мультиязычность (RU/EN/FR) и гибкую настройку конфигураций.

**Package:** `com.mentality.gamescope`  
**Target SDK:** 35 (Android 15)  
**Min SDK:** 31 (Android 12)  
**versionCode:** 5  

---

## ✨ Возможности v1.5.0

### 🎨 Кастомизируемый прицел
- **3 стиля:** Точка (DOT), Крестик (CROSSHAIR), Круг (CIRCLE)
- **Параметры:** цвет (6 акцентов + Dynamic), размер, толщина, прозрачность
- **CROSSHAIR:** слайдеры «Длина линий» и «Зазор от центра»
- **CIRCLE:** тоггл «Центральный крест» + слайдер «Размер крестика»
- **5 встроенных пресетов:** CS GO, Valorant, PUBG, Default, Classic

### 🎭 Темы оформления
- **4 режима:** Системная / Светлая / Тёмная / AMOLED
- **6 акцентных цветов:** Красный, Синий, Зелёный, Фиолетовый, Оранжевый, Бирюзовый
- **Dynamic Color** (Material You, Android 12+)
- AMOLED — чистый чёрный фон (#000000) для экономии батареи

### 💾 Конфигурации
- Сохранение и загрузка пользовательских конфигов
- **Мульти-выбор при экспорте** — выбирайте нужные конфиги через чекбоксы
- Импорт/экспорт в JSON
- Подтверждение при удалении

### 🌐 Локализация
- Полная поддержка **Русского**, **Английского** и **Французского** языков
- Переключение без перезапуска приложения (AppCompatDelegate)

### 🚀 Сервис и уведомления
- Foreground Service с `foregroundServiceType = specialUse`
- Persistent Notification: скрыть/показать прицел, открыть приложение
- **Автозапуск при включении телефона** (BootReceiver)

---

## 📦 Структура проекта

```
com/mentality/gamescope/
├── ui/
│   ├── MainActivity.kt                    # AppCompatActivity (locale fix)
│   ├── screen/
│   │   ├── HomeScreen.kt                  # Главный экран + доп. настройки стиля
│   │   ├── ConfigsScreen.kt               # Конфиги + мульти-выбор экспорта
│   │   └── SettingsScreen.kt              # Тема, язык, автозапуск, версия
│   ├── component/
│   │   └── CrosshairPreview.kt            # Compose-предпросмотр прицела
│   ├── overlay/
│   │   └── CrosshairView.kt               # Canvas View для отрисовки оверлея
│   └── theme/
│       ├── Theme.kt                       # SYSTEM/LIGHT/DARK/AMOLED темы
│       ├── Color.kt                       # Цветовая палитра
│       └── Typography.kt                  # Типография
├── service/
│   └── CrosshairService.kt                # Foreground Service + WindowManager
├── data/
│   ├── model/
│   │   ├── CrosshairConfig.kt             # Модель + showCenterCross/centerCrossSize
│   │   └── CrosshairStyle.kt              # Enum: DOT, CROSSHAIR, CIRCLE
│   └── repository/
│       └── CrosshairRepository.kt         # DataStore: конфиги + тема + язык
├── viewmodel/
│   ├── CrosshairViewModel.kt              # MVVM StateFlow: все состояния
│   └── CrosshairViewModelFactory.kt
├── notification/
│   └── NotificationHelper.kt
├── permission/
│   └── PermissionManager.kt
├── receiver/
│   ├── NotificationActionReceiver.kt
│   └── BootReceiver.kt                    # Автозапуск при загрузке
└── GameScopeApplication.kt
```

---

## 🗂️ Встроенные пресеты

| Название   | Стиль    | Цвет          | Размер | Толщина | Альфа |
|------------|----------|---------------|--------|---------|-------|
| **CS GO**  | Крестик  | #FF0000       | 1.0    | 2.0     | 0.9   |
| **Valorant** | Крестик | #00FF00      | 0.8    | 2.0     | 0.85  |
| **PUBG**   | Точка    | #FFFF00       | 1.2    | 2.5     | 0.8   |
| **Default** | Крестик | #FFFFFF       | 1.0    | 2.0     | 0.9   |
| **Classic** | Круг    | #0000FF       | 0.9    | 1.5     | 0.95  |

---

## 🛠️ Технологический стек

| Категория              | Технология                          |
|------------------------|-------------------------------------|
| Язык                   | Kotlin 1.9.x                        |
| UI                     | Jetpack Compose + Material3 1.1.2   |
| Архитектура            | MVVM (ViewModel + StateFlow)        |
| Персистентность        | AndroidX DataStore Preferences      |
| Сериализация           | JSONObject (android.org.json)       |
| Асинхронность          | Coroutines + Flow                   |
| Locale                 | AppCompatDelegate                   |
| Сборка                 | Gradle 8 KTS                        |

---

## 📖 Инструкции

- **[BUILD_INSTRUCTIONS_RU.md](BUILD_INSTRUCTIONS_RU.md)** — сборка, JAVA_HOME, типичные ошибки
- **[ARCHITECTURE_RU.md](ARCHITECTURE_RU.md)** — архитектура MVVM, data flow, компоненты

---

## 🎮 Использование

### Главный экран
1. Переключатель **«Активировать прицел»** — запуск/остановка сервиса  
2. **Размер / Толщина / Прозрачность** — слайдеры  
3. **Цвет** — 6 цветных кнопок  
4. **Стиль** — Точка / Крестик / Круг  
5. При выборе **Крестик** — дополнительно «Длина линий» и «Зазор»  
6. При выборе **Круг** — тоггл «Центральный крест» и «Размер крестика»  

### Экран конфигураций
1. **Встроенные пресеты** — горизонтальная прокрутка  
2. **Мои конфиги** — сохранить, загрузить, удалить  
3. **Экспорт** → диалог выбора конфигов (чекбоксы) → JSON-файл  
4. **Импорт** — загрузить JSON  

### Экран настроек
1. **Тема оформления** — SYSTEM / LIGHT / DARK / AMOLED  
2. **Цвет приложения** — 7 вариантов (включая Dynamic на Android 12+)  
3. **Язык** — Системный / Русский / English / Français  
4. **Автозапуск** — при включении телефона  
5. **Исходный код** — кнопка GitHub, ведущая на репозиторий проекта  

---

## ⚠️ Разрешения

```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

При первом запуске приложение запросит:
1. Разрешение на отображение поверх других приложений
2. Разрешение на уведомления (Android 13+)

---

## 📄 Архив

Предыдущие версии документации хранятся в `old_data/v1.3.0/docs/`.

---

