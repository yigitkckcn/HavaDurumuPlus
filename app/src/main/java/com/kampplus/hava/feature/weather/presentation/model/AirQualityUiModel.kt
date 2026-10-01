package com.kampplus.hava.feature.weather.presentation.model

import androidx.compose.ui.graphics.Color
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.domain.model.AirQualityLevel

data class AirQualityUiModel(
    val aqiValue: Int,
    val aqiLabel: UiText,
    val aqiColor: Color,
    val pm25Text: String?,
    val pm10Text: String?,
    val ozoneText: String?,
    val no2Text: String?,
    val uvIndexText: String?
)

fun AirQualityLevel.toColor(): Color = when (this) {
    AirQualityLevel.GOOD -> Color(0xFF22C55E) // Green
    AirQualityLevel.MODERATE -> Color(0xFFEAB308) // Yellow
    AirQualityLevel.UNHEALTHY_SENSITIVE -> Color(0xFFF97316) // Orange
    AirQualityLevel.UNHEALTHY -> Color(0xFFEF4444) // Red
    AirQualityLevel.VERY_UNHEALTHY -> Color(0xFFA855F7) // Purple
    AirQualityLevel.HAZARDOUS -> Color(0xFF7F1D1D) // Dark Maroon
}

fun AirQualityLevel.toLabel(): UiText = when (this) {
    AirQualityLevel.GOOD -> UiText.Resource(R.string.air_quality_good)
    AirQualityLevel.MODERATE -> UiText.Resource(R.string.air_quality_moderate)
    AirQualityLevel.UNHEALTHY_SENSITIVE -> UiText.Resource(R.string.air_quality_unhealthy_sensitive)
    AirQualityLevel.UNHEALTHY -> UiText.Resource(R.string.air_quality_unhealthy)
    AirQualityLevel.VERY_UNHEALTHY -> UiText.Resource(R.string.air_quality_very_unhealthy)
    AirQualityLevel.HAZARDOUS -> UiText.Resource(R.string.air_quality_hazardous)
}
