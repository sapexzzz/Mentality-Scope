# Mentality Scope — Guide de compilation v1.5.0

## 🚀 Démarrage rapide

### Prérequis
- **Java :** OpenJDK 17+ (ou JBR d'Android Studio)
- **Android SDK :** 31 (Min) et 35 (Target)
- **Gradle :** 8.x (wrapper inclus dans le projet)

### Compilation en ligne de commande

```bash
cd ~/scripts/android_application

# APK Debug
JAVA_HOME=/opt/android-studio/jbr ./gradlew assembleDebug

# APK Release (nécessite une signature keystore)
JAVA_HOME=/opt/android-studio/jbr ./gradlew assembleRelease

# Vérification lint (release)
JAVA_HOME=/opt/android-studio/jbr ./gradlew lintRelease
```

> **Important :** `JAVA_HOME=/opt/android-studio/jbr` est obligatoire, sinon Gradle
> peut utiliser une JVM incompatible du système.

### Fichiers de sortie

```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release.apk
```

### Installation sur appareil

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 📦 Structure du projet

```
android_application/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── local.properties                      # Chemin SDK (généré par l'IDE)
└── app/
    ├── build.gradle.kts                  # versionCode=5, versionName="1.5.0"
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
            │   ├── strings.xml            # RU (base)
            │   ├── colors.xml
            │   └── themes.xml             # NoActionBar
            ├── values-en/
            │   └── strings.xml            # EN
            ├── values-fr/
            │   └── strings.xml            # FR
            ├── values-night/
            │   └── colors.xml
            └── xml/
                ├── data_extraction_rules.xml
                └── backup_scheme.xml
```

---

## 🔧 Dépendances (app/build.gradle.kts)

| Dépendance                     | Version  | Utilisation                    |
|-------------------------------|----------|-------------------------------|
| `core-ktx`                    | 1.12.0   | Extensions Kotlin              |
| `appcompat`                   | 1.6.1    | AppCompatActivity + LocaleList |
| `lifecycle-runtime-ktx`       | 2.6.2    | Coroutines lifecycle           |
| `activity-compose`            | 1.8.0    | Intégration Compose            |
| `compose-bom`                 | 2023.10  | Compose BOM                   |
| `material3`                   | 1.1.2    | Material Design 3              |
| `datastore-preferences`       | 1.0.0    | Persistance                    |
| `kotlin-bom`                  | 1.9.21   | Version Kotlin                 |

---

## 🎨 Permissions requises

```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

Au premier lancement, l'application demandera :
1. Permission d'affichage par-dessus d'autres applications
2. Permission de notifications (Android 13+)

---

## ⚠️ Configuration lint

Dans `app/build.gradle.kts`, une règle lint est désactivée :

```kotlin
lint {
    disable += "MutableCollectionMutableState"
    abortOnError = true
}
```

Raison : bogue NPE connu dans `MutableCollectionMutableStateDetector` de l'AGP.
Cela n'affecte pas la qualité du code — la règle n'est pas applicable à ce projet.

---

## 🔴 Problèmes courants

### `Supplied javaHome is not a valid folder`
```bash
# Solution : spécifier JAVA_HOME explicitement
JAVA_HOME=/opt/android-studio/jbr ./gradlew assembleDebug
```

### `Lint found fatal errors while assembling a release target`
- Vérifier que `app/build.gradle.kts` contient un bloc `lint { ... }`
- Lancer : `JAVA_HOME=/opt/android-studio/jbr ./gradlew assembleRelease`

### `MissingDefaultResource` dans values-night/colors.xml
- `values/colors.xml` doit contenir les déclarations de fallback pour toutes les couleurs de `values-night`
- Le projet actuel les contient déjà (`dark_primary`, `dark_primary_dark`, `dark_secondary`, `dark_tertiary`)

### Nettoyer le cache
```bash
JAVA_HOME=/opt/android-studio/jbr ./gradlew clean
JAVA_HOME=/opt/android-studio/jbr ./gradlew assembleDebug
```

---

## 📋 Changement de version

**Fichier :** `app/build.gradle.kts`

```kotlin
defaultConfig {
    versionCode = 5          // +1 pour chaque release
    versionName = "1.5.0"
}
```

N'oubliez pas de mettre à jour la chaîne de version dans `values/strings.xml`, `values-en/strings.xml` et `values-fr/strings.xml` :
```xml
<string name="settings_version">1.5.0</string>
```

---

## 📄 Archive

Les versions précédentes de la documentation sont stockées dans `old_data/v1.4.0/`.

---

**Mentality Team** · v1.5.0 · 20.03.2026
