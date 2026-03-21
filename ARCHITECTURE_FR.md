# Mentality Scope — Architecture v1.5.0

## 🏗️ Architecture MVVM

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Couche UI (Compose)                            │
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
│                     Couche ViewModel                                │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │ CrosshairViewModel                                         │    │
│  │  ├─ currentConfig:  StateFlow<CrosshairConfig>             │    │
│  │  ├─ isServiceRunning: StateFlow<Boolean>                   │    │
│  │  ├─ customConfigs:  StateFlow<List<CrosshairConfig>>       │    │
│  │  ├─ presets:        StateFlow<List<CrosshairConfig>>       │    │
│  │  ├─ autoStart:      StateFlow<Boolean>                     │    │
│  │  ├─ appTheme:       StateFlow<String>   // couleur accent  │    │
│  │  ├─ appDarkMode:    StateFlow<String>   // SYSTEM/LIGHT/   │    │
│  │  │                                      // DARK/AMOLED     │    │
│  │  ├─ appLanguage:    StateFlow<String>                      │    │
│  │  └─ setSize/setColor/setStyle/setDarkMode/...              │    │
│  └────────────────────────────────────────────────────────────┘    │
└────────────────────────────┬────────────────────────────────────────┘
                             ↓
┌─────────────────────────────────────────────────────────────────────┐
│                 Couche Repository (DataStore)                       │
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

## 🔄 Flux de données

### Modification des paramètres du réticule
```
Utilisateur (Curseur) → viewModel.setSize(1.5f)
    → _currentConfig.update { it.copy(size = 1.5f) }
    → repository.saveCurrentConfig(config)   [suspend]
    → DataStore.edit { prefs[CURRENT_CONFIG_KEY] = json }
    → CrosshairService reçoit la mise à jour du Flow
    → crosshairView.setConfig(newConfig) → Canvas.invalidate()
```

### Changement de thème (v1.4.0)
```
Utilisateur (DarkModePicker) → viewModel.setAppDarkMode("AMOLED")
    → repository.setAppDarkMode("AMOLED")
    → DataStore.edit { prefs[APP_DARK_MODE_KEY] = "AMOLED" }
    → MainActivity.appDarkMode collectAsState()
    → MentalityScopeTheme(darkMode = "AMOLED") → buildAmoledScheme()
    → UI entièrement recomposée
```

### Changement de langue
```
Utilisateur (SettingsScreen) → viewModel.setAppLanguage("fr")
    → repository.setAppLanguage("fr")
    → AppCompatDelegate.setApplicationLocales(LocaleListCompat)
    → Activity recréée, LocaleList appliquée
```

### Export multi-sélection (v1.4.0)
```
Utilisateur appuie sur "Exporter" → showExportDialog = true
    → ExportSelectDialog (cases à cocher par config)
    → Utilisateur sélectionne → onExport(selectedIds: Set<String>)
    → viewModel.getExportJson(ids) filtre la liste
    → exportLauncher.launch("filename.json")
    → ContentResolver.openOutputStream → écriture JSON
```

---

## 📊 Composants

### **Modèles** (`data/model/`)
| Classe | Rôle |
|--------|------|
| `CrosshairConfig` | Configuration complète du réticule. Champs : id, name, color, size, thickness, alpha, style, lineLength, gapSize, **showCenterCross**, **centerCrossSize** |
| `CrosshairStyle` | Enum : DOT, CROSSHAIR, CIRCLE |

### **Repository** (`data/repository/`)
- Point d'accès unique au DataStore
- Sérialisation via `JSONObject` (sans bibliothèques tierces)
- Toutes les opérations sont des fonctions suspend ou des Flow

**Clés DataStore :**
```
CURRENT_CONFIG_KEY   — configuration actuelle du réticule (JSON)
CUSTOM_CONFIGS_KEY   — liste des configs utilisateur (tableau JSON)
AUTO_START_KEY       — démarrage automatique activé (Boolean)
APP_THEME_KEY        — couleur d'accent (String: RED/BLUE/GREEN/...)
APP_DARK_MODE_KEY    — mode thème (String: SYSTEM/LIGHT/DARK/AMOLED)
APP_LANGUAGE_KEY     — langue (String: system/ru/en/fr)
```

### **Thème** (`ui/theme/Theme.kt`)

```kotlin
MentalityScopeTheme(appTheme = "RED", darkMode = "AMOLED") { ... }
```

| darkMode   | Description                                           |
|------------|-------------------------------------------------------|
| `SYSTEM`   | Suit le thème système de l'appareil                   |
| `LIGHT`    | Toujours thème clair                                  |
| `DARK`     | Toujours thème sombre                                 |
| `AMOLED`   | Fond noir pur (#000000), surface = #0D0D0D            |

Couleurs d'accent et leur `onPrimary` :
```kotlin
"RED"     → Color(0xFFB71C1C) / White
"BLUE"    → Color(0xFF1565C0) / White
"GREEN"   → Color(0xFF2E7D32) / White
"PURPLE"  → Color(0xFF6A1B9A) / White
"ORANGE"  → Color(0xFFE64A19) / White
"TEAL"    → Color(0xFF00695C) / White
"DYNAMIC" → Material You (Android 12+)
```

### **Écrans UI** (`ui/screen/`)

| Écran | Fonctionnalités |
|-------|----------------|
| `HomeScreen` | Activation, curseurs, couleur, style ; pour CROSSHAIR — lineLength + gapSize ; pour CIRCLE — showCenterCross + centerCrossSize |
| `ConfigsScreen` | Préréglages (défilement horizontal), liste de configs, sauvegarde, export multi-sélection, import, suppression avec confirmation |
| `SettingsScreen` | DarkModePicker (4 boutons), couleur d'accent (7 options), langue, démarrage auto, version |

### **Service** (`service/CrosshairService.kt`)
- `foregroundServiceType = specialUse` (Android 14+)
- S'abonne à `getCurrentConfigFlow()` — mises à jour réactives du réticule
- Contrôles : `ACTION_TOGGLE_VISIBILITY`, `ACTION_STOP_SERVICE`

### **BootReceiver** (`receiver/BootReceiver.kt`)
- Écoute `BOOT_COMPLETED`
- Si `autoStart = true` — démarre `CrosshairService`

### **CrosshairView** (`ui/overlay/CrosshairView.kt`)
- Android `View` sur Canvas (pas Compose — fonctionne dans WindowManager)
- DOT : `drawCircle()`
- CROSSHAIR : 4 lignes avec gapSize + lineLength ; optionnellement point central
- CIRCLE : `drawCircle()` + si `showCenterCross` — croix de taille `radius * centerCrossSize`

### **CrosshairPreview** (`ui/component/CrosshairPreview.kt`)
- Compose `Canvas` — aperçu dans la carte HomeScreen
- Reproduit la logique de CrosshairView (DOT / CROSSHAIR / CIRCLE)

---

## 🎯 Détails d'implémentation clés

### NoActionBar — Correction de la double barre de titre
```xml
<!-- values/themes.xml -->
<style name="Base.Theme.MentalityScope" parent="Theme.Material3.DayNight.NoActionBar">
```
`CenterAlignedTopAppBar` dans Compose dessine lui-même le titre ; l'ActionBar natif est désactivé.

### AppCompatActivity — Requis pour la prise en charge des locales
```kotlin
class MainActivity : AppCompatActivity() {
    // AppCompatDelegate.setApplicationLocales() fonctionne uniquement avec AppCompatActivity
}
```

### Export multi-sélection
```kotlin
// ConfigsScreen.kt
var pendingExportJson by remember { mutableStateOf<String?>(null) }
// Appui "Exporter" → showExportDialog → ExportSelectDialog
// → onExport(selectedIds) → pendingExportJson = viewModel.getExportJson(ids)
// → exportLauncher.launch("filename.json")

// ViewModel
fun getExportJson(ids: Set<String> = emptySet()): String {
    val list = if (ids.isEmpty()) _customConfigs.value
               else _customConfigs.value.filter { it.id in ids }
    return repository.exportConfigsJson(list)
}
```

### Thème AMOLED
```kotlin
private fun buildAmoledScheme(primary: Color, onPrimary: Color) = darkColorScheme(
    background    = Color.Black,
    surface       = Color(0xFF0D0D0D),  // cartes légèrement plus claires que le fond
    secondary     = Color(0xFF252525),  // boutons de styles inactifs
    onSecondary   = Color(0xFFEEEEEE),  // texte sur eux — blanc
    outline       = Color(0xFF404040),
    ...
)
```

---

## 📋 Configuration lint (build.gradle.kts)

```kotlin
lint {
    disable += "MutableCollectionMutableState"  // bogue NPE dans le détecteur lint AGP
    abortOnError = true
}
```
