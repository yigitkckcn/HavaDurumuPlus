package com.kampplus.hava.feature.weather.presentation.detail.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SunTrackerCard(sunriseTime: String?, sunsetTime: String?, uvIndex: Double?, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Title (Hafif yukarı alınmış başlık)
            Row(
                modifier = Modifier.offset(y = (-4).dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.WbSunny,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.sun_tracker_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Orijinal Sun Arc Grafiği
            val sunProgress = calculateSunProgress(sunriseTime, sunsetTime)
            SunArcGraphic(sunProgress = sunProgress)

            // Sunrise & Sunset Times
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-6).dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.WbTwilight,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.sunrise),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = sunriseTime ?: "06:30",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.WbTwilight,
                        contentDescription = null,
                        tint = Color(0xFFEA580C),
                        modifier = Modifier.size(18.dp)
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.sunset),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = sunsetTime ?: "19:45",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // UV Index Section
            val uv = uvIndex ?: 0.0
            val (uvLabel, uvColor) = uvRisk(uv)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.uv_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = uvColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${"%.1f".format(uv)} • $uvLabel",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = uvColor
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { (uv / 12.0).coerceIn(0.0, 1.0).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = uvColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SunArcGraphic(sunProgress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        val pathColor = Color(0xFFE2E8F0)

        Canvas(modifier = Modifier.fillMaxWidth().height(80.dp)) {
            val w = size.width
            val h = size.height
            val radius = (w / 2f) - 24.dp.toPx()
            val centerX = w / 2f
            val centerY = h

            // Draw dashed half-circle arc
            drawArc(
                color = pathColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                    cap = StrokeCap.Round
                )
            )

            // Draw active sun position on arc (180deg = sunrise, 0deg/360deg = sunset)
            val angleDeg = 180f + (180f * sunProgress.coerceIn(0f, 1f))
            val angleRad = Math.toRadians(angleDeg.toDouble())
            val sunX = (centerX + radius * cos(angleRad)).toFloat()
            val sunY = (centerY + radius * sin(angleRad)).toFloat()

            // Glow circle
            drawCircle(
                color = Color(0x40F59E0B),
                radius = 12.dp.toPx(),
                center = Offset(sunX, sunY)
            )
            // Sun center
            drawCircle(
                color = Color(0xFFF59E0B),
                radius = 6.dp.toPx(),
                center = Offset(sunX, sunY)
            )
            drawCircle(
                color = Color(0xFFFEF08A),
                radius = 3.dp.toPx(),
                center = Offset(sunX, sunY)
            )
        }
    }
}

private fun calculateSunProgress(sunriseTimeStr: String?, sunsetTimeStr: String?): Float {
    if (sunriseTimeStr == null || sunsetTimeStr == null) return 0.5f
    return try {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val sunrise = LocalTime.parse(sunriseTimeStr.takeLast(5), formatter)
        val sunset = LocalTime.parse(sunsetTimeStr.takeLast(5), formatter)
        val now = LocalTime.now()

        val totalDayMinutes = (sunset.toSecondOfDay() - sunrise.toSecondOfDay()).toFloat()
        val currentMinutes = (now.toSecondOfDay() - sunrise.toSecondOfDay()).toFloat()

        (currentMinutes / totalDayMinutes).coerceIn(0f, 1f)
    } catch (_: Exception) {
        0.5f
    }
}

private fun uvRisk(uv: Double): Pair<String, Color> = when {
    uv < 3.0 -> "Düşük" to Color(0xFF22C55E)
    uv < 6.0 -> "Orta" to Color(0xFFF59E0B)
    uv < 8.0 -> "Yüksek" to Color(0xFFEA580C)
    uv < 11.0 -> "Çok Yüksek" to Color(0xFFDC2626)
    else -> "Aşırı Tehlikeli" to Color(0xFF7C3AED)
}
