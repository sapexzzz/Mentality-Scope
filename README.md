# 🎯 Mentality Scope — Crosshair Overlay for Mobile Shooters

> **Version:** 1.5.0 | **Date:** March 20, 2026 | **Min SDK:** 31 (Android 12+)

## 📱 Overview

**Mentality Scope** is a fully-featured Android crosshair overlay app for mobile shooters. It renders a customizable crosshair on top of any app via WindowManager Overlay, runs as a Foreground Service, supports AMOLED/Dark/Light/System themes, multilingual support (RU/EN/FR), and flexible config management.

**Package:** `com.mentality.gamescope`  
**Target SDK:** 35 (Android 15)  
**Min SDK:** 31 (Android 12)  
**versionCode:** 5  

---

## ✨ Features v1.5.0

### 🎨 Customizable Crosshair
- **3 styles:** Dot (DOT), Crosshair (CROSSHAIR), Circle (CIRCLE)
- **Parameters:** color (6 accents + Dynamic), size, thickness, opacity
- **CROSSHAIR:** sliders for "Line length" and "Center gap"
- **CIRCLE:** toggle "Center cross" + slider "Cross size"
- **5 built-in presets:** CS GO, Valorant, PUBG, Default, Classic

### 🎭 Themes
- **4 modes:** System / Light / Dark / AMOLED
- **6 accent colors:** Red, Blue, Green, Purple, Orange, Teal
- **Dynamic Color** (Material You, Android 12+)
- AMOLED — pure black background (#000000) for battery saving

### 💾 Configurations
- Save and load user configs
- **Multi-select export** — pick configs via checkboxes
- Import/export as JSON
- Delete confirmation dialog

### 🌐 Localization
- Full support for **Russian**, **English**, and **French**
- Switch language without restarting the app (AppCompatDelegate)

### 🚀 Service & Notifications
- Foreground Service with `foregroundServiceType = specialUse`
- Persistent Notification: hide/show crosshair, open app
- **Auto-start on device boot** (BootReceiver)

---

## 📦 Project Structure

```
com/mentality/gamescope/
├── ui/
│   ├── MainActivity.kt                    # AppCompatActivity (locale fix)
│   ├── screen/
│   │   ├── HomeScreen.kt                  # Main screen + style extra settings
│   │   ├── ConfigsScreen.kt               # Configs + multi-select export
│   │   └── SettingsScreen.kt              # Theme, language, auto-start, version
│   ├── component/
│   │   └── CrosshairPreview.kt            # Compose preview of crosshair
│   ├── overlay/
│   │   └── CrosshairView.kt               # Canvas View for overlay rendering
│   └── theme/
│       ├── Theme.kt                       # SYSTEM/LIGHT/DARK/AMOLED themes
│       ├── Color.kt                       # Color palette
│       └── Typography.kt                  # Typography
├── service/
│   └── CrosshairService.kt                # Foreground Service + WindowManager
├── data/
│   ├── model/
│   │   ├── CrosshairConfig.kt             # Model + showCenterCross/centerCrossSize
│   │   └── CrosshairStyle.kt              # Enum: DOT, CROSSHAIR, CIRCLE
│   └── repository/
│       └── CrosshairRepository.kt         # DataStore: configs + theme + language
├── viewmodel/
│   ├── CrosshairViewModel.kt              # MVVM StateFlow: all states
│   └── CrosshairViewModelFactory.kt
├── notification/
│   └── NotificationHelper.kt
├── permission/
│   └── PermissionManager.kt
├── receiver/
│   ├── NotificationActionReceiver.kt
│   └── BootReceiver.kt                    # Auto-start on boot
└── GameScopeApplication.kt
```

---

## 🗂️ Built-in Presets

| Name        | Style     | Color   | Size | Thickness | Alpha |
|-------------|-----------|---------|------|-----------|-------|
| **CS GO**   | Crosshair | #FF0000 | 1.0  | 2.0       | 0.9   |
| **Valorant**| Crosshair | #00FF00 | 0.8  | 2.0       | 0.85  |
| **PUBG**    | Dot       | #FFFF00 | 1.2  | 2.5       | 0.8   |
| **Default** | Crosshair | #FFFFFF | 1.0  | 2.0       | 0.9   |
| **Classic** | Circle    | #0000FF | 0.9  | 1.5       | 0.95  |

---

## 🛠️ Tech Stack

| Category        | Technology                        |
|-----------------|-----------------------------------|
| Language        | Kotlin 1.9.x                      |
| UI              | Jetpack Compose + Material3 1.1.2 |
| Architecture    | MVVM (ViewModel + StateFlow)      |
| Persistence     | AndroidX DataStore Preferences    |
| Serialization   | JSONObject (android.org.json)     |
| Async           | Coroutines + Flow                 |
| Locale          | AppCompatDelegate                 |
| Build           | Gradle 8 KTS                      |

---

## 📖 Documentation

- **[BUILD_INSTRUCTIONS_EN.md](BUILD_INSTRUCTIONS_EN.md)** — build steps, JAVA_HOME, common errors
- **[ARCHITECTURE_EN.md](ARCHITECTURE_EN.md)** — MVVM architecture, data flow, components

---

## 🎮 Usage

### Home Screen
1. **"Activate Crosshair"** toggle — start/stop the service
2. **Size / Thickness / Opacity** — sliders
3. **Color** — 6 color buttons
4. **Style** — Dot / Crosshair / Circle
5. For **Crosshair** — extra: "Line length" and "Gap"
6. For **Circle** — toggle "Center cross" and "Cross size"

### Configs Screen
1. **Built-in presets** — horizontal scroll row
2. **My configs** — save, load, delete
3. **Export** → dialog with config checkboxes → JSON file
4. **Import** — load JSON

### Settings Screen
1. **Theme mode** — SYSTEM / LIGHT / DARK / AMOLED
2. **App color** — 7 options (including Dynamic on Android 12+)
3. **Language** — System / Russian / English / Français
4. **Auto-start** — on device boot
5. **Source code** — GitHub button linking to the project repository

---

## ⚠️ Permissions

```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

On first launch, the app will request:
1. Permission to display over other apps
2. Notification permission (Android 13+)

---

## 📄 Archive

Previous documentation versions are stored in `old_data/v1.5.0/`.

---

