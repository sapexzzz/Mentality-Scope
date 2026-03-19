package com.mentality.gamescope.ui.screen

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
import androidx.compose.ui.unit.dp
import com.mentality.gamescope.viewmodel.CrosshairViewModel

@Composable
fun SettingsScreen(viewModel: CrosshairViewModel) {
    val autoStart by viewModel.autoStart.collectAsState()
    val appTheme by viewModel.appTheme.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionTitle("Основные")
        }

        item {
            SettingsCard {
                SettingsRowSwitch(
                    title = "Автозапуск при старте",
                    subtitle = "Запускать сервис при включении телефона",
                    checked = autoStart,
                    onCheckedChange = { viewModel.setAutoStart(it) }
                )
            }
        }

        item { SectionDivider() }

        item { SectionTitle("Внешний вид") }

        item {
            SettingsCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Цвет приложения", style = MaterialTheme.typography.bodyMedium)
                    ThemePicker(currentTheme = appTheme, onThemeSelected = { viewModel.setAppTheme(it) })
                }
            }
        }

        item { SectionDivider() }

        item { SectionTitle("Язык") }

        item {
            SettingsCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Язык интерфейса", style = MaterialTheme.typography.bodyMedium)
                    LanguagePicker(currentLanguage = appLanguage, onLanguageSelected = { viewModel.setAppLanguage(it) })
                    Text(
                        text = "Перезапустите приложение для применения",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { SectionDivider() }

        item { SectionTitle("О приложении") }

        item {
            SettingsCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsInfoRow("Версия", "1.2.0")
                    SettingsInfoRow("Разработчик", "Mentality Team")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
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
    val langs = listOf("SYSTEM" to "Системный", "ru" to "Русский", "en" to "English")
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

