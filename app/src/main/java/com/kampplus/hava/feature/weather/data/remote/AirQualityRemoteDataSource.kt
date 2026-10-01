package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.feature.weather.domain.model.AirQuality
import com.kampplus.hava.feature.weather.domain.model.Coordinates

interface AirQualityRemoteDataSource {
    suspend fun getAirQuality(coordinates: Coordinates): AirQuality
}
