package com.kampplus.hava.feature.weather.presentation.detail.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.feature.weather.domain.model.AlertSeverity
import com.kampplus.hava.feature.weather.domain.model.WeatherAlert

@Composable
fun WeatherAlertBanner(alerts: List<WeatherAlert>, modifier: Modifier = Modifier) {
    if (alerts.isEmpty()) return

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        alerts.forEach { alert ->
            val (bgColor, textColor, iconColor) = when (alert.severity) {
                AlertSeverity.DANGER -> Triple(Color(0xFFFEF2F2), Color(0xFF991B1B), Color(0xFFDC2626))
                AlertSeverity.WARNING -> Triple(Color(0xFFFFFBEB), Color(0xFF92400E), Color(0xFFF59E0B))
                AlertSeverity.INFO -> Triple(Color(0xFFEFF6FF), Color(0xFF1E40AF), Color(0xFF3B82F6))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = alert.emoji, fontSize = 18.sp)
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = alert.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}
