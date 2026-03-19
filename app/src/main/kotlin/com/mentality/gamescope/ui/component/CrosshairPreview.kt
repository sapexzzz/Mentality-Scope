package com.mentality.gamescope.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.mentality.gamescope.data.model.CrosshairConfig
import com.mentality.gamescope.data.model.CrosshairStyle

/**
 * Компонент для предпросмотра прицела в UI
 * Использует Compose Canvas для отрисовки прицела
 */
@Composable
fun CrosshairPreview(
    config: CrosshairConfig,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CrosshairCanvas(config)
        }
    }
}

/**
 * Canvas компонент для отрисовки прицела
 */
@Composable
fun CrosshairCanvas(config: CrosshairConfig) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(120.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        val color = try {
            androidx.compose.ui.graphics.Color(
                android.graphics.Color.parseColor(config.color)
            )
        } catch (e: Exception) {
            Color.White
        }

        val alphaPaint = color.copy(alpha = config.alpha)

        val baseSizeInPx = 40f * density
        val scaledSize = baseSizeInPx * config.size

        when (config.style) {
            CrosshairStyle.DOT -> {
                val radius = (config.thickness * config.size * density) / 2
                drawCircle(
                    color = alphaPaint,
                    radius = radius,
                    center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                )
            }
            CrosshairStyle.CROSSHAIR -> {
                val armEnd = (scaledSize / 2) * config.lineLength
                val gap = scaledSize * config.gapSize

                // Вертикальные линии
                drawLine(
                    color = alphaPaint,
                    start = androidx.compose.ui.geometry.Offset(centerX, centerY - armEnd),
                    end = androidx.compose.ui.geometry.Offset(centerX, centerY - gap),
                    strokeWidth = config.thickness
                )
                drawLine(
                    color = alphaPaint,
                    start = androidx.compose.ui.geometry.Offset(centerX, centerY + gap),
                    end = androidx.compose.ui.geometry.Offset(centerX, centerY + armEnd),
                    strokeWidth = config.thickness
                )

                // Горизонтальные линии
                drawLine(
                    color = alphaPaint,
                    start = androidx.compose.ui.geometry.Offset(centerX - armEnd, centerY),
                    end = androidx.compose.ui.geometry.Offset(centerX - gap, centerY),
                    strokeWidth = config.thickness
                )
                drawLine(
                    color = alphaPaint,
                    start = androidx.compose.ui.geometry.Offset(centerX + gap, centerY),
                    end = androidx.compose.ui.geometry.Offset(centerX + armEnd, centerY),
                    strokeWidth = config.thickness
                )

                // Центральная точка
                drawCircle(
                    color = alphaPaint,
                    radius = config.thickness * 0.5f,
                    center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                )
            }
            CrosshairStyle.CIRCLE -> {
                val radius = scaledSize / 2
                drawCircle(
                    color = alphaPaint,
                    radius = radius,
                    center = androidx.compose.ui.geometry.Offset(centerX, centerY),
                    style = Stroke(width = config.thickness)
                )

                if (config.showCenterCross) {
                    val crossSize = radius * config.centerCrossSize
                    drawLine(
                        color = alphaPaint,
                        start = androidx.compose.ui.geometry.Offset(centerX - crossSize, centerY),
                        end = androidx.compose.ui.geometry.Offset(centerX + crossSize, centerY),
                        strokeWidth = config.thickness
                    )
                    drawLine(
                        color = alphaPaint,
                        start = androidx.compose.ui.geometry.Offset(centerX, centerY - crossSize),
                        end = androidx.compose.ui.geometry.Offset(centerX, centerY + crossSize),
                        strokeWidth = config.thickness
                    )
                }
            }
        }
    }
}
