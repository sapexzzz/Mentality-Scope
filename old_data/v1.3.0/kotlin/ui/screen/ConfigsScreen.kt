package com.mentality.gamescope.ui.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mentality.gamescope.R
import com.mentality.gamescope.data.model.CrosshairConfig
import com.mentality.gamescope.ui.component.CrosshairPreview
import com.mentality.gamescope.viewmodel.CrosshairViewModel
import kotlinx.coroutines.launch

/**
 * Экран управления конфигурациями прицела
 * Встроенные пресеты и пользовательские сохраненные конфиги
 */
@Composable
fun ConfigsScreen(viewModel: CrosshairViewModel, onNavigateToHome: () -> Unit) {
    val presets by viewModel.presets.collectAsState()
    val customConfigs by viewModel.customConfigs.collectAsState()
    var showSaveDialog by remember { mutableStateOf(false) }
    var configToDelete by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val exportSuccessMsg = stringResource(R.string.configs_export_success)
    val importSuccessMsg = stringResource(R.string.configs_import_success)
    val importErrorMsg = stringResource(R.string.configs_import_error)
    val exportFilename = stringResource(R.string.configs_export_filename)

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val json = viewModel.getExportJson()
                    context.contentResolver.openOutputStream(uri)?.use { stream ->
                        stream.write(json.toByteArray(Charsets.UTF_8))
                    }
                    Toast.makeText(context, exportSuccessMsg, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, importErrorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val json = context.contentResolver.openInputStream(uri)?.use { stream ->
                        stream.readBytes().toString(Charsets.UTF_8)
                    } ?: return@launch
                    val count = viewModel.importFromJson(json)
                    Toast.makeText(context, String.format(importSuccessMsg, count), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, importErrorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.configs_presets_title),
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            // Горизонтальный список пресетов
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                presets.forEach { preset ->
                    PresetCard(
                        config = preset,
                        onClick = {
                            viewModel.loadPreset(preset.id)
                            onNavigateToHome()
                        }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.configs_custom_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(stringResource(R.string.configs_import_button), style = MaterialTheme.typography.labelMedium)
                    }
                    OutlinedButton(
                        onClick = { exportLauncher.launch("${exportFilename}.json") },
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(stringResource(R.string.configs_export_button), style = MaterialTheme.typography.labelMedium)
                    }
                    Button(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(stringResource(R.string.configs_save_button), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        items(customConfigs) { config ->
            CustomConfigCard(
                config = config,
                onLoad = {
                    viewModel.loadCustomConfig(config.id)
                    onNavigateToHome()
                },
                onDelete = { configToDelete = config.id }
            )
        }

        if (customConfigs.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.configs_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showSaveDialog) {
        SaveConfigDialog(
            onSave = { name ->
                viewModel.saveCustomConfig(name)
                showSaveDialog = false
            },
            onDismiss = { showSaveDialog = false }
        )
    }

    configToDelete?.let { id ->
        AlertDialog(
            onDismissRequest = { configToDelete = null },
            title = { Text(stringResource(R.string.configs_delete_title)) },
            text = { Text(stringResource(R.string.configs_delete_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCustomConfig(id)
                        configToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                OutlinedButton(onClick = { configToDelete = null }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@Composable
private fun PresetCard(
    config: CrosshairConfig,
    onClick: () -> Unit
) {
    val presetColor = try {
        Color(android.graphics.Color.parseColor(config.color))
    } catch (e: Exception) {
        Color.White
    }

    Card(
        modifier = Modifier
            .width(110.dp)
            .height(110.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(presetColor, CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
            Text(
                text = config.name,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = config.style.getDisplayName(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CustomConfigCard(
    config: CrosshairConfig,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 50.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = config.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${config.style.getDisplayName()} • ${config.color}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Button(
                    onClick = onLoad,
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(stringResource(R.string.configs_load_button))
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete config"
                    )
                }
            }
        }
    }
}

@Composable
private fun SaveConfigDialog(
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var configName by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.configs_save_dialog_title)) },
        text = {
            Column {
                TextField(
                    value = configName,
                    onValueChange = { 
                        configName = it 
                        isError = false
                    },
                    label = { Text(stringResource(R.string.configs_save_dialog_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = isError
                )
                if (isError) {
                    Text(
                        text = stringResource(R.string.configs_save_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (configName.isNotBlank()) {
                        onSave(configName)
                    } else {
                        isError = true
                    }
                }
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
