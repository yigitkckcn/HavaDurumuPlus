package com.kampplus.hava.feature.weather.domain.model

enum class AlertSeverity {
    INFO,
    WARNING,
    DANGER
}

data class WeatherAlert(
    val title: String,
    val description: String,
    val severity: AlertSeverity,
    val emoji: String
)
