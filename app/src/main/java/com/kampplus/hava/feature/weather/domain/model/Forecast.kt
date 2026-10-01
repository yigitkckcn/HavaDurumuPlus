package com.kampplus.hava.feature.weather.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/** Bir şehrin anlık durumu + önümüzdeki saatler ve günler. Zamanlar şehrin yerel saatindedir. */
data class Forecast(
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val sunrise: LocalDateTime? = null,
    val sunset: LocalDateTime? = null,
    val uvIndexMax: Double? = null
)

data class HourlyForecast(
    val time: LocalDateTime,
    val temperatureC: Double,
    val weatherCode: WeatherCode,
    val precipitationProbability: Int? = null,
    val uvIndex: Double? = null
)

data class DailyForecast(
    val date: LocalDate,
    val minTemperatureC: Double,
    val maxTemperatureC: Double,
    val weatherCode: WeatherCode,
    val precipitationProbability: Int? = null,
    val sunrise: LocalDateTime? = null,
    val sunset: LocalDateTime? = null,
    val uvIndexMax: Double? = null
)
