# Mentality Scope — Architecture v1.5.0

## 🏗️ MVVM Architecture

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
└────────────────────────────┬────────────────────────────────────────┘
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
│  │  ├─ appTheme:       StateFlow<String>   // accent color    │    │
│  │  ├─ appDarkMode:    StateFlow<String>   // SYSTEM/LIGHT/   │    │
│  │  │                                      // DARK/AMOLED     │    │
│  │  ├─ appLanguage:    StateFlow<String>                      │    │
│  │  └─ setSize/setColor/setStyle/setDarkMode/...              │    │
│  └────────────────────────────────────────────────────────────┘    │
└────────────────────────────┬────────────────────────────────────────┘
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

### Changing Crosshair Settings
```
User (Slider) → viewModel.setSize(1.5f)
    → _currentConfig.update { it.copy(size = 1.5f) }
    → repository.saveCurrentConfig(config)   [suspend]
    → DataStore.edit { prefs[CURRENT_CONFIG_KEY] = json }
    → CrosshairService receives Flow update
    → crosshairView.setConfig(newConfig) → Canvas.invalidate()
```

### Changing Theme (v1.4.0)
```
User (DarkModePicker) → viewModel.setAppDarkMode("AMOLED")
    → repository.setAppDarkMode("AMOLED")
    → DataStore.edit { prefs[APP_DARK_MODE_KEY] = "AMOLED" }
    → MainActivity.appDarkMode collectAsState()
    → MentalityScopeTheme(darkMode = "AMOLED") → buildAmoledScheme()
    → UI fully recomposed
```

### Language Switch
```
User (SettingsScreen) → viewModel.setAppLanguage("en")
    → repository.setAppLanguage("en")
    → AppCompatDelegate.setApplicationLocales(LocaleListCompat)
    → Activity recreated, LocaleList applied
```

### Multi-select Export (v1.4.0)
```
User presses "Export" → showExportDialog = true
    → ExportSelectDialog (checkboxes per config)
    → User selects desired configs → onExport(selectedIds: Set<String>)
    → viewModel.getExportJson(ids) filters list
    → exportLauncher.launch("filename.json")
    → ContentResolver.openOutputStream → write JSON
```

---

## 📊 Components

### **Models** (`data/model/`)
| Class | Purpose |
|-------|---------|
| `CrosshairConfig` | Full crosshair configuration. Fields: id, name, color, size, thickness, alpha, style, lineLength, gapSize, **showCenterCross**, **centerCrossSize** |
| `CrosshairStyle` | Enum: DOT, CROSSHAIR, CIRCLE |

### **Repository** (`data/repository/`)
- Single point of access to DataStore
- Serialization via `JSONObject` (no third-party libraries)
- All operations are suspend functions or Flow

**DataStore Keys:**
```
CURRENT_CONFIG_KEY   — current crosshair configuration (JSON)
CUSTOM_CONFIGS_KEY   — list of user configs (JSON array)
AUTO_START_KEY       — auto-start enabled (Boolean)
APP_THEME_KEY        — accent color (String: RED/BLUE/GREEN/...)
APP_DARK_MODE_KEY    — theme mode (String: SYSTEM/LIGHT/DARK/AMOLED)
APP_LANGUAGE_KEY     — language (String: system/ru/en/fr)
```

### **Theme** (`ui/theme/Theme.kt`)

```kotlin
MentalityScopeTheme(appTheme = "RED", darkMode = "AMOLED") { ... }
```

| darkMode   | Description                                        |
|------------|----------------------------------------------------|
| `SYSTEM`   | Follows system theme                               |
| `LIGHT`    | Always light theme                                 |
| `DARK`     | Always dark theme                                  |
| `AMOLED`   | Pure black (#000000) background, surface = #0D0D0D |

Accent colors and their `onPrimary`:
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

| Screen | Features |
|--------|----------|
| `HomeScreen` | Enable/disable, sliders, color, style; for CROSSHAIR — lineLength + gapSize; for CIRCLE — showCenterCross + centerCrossSize |
| `ConfigsScreen` | Presets (horizontal row), config list, save, multi-select export, import, delete with confirmation |
| `SettingsScreen` | DarkModePicker (4 buttons), accent color (7 options), language, auto-start, version |

### **Service** (`service/CrosshairService.kt`)
- `foregroundServiceType = specialUse` (Android 14+)
- Subscribes to `getCurrentConfigFlow()` — reactive crosshair updates
- Controls: `ACTION_TOGGLE_VISIBILITY`, `ACTION_STOP_SERVICE`

### **BootReceiver** (`receiver/BootReceiver.kt`)
- Listens for `BOOT_COMPLETED`
- If `autoStart = true` — starts `CrosshairService`

### **CrosshairView** (`ui/overlay/CrosshairView.kt`)
- Android `View` on Canvas (not Compose — runs in WindowManager)
- DOT: `drawCircle()`
- CROSSHAIR: 4 lines with gapSize + lineLength; optionally center dot
- CIRCLE: `drawCircle()` + if `showCenterCross` — cross of size `radius * centerCrossSize`

### **CrosshairPreview** (`ui/component/CrosshairPreview.kt`)
- Compose `Canvas` — preview in HomeScreen card
- Mirrors CrosshairView logic (DOT / CROSSHAIR / CIRCLE)

---

## 🎯 Key Implementation Details

### NoActionBar — Fixing Double Title Bar
```xml
<!-- values/themes.xml -->
<style name="Base.Theme.MentalityScope" parent="Theme.Material3.DayNight.NoActionBar">
```
`CenterAlignedTopAppBar` in Compose draws the title itself; the native ActionBar is disabled.

### AppCompatActivity — Required for Locale Support
```kotlin
class MainActivity : AppCompatActivity() {
    // AppCompatDelegate.setApplicationLocales() only works with AppCompatActivity
}
```

### Multi-select Export
```kotlin
// ConfigsScreen.kt
var pendingExportJson by remember { mutableStateOf<String?>(null) }
// Press "Export" → showExportDialog → ExportSelectDialog
// → onExport(selectedIds) → pendingExportJson = viewModel.getExportJson(ids)
// → exportLauncher.launch("filename.json")

// ViewModel
fun getExportJson(ids: Set<String> = emptySet()): String {
    val list = if (ids.isEmpty()) _customConfigs.value
               else _customConfigs.value.filter { it.id in ids }
    return repository.exportConfigsJson(list)
}
```

### AMOLED Theme
```kotlin
private fun buildAmoledScheme(primary: Color, onPrimary: Color) = darkColorScheme(
    background    = Color.Black,
    surface       = Color(0xFF0D0D0D),  // cards slightly lighter than background
    secondary     = Color(0xFF252525),  // inactive style buttons
    onSecondary   = Color(0xFFEEEEEE),  // text on them — white
    outline       = Color(0xFF404040),
    ...
)
```

---

## 📋 Lint Configuration (build.gradle.kts)

```kotlin
lint {
    disable += "MutableCollectionMutableState"  // NPE bug in AGP lint detector
    abortOnError = true
}
```
