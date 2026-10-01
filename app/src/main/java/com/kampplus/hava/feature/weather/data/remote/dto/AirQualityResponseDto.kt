package com.kampplus.hava.feature.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Open-Meteo Air Quality API yanıt modeli.
 * Endpoint: https://air-quality-api.open-meteo.com/v1/air-quality
 */
@Serializable
data class AirQualityResponseDto(
    val latitude: Double,
    val longitude: Double,
    val current: CurrentAirQualityDto? = null
)

@Serializable
data class CurrentAirQualityDto(
    val time: String? = null,
    @SerialName("european_aqi") val europeanAqi: Int? = null,
    @SerialName("us_aqi") val usAqi: Int? = null,
    @SerialName("pm10") val pm10: Double? = null,
    @SerialName("pm2_5") val pm25: Double? = null,
    @SerialName("ozone") val ozone: Double? = null,
    @SerialName("nitrogen_dioxide") val nitrogenDioxide: Double? = null,
    @SerialName("uv_index") val uvIndex: Double? = null
)
