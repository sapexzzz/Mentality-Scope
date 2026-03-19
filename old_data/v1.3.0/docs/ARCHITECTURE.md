# Mentality Scope — Архитектура и обзор

## 🏗️ Архитектура MVVM

```
┌─────────────────────────────────────────────────────────────────────┐
│                          UI Layer (Compose)                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐  │
│  │ HomeScreen   │  │ ConfigsScreen│  │ SettingsScreen           │  │
│  └──────────────┘  └──────────────┘  └──────────────────────────┘  │
│         ↓                  ↓                      ↓                   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │            MainActivity (NavigationBar)                      │   │
│  └─────────────────────────────────────────────────────────────┘   │
└────────────────────────────────┬────────────────────────────────────┘
                                 ↓
┌─────────────────────────────────────────────────────────────────────┐
│                     ViewModel Layer (MVVM)                          │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │ CrosshairViewModel                                         │    │
│  │  ├─ currentConfig: StateFlow<CrosshairConfig>             │    │
│  │  ├─ isServiceRunning: StateFlow<Boolean>                  │    │
│  │  ├─ customConfigs: StateFlow<List<CrosshairConfig>>       │    │
│  │  ├─ presets: StateFlow<List<CrosshairConfig>>             │    │
│  │  ├─ autoStart: StateFlow<Boolean>                         │    │
│  │  └─ Methods: startService(), stopService(), setSize(), ...│    │
│  └────────────────────────────────────────────────────────────┘    │
└────────────────────────────────┬────────────────────────────────────┘
                                 ↓
┌─────────────────────────────────────────────────────────────────────┐
│                    Repository Layer (Data)                          │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │ CrosshairRepository                      ┌──────────────┐  │    │
│  │ (Manages DataStore)                      │ Current      │  │    │
│  │  ├─ getCurrentConfigFlow()                │ Config       │  │    │
│  │  ├─ saveCurrentConfig()                  │ (JSON)       │  │    │
│  │  ├─ getCustomConfigsFlow()                └──────────────┘  │    │
│  │  ├─ addCustomConfig()                                       │   │
│  │  ├─ deleteCustomConfig()                   ┌─────────────┐ │    │
│  │  ├─ getPresets()                           │ Custom      │ │    │
│  │  └─ getAutoStartFlow()                     │ Configs     │ │    │
│  │                                            │ (JSON List) │ │    │
│  └────────────────────────────────────────────┴─────────────┘─│    │
│                                    DataStore Preferences       │    │
└────────────────────────────────────────────────────────────────────┘
```

## 🔄 Data Flow

### 1. **UI → ViewModel → Repository → DataStore**

```
User Action (Home Screen)
       ↓
viewModel.setSize(1.5f)
       ↓
_currentConfig.value = config.copy(size = 1.5f)
       ↓
repository.saveCurrentConfig(config)
       ↓
DataStore.edit { prefs[CURRENT_CONFIG_KEY] = json }
       ↓
CrosshairView.setConfig(newConfig)  // Реактивное обновление
       ↓
Canvas.invalidate() → Переоисовка прицела
```

### 2. **Service ↔ UI (Bidirectional)**

```
startService()
    ↓
CrosshairService.onStartCommand()
    ↓
startForeground(notification)  // Persistent notification
subscribeToConfigChanges()
    ↓
repository.getCurrentConfigFlow().collect { config →
    crosshairView.setConfig(config)  // Реактивное обновление
}
    ↓
WindowManager.addView(crosshairView)  // Рисование поверх
```

---

## 📊 Компоненты и их ответственность

### **Models** (`data/model/`)
- **CrosshairConfig**: Полная конфигурация прицела (цвет, размер, стиль, толщина, альфа)
- **CrosshairStyle**: Enum из 3 стилей (DOT, CROSSHAIR, CIRCLE)
- **Встроенные пресеты**: 5 готовых конфигов для популярных игр

### **Repository** (`data/repository/`)
- **CrosshairRepository**: Единственная точка для доступа к persisted data
- Использует DataStore (Preferences)
- Все операции асинхронные (Coroutines + Suspend functions)
- Сериализация/десериализация через JSONObject (android.org.json)

### **Service** (`service/`)
- **CrosshairService**: Foreground Service для отрисовки оверлея
- WindowManager для добавления CrosshairView на экран
- Persistent Notification с управлением (скрыть/показать)
- BroadcastReceiver для обработки экшнов из notification

### **UI Views** (`ui/overlay/`)
- **CrosshairView**: Custom Android View (Canvas-based)
- 60 FPS оптимизация через invalidation вместо перестройки
- Поддержка динамических параметров (color, size, alpha, thickness, style)

### **UI Screens** (`ui/screen/`)
- **HomeScreen**: Включение/выключение, слайдеры, выбор цвета и стиля
- **ConfigsScreen**: Встроенные пресеты (горизонтальная лента) + сохраненные конфиги
- **SettingsScreen**: Автозапуск, версия, очистка всех данных

### **UI Components** (`ui/component/`)
- **CrosshairPreview**: Compose Canvas компонент для предпросмотра прицела
- Использует Compose вместо Canvas View для гладкой интеграции

### **ViewModel** (`viewmodel/`)
- **CrosshairViewModel**: MVVM state holder
- StateFlow для reactive updates
- Управление жизненным циклом Service
- Factory для dependency injection

### **Notifications** (`notification/`)
- **NotificationHelper**: 
  - Создание и управление Persistent Notification
  - ACTION_BUTTONS для toggle overlay и open app
  - Без звука (IMPORTANCE_MIN)

### **Permissions** (`permission/`)
- **PermissionManager**:
  - Проверка SYSTEM_ALERT_WINDOW через Settings.canDrawOverlays()
  - POST_NOTIFICATIONS (Android 13+)
  - FOREGROUND_SERVICE (только Manifest)

### **Receiver** (`receiver/`)
- **NotificationActionReceiver**: BroadcastReceiver
- Обработка экшнов из notification (toggle, open app)

### **Theme** (`ui/theme/`)
- **Theme.kt**: Material Design 3 динамические цвета + fallback
- **Color.kt**: Полная цветовая палитра (light + dark)
- **Typography.kt**: Material 3 типография

---

## 🎯 Key Features Implementation

### ✅ **Overlay (поверх других приложений)**

```kotlin
// CrosshairService.kt
private fun addViewToWindow(view: CrosshairView) {
    val layoutParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY  // Android 5+
        flags = FLAG_NOT_FOCUSABLE or FLAG_NOT_TOUCHABLE
        gravity = Gravity.CENTER
    }
    windowManager.addView(view, layoutParams)
}
```

**Ключевые моменты:**
- FLAG_NOT_FOCUSABLE: Прицел не перехватывает клики
- FLAG_NOT_TOUCHABLE: Невозможно потрогать прицел
- TYPE_APPLICATION_OVERLAY: Работает на Android 5+

### ✅ **Foreground Service (Android 12+)**

```kotlin
override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    startForeground(NOTIFICATION_ID, notification.build())
    return START_STICKY  // Сервис перезапускается при убиении
}
```

**Требования Android 12+:**
- foregroundServiceType="systemAlertWindow" в Manifest
- Persistent Notification (not cancellable)
- startForeground() в onStartCommand()

### ✅ **DataStore (Modern Preferences)**

```kotlin
// Сохранение конфига
dataStore.edit { preferences →
    preferences[stringPreferencesKey(CURRENT_CONFIG_KEY)] = jsonString
}

// Чтение с реактивностью
repository.getCurrentConfigFlow().collect { config →
    // Обновить UI
}
```

### ✅ **Material Design 3 (Jetpack Compose)**

```kotlin
Scaffold(
    topBar = { CenterAlignedTopAppBar(...) },
    bottomBar = { NavigationBar(...) }
) { ... }

// Все компоненты используют Material 3:
// - Slider, Switch, Button, Card, TextField, Alert
// - Dynamic colors (Android 12+) + Fallback static colors
```

### ✅ **MVVM с StateFlow**

```kotlin
class CrosshairViewModel(...) : ViewModel() {
    private val _currentConfig = MutableStateFlow(CrosshairConfig.getDefault())
    val currentConfig: StateFlow<CrosshairConfig> = _currentConfig.asStateFlow()
    
    fun setSize(size: Float) {
        val newConfig = _currentConfig.value.copy(size = size)
        updateConfig(newConfig)  // Асинхронно сохраняется в DataStore
    }
}

// В UI:
val config by viewModel.currentConfig.collectAsState()
```

---

## 🔐 Permissions & Security

| Разрешение | Android Version | Как запрашивается | Зачем |
|-----------|----------------|------------------|-------|
| SYSTEM_ALERT_WINDOW | 6+ | Settings Activity | Рисование поверх других приложений |
| POST_NOTIFICATIONS | 13+ | Runtime Request | Persistent Notification |
| FOREGROUND_SERVICE | 12+ | Manifest only | Foreground Service (no runtime) |
| FOREGROUND_SERVICE_SYSTEM_ALERT_WINDOW | 12+ | Manifest only | Type для service |

---

## 🎨 Цветовая система

### Light Theme (Default)
- **Primary**: #E31C23 (Красный)
- **Secondary**: #757575 (Серый)
- **Tertiary**: #536DFE (Синий)
- **Background**: #FFFFFF
- **Surface**: #FFFFFF

### Dark Theme (Values-night)
- **Primary**: #FF5252 (Светло-красный)
- **Secondary**: #A4A4A4 (Светлый серый)
- **Tertiary**: #7A8DFF (Светло-синий)
- **Background**: #1A1C1E
- **Surface**: #1D1D1D

---

## 📱 UI Layout

### **HomeScreen**
```
┌────────────────────────────────┐
│ CenterAlignedTopAppBar         │  ← Заголовок "Mentality Scope"
├────────────────────────────────┤
│                                │
│  ┌──────────────────────────┐  │
│  │ CrosshairPreview (Card)  │  │  ← Предпросмотр текущего прицела
│  └──────────────────────────┘  │
│                                │
│  ┌──────────────────────────┐  │
│  │ Switch: Активировать    │  │  ← Включение/выключение сервиса
│  └──────────────────────────┘  │
│                                │
│  LazyColumn со слайдерами:     │
│  ├─ Размер (0.5 - 3.0)         │
│  ├─ Толщина (1.0 - 4.0)        │
│  ├─ Прозрачность (0.3 - 1.0)  │
│  ├─ Цвет (6 вариантов)         │
│  └─ Стиль (DOT/CROSSHAIR/CIR...│
│                                │
├────────────────────────────────┤
│ NavigationBar (3 вкладки)      │  ← Home | Configs | Settings
└────────────────────────────────┘
```

### **ConfigsScreen**
```
┌────────────────────────────────┐
│ CenterAlignedTopAppBar         │
├────────────────────────────────┤
│ Встроенные пресеты:            │
│ [CS GO][Valorant][PUBG][...]   │  ← Горизонтальный скролл
│                                │
│ Мои конфиги              [+]   │  ← Кнопка сохранить
│                                │
│ ┌──────────────────────────┐   │
│ │ Custom Config 1          │   │
│ │ [Использовать] [✕ Delete]│   │
│ └──────────────────────────┘   │
│ ┌──────────────────────────┐   │
│ │ Custom Config 2          │   │
│ │ [Использовать] [✕ Delete]│   │
│ └──────────────────────────┘   │
│                                │
├────────────────────────────────┤
│ NavigationBar                  │
└────────────────────────────────┘
```

### **SettingsScreen**
```
┌────────────────────────────────┐
│ CenterAlignedTopAppBar         │
├────────────────────────────────┤
│ Основные                       │
│ ┌──────────────────────────┐   │
│ │ Автозапуск при старте[🎛]│   │
│ └──────────────────────────┘   │
│                                │
│ О приложении                   │
│ ┌──────────────────────────┐   │
│ │ Версия  |  1.0.0         │   │
│ │ Разработчик: Mentality  │   │
│ └──────────────────────────┘   │
│                                │
│ Данные                         │
│ ┌──────────────────────────┐   │
│ │ [Очистить все настройки] │   │ ← Красная кнопка
│ └──────────────────────────┘   │
│                                │
├────────────────────────────────┤
│ NavigationBar                  │
└────────────────────────────────┘
```

---

## 🚀 Performance Optimizations

### 1. **CrosshairView rendering (60 FPS)**
```kotlin
// Используем invalidate() вместо перестройки всего дерева
fun setConfig(newConfig: CrosshairConfig) {
    config = newConfig
    invalidate()  // Только перерисовка одного View
}
```

### 2. **WindowManager flags оптимизированы**
```kotlin
flags = FLAG_NOT_FOCUSABLE or FLAG_NOT_TOUCHABLE  // Минимум обработки событий
```

### 3. **StateFlow вместо LiveData**
```kotlin
// Coroutines-native, более эффективно
val currentConfig: StateFlow<CrosshairConfig>
```

### 4. **Canvas вместо VectorDrawable**
```kotlin
// Прямая отрисовка быстрее чем через drawable inflation
override fun onDraw(canvas: Canvas) { ... }
```

---

## 🔄 Data Persistence Flow

```
CrosshairView ← CrosshairService ← Repository ← DataStore
    ↑                                               ↓
    └───────── ViewModel ←──────────────────────────┘
                  ↓
              UI Updates
```

**Пример:**
1. User меняет размер слайдером (HomeScreen)
2. `viewModel.setSize(1.5f)` 
3. ViewModel обновляет `_currentConfig.value`
4. ViewModel вызывает `repository.saveCurrentConfig()`
5. Repository сохраняет в DataStore (JSON)
6. CrosshairService подписан на конфигурацию
7. CrosshairView получает новый конфиг и перерисовывается
8. Пользователь видит изменение на оверлее в реальном времени

---

## 📦 Зависимости проекта

```
androidx.compose:compose-bom:2023.10.01
├── compose.ui
├── compose.material3
├── compose.material-icons-extended
└── compose.animation

androidx.lifecycle
├── lifecycle-runtime-ktx
├── lifecycle-viewmodel-ktx
├── lifecycle-viewmodel-compose
└── lifecycle-runtime-compose

androidx.datastore
├── datastore-preferences
└── datastore-preferences-core

androidx.activity:activity-compose:1.8.0

kotlinx.coroutines
├── coroutines-core
└── coroutines-android

android.material:material:1.10.0
```

---

## 🎯 Готовое приложение поддерживает:

✅ Android 12+ (Min SDK 31)  
✅ Android 15 (Target SDK 35)  
✅ Material Design 3  
✅ Dark Mode  
✅ MVVM Architecture  
✅ Coroutines & DataStore  
✅ Foreground Service  
✅ Runtime Permissions  
✅ 60 FPS Overlay  
✅ 5 встроенных пресетов  
✅ Кастомные конфиги  
✅ Persistent Notification  

---

**Дата:** 19 марта 2026 г.  
**Версия:** 1.0.0  
**Package:** com.mentality.gamescope  
**Архитектура:** MVVM + Jetpack Compose + Coroutines + DataStore
