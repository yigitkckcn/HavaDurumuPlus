package com.kampplus.hava.feature.weather.domain.policy

import com.kampplus.hava.feature.weather.domain.model.AlertSeverity
import com.kampplus.hava.feature.weather.domain.model.CurrentWeather
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherAlertDetectorTest {

    private val detector = WeatherAlertDetector()

    @Test
    fun `detects thunderstorm danger alert for code 95`() {
        val current = CurrentWeather(
            temperatureC = 22.0,
            weatherCode = WeatherCode(95),
            observedAt = LocalDateTime.now()
        )
        val alerts = detector.detectAlerts(current)

        assertTrue(alerts.isNotEmpty())
        assertEquals(AlertSeverity.DANGER, alerts.first().severity)
        assertTrue(alerts.first().title.contains("Fırtına"))
    }

    @Test
    fun `detects extreme heat alert for temperatures above 37 degrees`() {
        val current = CurrentWeather(
            temperatureC = 40.0,
            weatherCode = WeatherCode(0),
            observedAt = LocalDateTime.now()
        )
        val alerts = detector.detectAlerts(current)

        assertTrue(alerts.any { it.severity == AlertSeverity.DANGER && it.title.contains("Sıcaklık") })
    }

    @Test
    fun `detects strong wind alert for wind over 60 kmh`() {
        val current = CurrentWeather(
            temperatureC = 18.0,
            weatherCode = WeatherCode(1),
            observedAt = LocalDateTime.now(),
            windSpeedKmh = 65.0
        )
        val alerts = detector.detectAlerts(current)

        assertTrue(alerts.any { it.severity == AlertSeverity.DANGER && it.title.contains("Rüzgâr") })
    }

    @Test
    fun `returns no alerts for mild pleasant weather`() {
        val current = CurrentWeather(
            temperatureC = 22.0,
            weatherCode = WeatherCode(0),
            observedAt = LocalDateTime.now(),
            windSpeedKmh = 12.0
        )
        val alerts = detector.detectAlerts(current)

        assertTrue(alerts.isEmpty())
    }
}
