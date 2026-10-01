package com.kampplus.hava.feature.weather.domain.repository

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.AirQuality
import com.kampplus.hava.feature.weather.domain.model.Coordinates

interface AirQualityRepository {
    suspend fun getAirQuality(coordinates: Coordinates): AppResult<AirQuality>
}
