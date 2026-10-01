package com.kampplus.hava.feature.weather.domain.model

/**
 * Hava Kalitesi İndeksi (AQI) ve partikül madde değerleri.
 * Avrupa Çevre Ajansı (CAQI/EAQI) ve US AQI standartlarına uygundur.
 */
data class AirQuality(
    val europeanAqi: Int,
    val usAqi: Int? = null,
    val pm10: Double? = null,
    val pm25: Double? = null,
    val ozone: Double? = null,
    val nitrogenDioxide: Double? = null,
    val uvIndex: Double? = null
) {
    val level: AirQualityLevel get() = AirQualityLevel.fromEuropeanAqi(europeanAqi)
}

enum class AirQualityLevel {
    GOOD,
    MODERATE,
    UNHEALTHY_SENSITIVE,
    UNHEALTHY,
    VERY_UNHEALTHY,
    HAZARDOUS;

    companion object {
        fun fromEuropeanAqi(aqi: Int): AirQualityLevel = when {
            aqi <= 20 -> GOOD
            aqi <= 40 -> MODERATE
            aqi <= 60 -> UNHEALTHY_SENSITIVE
            aqi <= 80 -> UNHEALTHY
            aqi <= 100 -> VERY_UNHEALTHY
            else -> HAZARDOUS
        }
    }
}
