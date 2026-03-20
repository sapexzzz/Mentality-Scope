# Mentality Scope — Архитектура v1.5.0

## 🏗️ Архитектура MVVM

```
┌─────────────────────────────────────────────────────────────────────┐
│                          UI Layer (Compose)                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐  │
│  │  HomeScreen  │  │ConfigsScreen │  │    SettingsScreen        │  │
│  └──────────────┘  └──────────────┘  └──────────────────────────┘  │
│         ↓                  ↓                      ↓                  │
│  ┌────────────────────────────────────────────────────────────────┐ │
│  │           MainActivity (AppCompatActivity + NavBar)            │ │
│  └────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────┬────────────────────────────────────┘
                                 ↓
┌─────────────────────────────────────────────────────────────────────┐
│                     ViewModel Layer                                 │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │ CrosshairViewModel                                         │    │
│  │  ├─ currentConfig:  StateFlow<CrosshairConfig>             │    │
│  │  ├─ isServiceRunning: StateFlow<Boolean>                   │    │
│  │  ├─ customConfigs:  StateFlow<List<CrosshairConfig>>       │    │
│  │  ├─ presets:        StateFlow<List<CrosshairConfig>>       │    │
│  │  ├─ autoStart:      StateFlow<Boolean>                     │    │
│  │  ├─ appTheme:       StateFlow<String>   // акцент цвет     │    │
│  │  ├─ appDarkMode:    StateFlow<String>   // SYSTEM/LIGHT/   │    │
│  │  │                                      // DARK/AMOLED     │    │
│  │  ├─ appLanguage:    StateFlow<String>                      │    │
│  │  └─ setSize/setColor/setStyle/setDarkMode/...              │    │
│  └────────────────────────────────────────────────────────────┘    │
└────────────────────────────────┬────────────────────────────────────┘
                                 ↓
┌─────────────────────────────────────────────────────────────────────┐
│                    Repository Layer (DataStore)                     │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │ CrosshairRepository                                        │    │
│  │  ├─ getCurrentConfigFlow() / saveCurrentConfig()           │    │
│  │  ├─ getCustomConfigsFlow() / addCustomConfig()             │    │
│  │  ├─ getAutoStartFlow()   / setAutoStart()                  │    │
│  │  ├─ getAppThemeFlow()    / setAppTheme()                   │    │
│  │  ├─ getAppDarkModeFlow() / setAppDarkMode()  ← new 1.4.0  │    │
│  │  ├─ getAppLanguageFlow() / setAppLanguage()                │    │
│  │  ├─ exportConfigsJson()  / importConfigsJson()             │    │
│  │  └─ getPresets(): List<CrosshairConfig>                    │    │
│  └────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Data Flow

### Изменение настройки прицела
```
User (Slider) → viewModel.setSize(1.5f)
    → _currentConfig.update { it.copy(size = 1.5f) }
    → repository.saveCurrentConfig(config)   [suspend]
    → DataStore.edit { prefs[CURRENT_CONFIG_KEY] = json }
    → CrosshairService получает Flow-обновление
    → crosshairView.setConfig(newConfig) → Canvas.invalidate()
```

### Смена темы оформления (v1.4.0)
```
User (DarkModePicker) → viewModel.setAppDarkMode("AMOLED")
    → repository.setAppDarkMode("AMOLED")
    → DataStore.edit { prefs[APP_DARK_MODE_KEY] = "AMOLED" }
    → MainActivity.appDarkMode collectAsState()
    → MentalityScopeTheme(darkMode = "AMOLED") → buildAmoledScheme()
    → UI полностью перерисовывается
```

### Смена языка
```
User (SettingsScreen) → viewModel.setAppLanguage("en")
    → repository.setAppLanguage("en")
    → AppCompatDelegate.setApplicationLocales(LocaleListCompat)
    → Activity пересоздаётся, LocaleList применяется
```

### Мульти-выбор экспорта (v1.4.0)
```
User нажимает "Экспорт" → showExportDialog = true
    → ExportSelectDialog (чекбоксы по каждому конфигу)
    → User выбирает нужные → onExport(selectedIds: Set<String>)
    → viewModel.getExportJson(ids) фильтрует список
    → exportLauncher.launch("filename.json")
    → ContentResolver.openOutputStream → запись JSON
```

---

## 📊 Компоненты

### **Models** (`data/model/`)
| Класс | Назначение |
|-------|-----------|
| `CrosshairConfig` | Полная конфигурация прицела. Поля: id, name, color, size, thickness, alpha, style, lineLength, gapSize, **showCenterCross**, **centerCrossSize** |
| `CrosshairStyle` | Enum: DOT, CROSSHAIR, CIRCLE |

### **Repository** (`data/repository/`)
- Единственная точка доступа к DataStore
- Сериализация через `JSONObject` (без сторонних библиотек)
- Все операции — suspend-функции или Flow

**Ключи DataStore:**
```
CURRENT_CONFIG_KEY   — текущая конфигурация прицела (JSON)
CUSTOM_CONFIGS_KEY   — список пользовательских конфигов (JSON array)
AUTO_START_KEY       — включён ли автозапуск (Boolean)
APP_THEME_KEY        — акцентный цвет (String: RED/BLUE/GREEN/...)
APP_DARK_MODE_KEY    — режим темы (String: SYSTEM/LIGHT/DARK/AMOLED)
APP_LANGUAGE_KEY     — язык (String: system/ru/en/fr)
```

### **Theme** (`ui/theme/Theme.kt`)

```kotlin
MentalityScopeTheme(appTheme = "RED", darkMode = "AMOLED") { ... }
```

| darkMode   | Описание                                      |
|------------|-----------------------------------------------|
| `SYSTEM`   | Следует системной теме устройства             |
| `LIGHT`    | Всегда светлая тема                           |
| `DARK`     | Всегда тёмная тема                            |
| `AMOLED`   | Чистый чёрный (#000000) фон, surface = #0D0D0D |

Акцентные цвета и их `onPrimary`:
```kotlin
"RED"     → Color(0xFFB71C1C) / White
"BLUE"    → Color(0xFF1565C0) / White
"GREEN"   → Color(0xFF2E7D32) / White
"PURPLE"  → Color(0xFF6A1B9A) / White
"ORANGE"  → Color(0xFFE64A19) / White
"TEAL"    → Color(0xFF00695C) / White
"DYNAMIC" → Material You (Android 12+)
```

### **UI Screens** (`ui/screen/`)

| Экран | Функции |
|-------|---------|
| `HomeScreen` | Включение, слайдеры, цвет, стиль; при CROSSHAIR — lineLength + gapSize; при CIRCLE — showCenterCross + centerCrossSize |
| `ConfigsScreen` | Пресеты (горизонт. лента), список конфигов, сохранение, мульти-выбор экспорта, импорт, удаление с подтверждением |
| `SettingsScreen` | DarkModePicker (4 кнопки), акцентный цвет (7 вариантов), язык, автозапуск, версия |

### **Service** (`service/CrosshairService.kt`)
- `foregroundServiceType = specialUse` (Android 14+)
- Подписывается на `getCurrentConfigFlow()` — реактивное обновление прицела
- Управление: `ACTION_TOGGLE_VISIBILITY`, `ACTION_STOP_SERVICE`

### **BootReceiver** (`receiver/BootReceiver.kt`)
- Слушает `BOOT_COMPLETED`
- Если `autoStart = true` — стартует `CrosshairService`

### **CrosshairView** (`ui/overlay/CrosshairView.kt`)
- Android `View` на Canvas (не Compose — работает в WindowManager)
- DOT: `drawCircle()`
- CROSSHAIR: 4 линии с gapSize + lineLength; опционально центральная точка
- CIRCLE: `drawCircle()` + если `showCenterCross` — крест размером `radius * centerCrossSize`

### **CrosshairPreview** (`ui/component/CrosshairPreview.kt`)
- Compose `Canvas` — предпросмотр в карточке HomeScreen
- Повторяет логику CrosshairView (DOT / CROSSHAIR / CIRCLE)

---

## 🎯 Key Implementation Details

### NoActionBar — устранение двойного заголовка
```xml
<!-- values/themes.xml -->
<style name="Base.Theme.MentalityScope" parent="Theme.Material3.DayNight.NoActionBar">
```
`CenterAlignedTopAppBar` в Compose рисует заголовок сам, нативный ActionBar отключён.

### AppCompatActivity — для работы локализации
```kotlin
class MainActivity : AppCompatActivity() {
    // AppCompatDelegate.setApplicationLocales() работает только с AppCompatActivity
}
```

### Мульти-выбор экспорта
```kotlin
// ConfigsScreen.kt
var pendingExportJson by remember { mutableStateOf<String?>(null) }
// Нажатие "Экспорт" → showExportDialog → ExportSelectDialog
// → onExport(selectedIds) → pendingExportJson = viewModel.getExportJson(ids)
// → exportLauncher.launch("filename.json")

// ViewModel
fun getExportJson(ids: Set<String> = emptySet()): String {
    val list = if (ids.isEmpty()) _customConfigs.value
               else _customConfigs.value.filter { it.id in ids }
    return repository.exportConfigsJson(list)
}
```

### AMOLED тема
```kotlin
private fun buildAmoledScheme(primary: Color, onPrimary: Color) = darkColorScheme(
    background    = Color.Black,
    surface       = Color(0xFF0D0D0D),  // карточки чуть светлее фона
    secondary     = Color(0xFF252525),  // кнопки неактивных стилей
    onSecondary   = Color(0xFFEEEEEE),  // текст на них — белый
    outline       = Color(0xFF404040),
    ...
)
```

---

## 📋 Lint-конфигурация (build.gradle.kts)

```kotlin
lint {
    disable += "MutableCollectionMutableState"  // NPE-баг в AGP lint-детекторе
    abortOnError = true
}
```
