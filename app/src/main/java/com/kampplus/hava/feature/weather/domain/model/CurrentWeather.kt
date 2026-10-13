package com.kampplus.hava.feature.weather.domain.model

import java.time.LocalDateTime

<<<<<<< Updated upstream
/** Bir şehrin anlık ölçülen hava durumu değerleri. */
=======
/** Bir şehrin anlık hava durumu. Zaman, şehrin yerel saatindedir. */
>>>>>>> Stashed changes
data class CurrentWeather(
    val temperatureC: Double,
    val weatherCode: WeatherCode,
    val observedAt: LocalDateTime,
    val apparentTemperatureC: Double? = null,
    val humidityPercent: Int? = null,
    val windSpeedKmh: Double? = null,
    val isDay: Boolean = true
)
