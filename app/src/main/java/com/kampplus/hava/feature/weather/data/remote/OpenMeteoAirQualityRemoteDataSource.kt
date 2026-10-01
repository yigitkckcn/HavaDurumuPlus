package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.feature.weather.data.mapper.AirQualityDtoMapper
import com.kampplus.hava.feature.weather.data.remote.api.OpenMeteoAirQualityApi
import com.kampplus.hava.feature.weather.domain.model.AirQuality
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import javax.inject.Inject

class OpenMeteoAirQualityRemoteDataSource @Inject constructor(
    private val api: OpenMeteoAirQualityApi,
    private val mapper: AirQualityDtoMapper
) : AirQualityRemoteDataSource {

    override suspend fun getAirQuality(coordinates: Coordinates): AirQuality {
        val dto = api.getAirQuality(
            latitude = coordinates.latitude,
            longitude = coordinates.longitude
        )
        return mapper.map(dto)
    }
}
