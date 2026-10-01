package com.kampplus.hava.feature.weather.presentation.detail.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.feature.weather.presentation.model.HourlyChartPointUiModel
import kotlin.math.roundToInt

@Composable
fun HourlyWeatherChart(points: List<HourlyChartPointUiModel>, modifier: Modifier = Modifier) {
    if (points.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val pointSpacingDp = 54.dp
    val totalWidthDp = pointSpacingDp * points.size.coerceAtLeast(1)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header with selected info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "24 Saatlik Sıcaklık & Yağış Grafiği",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                selectedIndex?.let { idx ->
                    val p = points.getOrNull(idx)
                    if (p != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${p.timeText}: ${p.temperatureC.roundToInt()}° | %${p.precipitationProbability ?: 0}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Scrollable interactive canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val cyanColor = Color(0xFF0284C7)
                val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                val onSurface = MaterialTheme.colorScheme.onSurface

                Column {
                    Canvas(
                        modifier = Modifier
                            .width(totalWidthDp)
                            .height(140.dp)
                            .pointerInput(points) {
                                detectTapGestures { offset ->
                                    val stepPx = size.width / points.size
                                    val clickedIdx = (offset.x / stepPx).toInt().coerceIn(0, points.size - 1)
                                    selectedIndex = if (selectedIndex == clickedIdx) null else clickedIdx
                                }
                            }
                    ) {
                        val w = size.width
                        val h = size.height
                        val stepX = w / points.size
                        val minTemp = (points.minOfOrNull { it.temperatureC } ?: 0.0) - 2.0
                        val maxTemp = (points.maxOfOrNull { it.temperatureC } ?: 25.0) + 2.0
                        val tempRange = (maxTemp - minTemp).coerceAtLeast(1.0)

                        val chartTopPadding = 20f
                        val chartBottomPadding = 30f
                        val usableHeight = h - chartTopPadding - chartBottomPadding

                        // Calculate points
                        val coords = points.mapIndexed { idx, pt ->
                            val x = idx * stepX + stepX / 2f
                            val normalized = ((pt.temperatureC - minTemp) / tempRange).toFloat()
                            val y = h - chartBottomPadding - (normalized * usableHeight)
                            Offset(x, y)
                        }

                        // Gradient fill under curve
                        val fillPath = Path().apply {
                            moveTo(coords.first().x, h - chartBottomPadding)
                            coords.forEach { lineTo(it.x, it.y) }
                            lineTo(coords.last().x, h - chartBottomPadding)
                            close()
                        }
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(primaryColor.copy(alpha = 0.28f), Color.Transparent),
                                startY = 0f,
                                endY = h - chartBottomPadding
                            )
                        )

                        // Temperature line
                        val strokePath = Path().apply {
                            coords.forEachIndexed { i, pt ->
                                if (i == 0) {
                                    moveTo(pt.x, pt.y)
                                } else {
                                    lineTo(pt.x, pt.y)
                                }
                            }
                        }
                        drawPath(
                            path = strokePath,
                            color = primaryColor,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Precipitation probability bars at bottom
                        points.forEachIndexed { idx, pt ->
                            val prob = pt.precipitationProbability ?: 0
                            if (prob > 0) {
                                val x = idx * stepX + stepX / 2f
                                val barWidth = 8.dp.toPx()
                                val barHeight = (prob / 100f) * 22.dp.toPx()
                                drawRoundRect(
                                    color = cyanColor.copy(alpha = 0.6f),
                                    topLeft = Offset(x - barWidth / 2f, h - barHeight),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
                                )
                            }
                        }

                        // Dots on line
                        coords.forEachIndexed { idx, pt ->
                            val isSelected = selectedIndex == idx
                            drawCircle(
                                color = if (isSelected) cyanColor else primaryColor,
                                radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx(),
                                center = pt
                            )
                            if (isSelected) {
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.5.dp.toPx(),
                                    center = pt
                                )
                            }
                        }
                    }

                    // Time labels under canvas
                    Row(modifier = Modifier.width(totalWidthDp)) {
                        points.forEachIndexed { idx, pt ->
                            Box(
                                modifier = Modifier
                                    .width(pointSpacingDp)
                                    .padding(top = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${pt.temperatureC.roundToInt()}°",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedIndex ==
                                            idx
                                        ) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                    Text(
                                        text = pt.emoji,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = pt.timeText,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
