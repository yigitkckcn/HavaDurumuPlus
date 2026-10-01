package com.kampplus.hava.feature.weather.presentation.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.R
import com.kampplus.hava.feature.weather.presentation.model.AirQualityUiModel

@Composable
fun AirQualityCard(airQuality: AirQualityUiModel, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.air_quality_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(airQuality.aqiColor)
                    )
                    Text(
                        text = airQuality.aqiLabel.asString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = airQuality.aqiColor
                    )
                }
            }

            // AQI Indicator Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = airQuality.aqiValue.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = airQuality.aqiColor
                    )
                    Text(
                        text = "AQI",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Progress Bar
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        val fraction = (airQuality.aqiValue / 100f).coerceIn(0.05f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(airQuality.aqiColor)
                        )
                    }
                }
            }

            // Metrics row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                airQuality.pm25Text?.let {
                    AirMetric(label = stringResource(R.string.air_quality_pm25), value = it)
                }
                airQuality.pm10Text?.let {
                    AirMetric(label = stringResource(R.string.air_quality_pm10), value = it)
                }
                airQuality.uvIndexText?.let {
                    AirMetric(label = stringResource(R.string.air_quality_uv), value = it)
                }
                airQuality.ozoneText?.let {
                    AirMetric(label = stringResource(R.string.air_quality_o3), value = it)
                }
            }
        }
    }
}

@Composable
private fun AirMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 13.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
