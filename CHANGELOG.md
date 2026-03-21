# Changelog — Mentality Scope

All notable changes to this project are documented here.

---

## [1.7.0] — 2026-03-21

### Added
- **Ignore camera cutout** setting in Settings → Basic section
  - When enabled (default): crosshair is centered on the full physical screen, ignoring the front-camera notch area in landscape mode
  - When disabled: system-default behavior (safe zone only)
  - Change takes effect immediately without restarting the service (live `updateViewLayout`)
- Cutout setting persisted via DataStore (`ignore_cutout` key)
- Localization for new setting in RU / EN / FR

### Fixed
- Critical bug: in landscape mode (especially with punch-hole / notch camera cutouts) the crosshair was offset from screen center — caused by the OS restricting overlay window to the "safe zone". Fixed by setting `LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS` on the WindowManager overlay.

---

## [1.5.0] — 2026-03-20

### Added
- **French language (Français)** — complete `values-fr/strings.xml` with all keys
- **GitHub button** in Settings → About section — opens the project repository
- Language picker now shows 4 options: System / Русский / English / Français
- `string` keys: `settings_github_label`, `settings_github_url`, `language_french`

### Changed
- Multi-language documentation split into separate files per locale:
  - `README.md` (EN), `README_RU.md`, `README_FR.md`
  - `BUILD_INSTRUCTIONS_EN.md`, `BUILD_INSTRUCTIONS_RU.md`, `BUILD_INSTRUCTIONS_FR.md`
  - `ARCHITECTURE_EN.md`, `ARCHITECTURE_RU.md`, `ARCHITECTURE_FR.md`

### Fixed
- `MainActivity.kt` locale `when` block was missing `"fr"` branch — French selection fell through to system locale

### Infrastructure
- Version bumped: `versionCode = 5`, `versionName = "1.5.0"`
- Backup of v1.4.0 saved to `old_data/v1.4.0/`

---

## [1.4.0] — 2026-03-19

### Added
- **AMOLED theme** — pure black background (`#000000`), `surface = #0D0D0D`
- **Light / Dark / AMOLED / System** theme mode picker (4 buttons)
- **Circle crosshair** extra settings:
  - Toggle "Show center cross" (`showCenterCross`)
  - Slider "Center cross size" (`centerCrossSize`)
- **Multi-select export** for configs — checkbox dialog before exporting to JSON
- `getExportJson(ids: Set<String>)` in ViewModel to filter configs by selection

### Fixed
- Double title bar — set `Theme.Material3.DayNight.NoActionBar` in `values/themes.xml`
- Font color issues — explicit `onPrimary`/`onSecondary` per accent in `Theme.kt`
- `MissingDefaultResource` lint error — added fallback colors in `values/colors.xml`
- Lint rule `MutableCollectionMutableState` disabled in `build.gradle.kts` (AGP NPE bug)
- `ComponentActivity` → `AppCompatActivity` to enable `AppCompatDelegate` locale switching

### Changed
- `CrosshairConfig` extended with `showCenterCross: Boolean` and `centerCrossSize: Float`
- DataStore key `APP_DARK_MODE_KEY` added alongside existing `APP_THEME_KEY`

### Infrastructure
- Version bumped: `versionCode = 4`, `versionName = "1.4.0"`
- Backup of v1.3.0 saved to `old_data/v1.3.0/`

---

## [1.3.0] — 2026-03-18

### Added
- **English localization** (`values-en/strings.xml`) — full translation
- Language picker in Settings (System / Русский / English)
- `AppCompatDelegate.setApplicationLocales()` in `MainActivity`
- `APP_LANGUAGE_KEY` in DataStore

---

## [1.2.0] — 2026-03-17

### Added
- **6 accent colors**: Red, Blue, Green, Purple, Orange, Teal
- **Dynamic Color** (Material You, Android 12+)
- Theme color picker in Settings (7 buttons)
- `APP_THEME_KEY` in DataStore

---

## [1.1.0] — 2026-03-16

### Added
- **Import / Export configs** as JSON via `ContentResolver`
- **Delete confirmation** dialog for custom configs
- **Built-in presets**: CS GO, Valorant, PUBG, Default, Classic
- Horizontal preset scroll row in Configs screen

---

## [1.0.0] — 2026-03-15

### Initial Release
- Crosshair overlay via `WindowManager` (`TYPE_APPLICATION_OVERLAY`)
- **3 crosshair styles**: Dot (DOT), Crosshair (CROSSHAIR), Circle (CIRCLE)
- Sliders: Size, Thickness, Opacity
- CROSSHAIR extras: Line length, Center gap
- Save / load / delete custom configs (DataStore JSON)
- Foreground Service with persistent notification (toggle visibility, open app)
- BootReceiver — auto-start on device boot
- MVVM architecture: ViewModel + Repository + StateFlow
- Min SDK 31 (Android 12), Target SDK 35 (Android 15)
