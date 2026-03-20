# Mentality Scope — Руководство по сборке v1.4.0

## 🚀 Быстрый старт

### Требования
- **Java:** OpenJDK 17+ (или JBR из Android Studio)
- **Android SDK:** 31 (Min) и 35 (Target)
- **Gradle:** 8.x (wrapper включён в проект)

### Сборка из командной строки

```bash
cd ~/scripts/android_application

# Debug APK
JAVA_HOME=/opt/android-studio/jbr ./gradlew assembleDebug

# Release APK (требует подписи keystore)
JAVA_HOME=/opt/android-studio/jbr ./gradlew assembleRelease

# Проверить lint (release)
JAVA_HOME=/opt/android-studio/jbr ./gradlew lintRelease
```

> **Важно:** `JAVA_HOME=/opt/android-studio/jbr` — обязателен, иначе Gradle
> может использовать несовместимую JVM из системы.

### Выходные файлы

```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release.apk
```

### Установка на устройство

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 📦 Структура проекта

```
android_application/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── local.properties                      # SDK path (генерируется IDE)
└── app/
    ├── build.gradle.kts                  # versionCode=4, versionName="1.4.0"
    └── src/main/
        ├── AndroidManifest.xml
        ├── kotlin/com/mentality/gamescope/
        │   ├── GameScopeApplication.kt
        │   ├── ui/
        │   │   ├── MainActivity.kt        # AppCompatActivity
        │   │   ├── screen/
        │   │   │   ├── HomeScreen.kt
        │   │   │   ├── ConfigsScreen.kt
        │   │   │   └── SettingsScreen.kt
        │   │   ├── component/CrosshairPreview.kt
        │   │   ├── overlay/CrosshairView.kt
        │   │   └── theme/
        │   │       ├── Theme.kt           # SYSTEM/LIGHT/DARK/AMOLED
        │   │       ├── Color.kt
        │   │       └── Typography.kt
        │   ├── service/CrosshairService.kt
        │   ├── data/
        │   │   ├── model/CrosshairConfig.kt
        │   │   ├── model/CrosshairStyle.kt
        │   │   └── repository/CrosshairRepository.kt
        │   ├── viewmodel/CrosshairViewModel.kt
        │   ├── viewmodel/CrosshairViewModelFactory.kt
        │   ├── notification/NotificationHelper.kt
        │   ├── permission/PermissionManager.kt
        │   └── receiver/
        │       ├── NotificationActionReceiver.kt
        │       └── BootReceiver.kt
        └── res/
            ├── values/
            │   ├── strings.xml            # RU
            │   ├── colors.xml
            │   └── themes.xml             # NoActionBar
            ├── values-en/
            │   └── strings.xml            # EN
            ├── values-night/
            │   └── colors.xml
            └── xml/
                ├── data_extraction_rules.xml
                └── backup_scheme.xml
```

---

## 🔧 Зависимости (app/build.gradle.kts)

| Зависимость                    | Версия  | Назначение                     |
|-------------------------------|---------|-------------------------------|
| `core-ktx`                    | 1.12.0  | Kotlin extensions              |
| `appcompat`                   | 1.6.1   | AppCompatActivity + LocaleList |
| `lifecycle-runtime-ktx`       | 2.6.2   | Lifecycle корутины             |
| `activity-compose`            | 1.8.0   | Compose интеграция             |
| `compose-bom`                 | 2023.10 | Compose BOM                   |
| `material3`                   | 1.1.2   | Material Design 3              |
| `datastore-preferences`       | 1.0.0   | Персистентность                |
| `kotlin-bom`                  | 1.9.21  | Kotlin версия                  |

---

## 🎨 Требуемые разрешения

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

## ⚠️ Lint-конфигурация

В `app/build.gradle.kts` отключён один lint-детектор:

```kotlin
lint {
    disable += "MutableCollectionMutableState"
    abortOnError = true
}
```

Причина: известный NPE-баг в `MutableCollectionMutableStateDetector` из AGP.
Это не влияет на качество кода — правило не применимо к нашему проекту.

---

## 🔴 Типичные проблемы

### `Supplied javaHome is not a valid folder`
```bash
# Решение: явно указать JAVA_HOME
JAVA_HOME=/opt/android-studio/jbr ./gradlew assembleDebug
```

### `Lint found fatal errors while assembling a release target`
- Проверить что в `app/build.gradle.kts` есть блок `lint { ... }`
- Запустить: `JAVA_HOME=/opt/android-studio/jbr ./gradlew assembleRelease`

### `MissingDefaultResource` в values-night/colors.xml
- В `values/colors.xml` должны быть fallback-объявления для всех цветов из `values-night`
- Текущий проект уже содержит их (`dark_primary`, `dark_primary_dark`, `dark_secondary`, `dark_tertiary`)

### Очистка кэша
```bash
JAVA_HOME=/opt/android-studio/jbr ./gradlew clean
JAVA_HOME=/opt/android-studio/jbr ./gradlew assembleDebug
```

---

## 📋 Изменение версии

**Файл:** `app/build.gradle.kts`

```kotlin
defaultConfig {
    versionCode = 5          // +1 для каждого релиза
    versionName = "1.5.0"
}
```

Не забудьте обновить строку в `values/strings.xml` и `values-en/strings.xml`:
```xml
<string name="settings_version">1.5.0</string>
```

---

## 📄 Архив

Предыдущие версии документации хранятся в `old_data/v1.3.0/docs/`.

---

**Mentality Team** · v1.4.0 · 19.03.2026
