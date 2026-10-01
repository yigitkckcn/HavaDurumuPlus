package com.kampplus.hava.feature.weather.presentation.model

import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.weather.domain.model.WeatherAlert
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition

data class ForecastUiModel(
    val cityId: Long,
    val cityName: String,
    val subtitle: String,
    val temperatureText: String,
    val temperatureC: Double,
    val conditionEmoji: String,
    val conditionLabel: UiText,
    val weatherCondition: WeatherCondition? = null,
    val feelsLikeText: String?,
    val humidityText: String?,
    val windText: String?,
    val hourly: List<HourlyUiModel>,
    val hourlyChartPoints: List<HourlyChartPointUiModel> = emptyList(),
    val daily: List<DailyUiModel>,
    val airQuality: AirQualityUiModel? = null,
    val alerts: List<WeatherAlert> = emptyList(),
    val sunriseTimeText: String? = null,
    val sunsetTimeText: String? = null,
    val uvIndex: Double? = null,
    val isFavorite: Boolean = false
)

data class HourlyUiModel(
    val timeText: String,
    val emoji: String,
    val temperatureText: String,
    val precipitationText: String?
)

data class HourlyChartPointUiModel(
    val timeText: String,
    val temperatureC: Double,
    val emoji: String,
    val precipitationProbability: Int?
)

data class DailyUiModel(
    val dayLabel: UiText,
    val emoji: String,
    val minText: String,
    val maxText: String,
    val precipitationText: String?
)
