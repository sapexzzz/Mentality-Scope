package com.mentality.gamescope.ui.screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mentality.gamescope.R
import com.mentality.gamescope.viewmodel.CrosshairViewModel

@Composable
fun SettingsScreen(viewModel: CrosshairViewModel) {
    val autoStart by viewModel.autoStart.collectAsState()
    val appTheme by viewModel.appTheme.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val appDarkMode by viewModel.appDarkMode.collectAsState()
    val context = LocalContext.current
    var showRestartDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionTitle(stringResource(R.string.settings_basic_title))
        }

        item {
            SettingsCard {
                SettingsRowSwitch(
                    title = stringResource(R.string.settings_autostart_title),
                    subtitle = stringResource(R.string.settings_autostart_subtitle),
                    checked = autoStart,
                    onCheckedChange = { viewModel.setAutoStart(it) }
                )
            }
        }

        item { SectionDivider() }

        item { SectionTitle(stringResource(R.string.settings_appearance_title)) }

        item {
            SettingsCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.settings_theme_color), style = MaterialTheme.typography.bodyMedium)
                    ThemePicker(currentTheme = appTheme, onThemeSelected = { viewModel.setAppTheme(it) })
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(stringResource(R.string.settings_dark_mode_label), style = MaterialTheme.typography.bodyMedium)
                    DarkModePicker(currentMode = appDarkMode, onModeSelected = { viewModel.setAppDarkMode(it) })
                }
            }
        }

        item { SectionDivider() }

        item { SectionTitle(stringResource(R.string.settings_language_title)) }

        item {
            SettingsCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.settings_language_subtitle), style = MaterialTheme.typography.bodyMedium)
                    LanguagePicker(
                        currentLanguage = appLanguage,
                        onLanguageSelected = { lang ->
                            if (lang != appLanguage) {
                                viewModel.setAppLanguage(lang)
                                showRestartDialog = true
                            }
                        }
                    )
                    Text(
                        text = stringResource(R.string.settings_language_restart_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { SectionDivider() }

        item { SectionTitle(stringResource(R.string.settings_about_title)) }

        item {
            SettingsCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsInfoRow(stringResource(R.string.settings_version_label), stringResource(R.string.settings_version))
                    SettingsInfoRow(stringResource(R.string.settings_developer), stringResource(R.string.settings_developer_name))
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    val ctx = LocalContext.current
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.settings_github_label), style = MaterialTheme.typography.bodyMedium)
                        TextButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/sapexzzz/Mentality-Scope/tree/main"))
                                ctx.startActivity(intent)
                            }
                        ) {
                            Text(stringResource(R.string.settings_github_url), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }

    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            title = { Text(stringResource(R.string.settings_restart_dialog_title)) },
            text = { Text(stringResource(R.string.settings_restart_dialog_message)) },
            confirmButton = {
                Button(onClick = {
                    showRestartDialog = false
                    (context as? Activity)?.recreate()
                }) {
                    Text(stringResource(R.string.settings_restart_now))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRestartDialog = false }) {
                    Text(stringResource(R.string.settings_restart_later))
                }
            }
        )
    }
}

// ─── Helpers ────────────────────────────────────────────────────────────────

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun SectionDivider() {
    Divider(modifier = Modifier.padding(vertical = 4.dp))
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun SettingsRowSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ThemePicker(currentTheme: String, onThemeSelected: (String) -> Unit) {
    val themes = listOf(
        "RED"     to Color(0xFFB71C1C),
        "BLUE"    to Color(0xFF1565C0),
        "GREEN"   to Color(0xFF2E7D32),
        "PURPLE"  to Color(0xFF6A1B9A),
        "ORANGE"  to Color(0xFFE64A19),
        "TEAL"    to Color(0xFF00695C),
        "DYNAMIC" to null
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        themes.forEach { (key, color) ->
            val isSelected = currentTheme == key
            val borderColor = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent
            if (color != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(3.dp, borderColor, CircleShape)
                        .clickable { onThemeSelected(key) }
                )
            } else {
                // Dynamic — gradient placeholder
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                                listOf(Color(0xFF1565C0), Color(0xFF2E7D32), Color(0xFFB71C1C), Color(0xFF1565C0))
                            )
                        )
                        .border(3.dp, borderColor, CircleShape)
                        .clickable { onThemeSelected(key) }
                ) {
                    Text("A", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun LanguagePicker(currentLanguage: String, onLanguageSelected: (String) -> Unit) {
    val langs = listOf(
        "SYSTEM" to stringResource(R.string.language_system),
        "ru" to stringResource(R.string.language_russian),
        "en" to stringResource(R.string.language_english),
        "fr" to stringResource(R.string.language_french)
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        langs.forEach { (key, label) ->
            val selected = currentLanguage == key
            if (selected) {
                Button(
                    onClick = { onLanguageSelected(key) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(label, style = MaterialTheme.typography.labelMedium)
                }
            } else {
                OutlinedButton(
                    onClick = { onLanguageSelected(key) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(label, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun DarkModePicker(currentMode: String, onModeSelected: (String) -> Unit) {
    val modes = listOf(
        "SYSTEM" to stringResource(R.string.dark_mode_system),
        "LIGHT"  to stringResource(R.string.dark_mode_light),
        "DARK"   to stringResource(R.string.dark_mode_dark),
        "AMOLED" to stringResource(R.string.dark_mode_amoled)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        modes.forEach { (key, label) ->
            val selected = currentMode == key
            if (selected) {
                Button(
                    onClick = { onModeSelected(key) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).height(34.dp)
                ) {
                    Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }
            } else {
                OutlinedButton(
                    onClick = { onModeSelected(key) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).height(34.dp)
                ) {
                    Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }
            }
        }
    }
}

