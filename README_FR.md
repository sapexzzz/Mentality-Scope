# 🎯 Mentality Scope — Réticule Overlay pour Shooters Mobiles

> **Version :** 1.5.0 | **Date :** 20 mars 2026 | **Min SDK :** 31 (Android 12+)

## 📱 Description

**Mentality Scope** est une application Android complète de réticule en overlay pour les shooters mobiles. Elle affiche un réticule personnalisable par-dessus toute application via WindowManager Overlay, fonctionne comme un Service de premier plan (Foreground Service), supporte les thèmes AMOLED/Sombre/Clair/Système, la multilinguisme (RU/EN/FR) et une gestion flexible des configurations.

**Package :** `com.mentality.gamescope`  
**Target SDK :** 35 (Android 15)  
**Min SDK :** 31 (Android 12)  
**versionCode :** 5  

---

## ✨ Fonctionnalités v1.5.0

### 🎨 Réticule personnalisable
- **3 styles :** Point (DOT), Réticule (CROSSHAIR), Cercle (CIRCLE)
- **Paramètres :** couleur (6 accents + Dynamic), taille, épaisseur, opacité
- **CROSSHAIR :** curseurs « Longueur des lignes » et « Écart du centre »
- **CIRCLE :** bascule « Croix centrale » + curseur « Taille de la croix »
- **5 préréglages intégrés :** CS GO, Valorant, PUBG, Default, Classic

### 🎭 Thèmes
- **4 modes :** Système / Clair / Sombre / AMOLED
- **6 couleurs d'accent :** Rouge, Bleu, Vert, Violet, Orange, Sarcelle
- **Couleur dynamique** (Material You, Android 12+)
- AMOLED — fond noir pur (#000000) pour économiser la batterie

### 💾 Configurations
- Sauvegarder et charger des configs personnalisées
- **Export multi-sélection** — choisissez les configs via des cases à cocher
- Import/export en JSON
- Confirmation avant suppression

### 🌐 Localisation
- Support complet du **Russe**, de l'**Anglais** et du **Français**
- Changement de langue sans redémarrer l'application (AppCompatDelegate)

### 🚀 Service & Notifications
- Service de premier plan avec `foregroundServiceType = specialUse`
- Notification persistante : masquer/afficher le réticule, ouvrir l'app
- **Démarrage automatique au démarrage de l'appareil** (BootReceiver)

---

## 📦 Structure du projet

```
com/mentality/gamescope/
├── ui/
│   ├── MainActivity.kt                    # AppCompatActivity (correctif locale)
│   ├── screen/
│   │   ├── HomeScreen.kt                  # Écran principal + paramètres de style
│   │   ├── ConfigsScreen.kt               # Configs + export multi-sélection
│   │   └── SettingsScreen.kt              # Thème, langue, démarrage automatique
│   ├── component/
│   │   └── CrosshairPreview.kt            # Aperçu Compose du réticule
│   ├── overlay/
│   │   └── CrosshairView.kt               # Vue Canvas pour le rendu overlay
│   └── theme/
│       ├── Theme.kt                       # Thèmes SYSTEM/LIGHT/DARK/AMOLED
│       ├── Color.kt                       # Palette de couleurs
│       └── Typography.kt                  # Typographie
├── service/
│   └── CrosshairService.kt                # Foreground Service + WindowManager
├── data/
│   ├── model/
│   │   ├── CrosshairConfig.kt             # Modèle + showCenterCross/centerCrossSize
│   │   └── CrosshairStyle.kt              # Enum : DOT, CROSSHAIR, CIRCLE
│   └── repository/
│       └── CrosshairRepository.kt         # DataStore : configs + thème + langue
├── viewmodel/
│   ├── CrosshairViewModel.kt              # MVVM StateFlow : tous les états
│   └── CrosshairViewModelFactory.kt
├── notification/
│   └── NotificationHelper.kt
├── permission/
│   └── PermissionManager.kt
├── receiver/
│   ├── NotificationActionReceiver.kt
│   └── BootReceiver.kt                    # Démarrage automatique au boot
└── GameScopeApplication.kt
```

---

## 🗂️ Préréglages intégrés

| Nom         | Style     | Couleur | Taille | Épaisseur | Alpha |
|-------------|-----------|---------|--------|-----------|-------|
| **CS GO**   | Réticule  | #FF0000 | 1.0    | 2.0       | 0.9   |
| **Valorant**| Réticule  | #00FF00 | 0.8    | 2.0       | 0.85  |
| **PUBG**    | Point     | #FFFF00 | 1.2    | 2.5       | 0.8   |
| **Default** | Réticule  | #FFFFFF | 1.0    | 2.0       | 0.9   |
| **Classic** | Cercle    | #0000FF | 0.9    | 1.5       | 0.95  |

---

## 🛠️ Stack technique

| Catégorie       | Technologie                       |
|-----------------|-----------------------------------|
| Langage         | Kotlin 1.9.x                      |
| UI              | Jetpack Compose + Material3 1.1.2 |
| Architecture    | MVVM (ViewModel + StateFlow)      |
| Persistance     | AndroidX DataStore Preferences    |
| Sérialisation   | JSONObject (android.org.json)     |
| Asynchrone      | Coroutines + Flow                 |
| Locale          | AppCompatDelegate                 |
| Build           | Gradle 8 KTS                      |

---

## 📖 Documentation

- **[BUILD_INSTRUCTIONS_FR.md](BUILD_INSTRUCTIONS_FR.md)** — compilation, JAVA_HOME, erreurs courantes
- **[ARCHITECTURE_FR.md](ARCHITECTURE_FR.md)** — architecture MVVM, flux de données, composants

---

## 🎮 Utilisation

### Écran principal
1. Bascule **« Activer le réticule »** — démarrer/arrêter le service
2. **Taille / Épaisseur / Opacité** — curseurs
3. **Couleur** — 6 boutons de couleur
4. **Style** — Point / Réticule / Cercle
5. Pour **Réticule** — en plus : « Longueur des lignes » et « Écart »
6. Pour **Cercle** — bascule « Croix centrale » et « Taille de la croix »

### Écran des configurations
1. **Préréglages intégrés** — défilement horizontal
2. **Mes configs** — sauvegarder, charger, supprimer
3. **Exporter** → dialogue avec cases à cocher → fichier JSON
4. **Importer** — charger un JSON

### Écran des paramètres
1. **Mode thème** — SYSTEM / LIGHT / DARK / AMOLED
2. **Couleur de l'app** — 7 options (dont Dynamic sur Android 12+)
3. **Langue** — Système / Руский / English / Français
4. **Démarrage auto** — au démarrage de l'appareil
5. **Code source** — bouton GitHub vers le dépôt du projet

---

## ⚠️ Permissions

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

## 📄 Archive

Les versions précédentes de la documentation sont stockées dans `old_data/v1.4.0/`.

---
