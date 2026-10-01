package com.kampplus.hava.feature.weather.data.remote.api

import com.kampplus.hava.feature.weather.data.remote.dto.AirQualityResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * https://air-quality-api.open-meteo.com/v1/air-quality
 */
interface OpenMeteoAirQualityApi {

    @GET("air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "european_aqi,us_aqi,pm10,pm2_5,nitrogen_dioxide,ozone,uv_index",
        @Query("timezone") timezone: String = "auto"
    ): AirQualityResponseDto
}
