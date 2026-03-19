package com.mentality.gamescope.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mentality.gamescope.R
import com.mentality.gamescope.data.model.CrosshairStyle
import com.mentality.gamescope.ui.component.CrosshairPreview
import com.mentality.gamescope.viewmodel.CrosshairViewModel

/**
 * Главный экран приложения
 * Управление включением/выключением сервиса и настройка прицела
 */
@Composable
fun HomeScreen(viewModel: CrosshairViewModel) {
    val currentConfig by viewModel.currentConfig.collectAsState()
    val isServiceRunning by viewModel.isServiceRunning.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Карточка предпросмотра прицела
            CrosshairPreview(
                config = currentConfig,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        item {
            // Переключатель включения сервиса
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_activate_crosshair),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isServiceRunning,
                        onCheckedChange = { viewModel.toggleService() }
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.home_settings_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Слайдер размера
        item {
            SizeSlider(
                value = currentConfig.size,
                onValueChange = { viewModel.setSize(it) }
            )
        }

        // Слайдер толщины
        item {
            ThicknessSlider(
                value = currentConfig.thickness,
                onValueChange = { viewModel.setThickness(it) }
            )
        }

        // Слайдер прозрачности
        item {
            AlphaSlider(
                value = currentConfig.alpha,
                onValueChange = { viewModel.setAlpha(it) }
            )
        }

        // Выбор цвета
        item {
            ColorSelector(
                currentColor = currentConfig.color,
                onColorSelected = { viewModel.setColor(it) }
            )
        }

        // Выбор стиля
        item {
            StyleSelector(
                currentStyle = currentConfig.style,
                onStyleSelected = { viewModel.setStyle(it) }
            )
        }

        // Геометрия крестика (только для CROSSHAIR)
        if (currentConfig.style == CrosshairStyle.CROSSHAIR) {
            item {
                LineLengthSlider(
                    value = currentConfig.lineLength,
                    onValueChange = { viewModel.setLineLength(it) }
                )
            }
            item {
                GapSizeSlider(
                    value = currentConfig.gapSize,
                    onValueChange = { viewModel.setGapSize(it) }
                )
            }
        }

        // Настройки круга (только для CIRCLE)
        if (currentConfig.style == CrosshairStyle.CIRCLE) {
            item {
                CircleCenterCrossToggle(
                    checked = currentConfig.showCenterCross,
                    onCheckedChange = { viewModel.setShowCenterCross(it) }
                )
            }
            if (currentConfig.showCenterCross) {
                item {
                    CenterCrossSizeSlider(
                        value = currentConfig.centerCrossSize,
                        onValueChange = { viewModel.setCenterCrossSize(it) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SizeSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.home_size_label), style = MaterialTheme.typography.labelMedium)
                Text(String.format("%.1f", value), style = MaterialTheme.typography.labelMedium)
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0.5f..3.0f,
                steps = 24,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ThicknessSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.home_thickness_label), style = MaterialTheme.typography.labelMedium)
                Text(String.format("%.1f", value), style = MaterialTheme.typography.labelMedium)
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 1.0f..4.0f,
                steps = 5,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AlphaSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.home_alpha_label), style = MaterialTheme.typography.labelMedium)
                Text(String.format("%.0f%%", value * 100), style = MaterialTheme.typography.labelMedium)
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0.3f..1.0f,
                steps = 13,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ColorSelector(
    currentColor: String,
    onColorSelected: (String) -> Unit
) {
    val colors = listOf(
        "#FFFFFF" to stringResource(R.string.home_color_white),
        "#FF0000" to stringResource(R.string.home_color_red),
        "#00FF00" to stringResource(R.string.home_color_green),
        "#0000FF" to stringResource(R.string.home_color_blue),
        "#FFFF00" to stringResource(R.string.home_color_yellow),
        "#FF00FF" to stringResource(R.string.home_color_purple)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.home_color_label), style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                colors.forEach { (hexColor, _) ->
                    ColorButton(
                        hex = hexColor,
                        isSelected = currentColor == hexColor,
                        onClick = { onColorSelected(hexColor) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorButton(
    hex: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.White
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(color, RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
    )
}

@Composable
private fun LineLengthSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.home_line_length_label), style = MaterialTheme.typography.labelMedium)
                Text(String.format("%.2f", value), style = MaterialTheme.typography.labelMedium)
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0.2f..2.0f,
                steps = 17,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GapSizeSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.home_gap_size_label), style = MaterialTheme.typography.labelMedium)
                Text(String.format("%.2f", value), style = MaterialTheme.typography.labelMedium)
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0.0f..0.5f,
                steps = 9,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StyleSelector(
    currentStyle: CrosshairStyle,
    onStyleSelected: (CrosshairStyle) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.home_style_label), style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CrosshairStyle.values().forEach { style ->
                    val isActive = currentStyle == style
                    Button(
                        onClick = { onStyleSelected(style) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isActive) MaterialTheme.colorScheme.primary
                                             else MaterialTheme.colorScheme.secondary,
                            contentColor = if (isActive) MaterialTheme.colorScheme.onPrimary
                                           else MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(style.getDisplayName(), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun CircleCenterCrossToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
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
            Text(stringResource(R.string.home_circle_center_cross), style = MaterialTheme.typography.labelMedium)
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun CenterCrossSizeSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.home_circle_cross_size), style = MaterialTheme.typography.labelMedium)
                Text(String.format("%.2f", value), style = MaterialTheme.typography.labelMedium)
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0.0f..0.6f,
                steps = 11,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
