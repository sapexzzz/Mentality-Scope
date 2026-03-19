package com.mentality.gamescope.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mentality.gamescope.viewmodel.CrosshairViewModel

/**
 * Экран системных настроек приложения
 */
@Composable
fun SettingsScreen(viewModel: CrosshairViewModel) {
    val autoStart by viewModel.autoStart.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Основные",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            // Автозапуск сервиса
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Автозапуск при старте",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Запускать сервис при включении телефона",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = autoStart,
                        onCheckedChange = { viewModel.setAutoStart(it) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Text(
                text = "О приложении",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            // Версия приложения
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Версия", style = MaterialTheme.typography.bodyMedium)
                        Text("1.0.0", style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Разработчик", style = MaterialTheme.typography.bodyMedium)
                        Text("Mentality Team", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Text(
                text = "Данные",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            // Кнопка очистки всех данных
            Button(
                onClick = { showClearDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Очистить все настройки", color = MaterialTheme.colorScheme.onError)
            }
        }

        item {
            Text(
                text = "Это удалит все сохраненные конфиги и вернет приложение в исходное состояние",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showClearDialog) {
        ClearSettingsDialog(
            onConfirm = {
                viewModel.clearAllSettings()
                showClearDialog = false
            },
            onDismiss = { showClearDialog = false }
        )
    }
}

@Composable
private fun ClearSettingsDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Очистить все настройки?") },
        text = {
            Column {
                Text("Это удалит:")
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Все сохраненные конфиги")
                Text("• Все пользовательские пресеты")
                Text("• Параметры приложения")
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Это действие не может быть отменено.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Очистить", color = MaterialTheme.colorScheme.onError)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
