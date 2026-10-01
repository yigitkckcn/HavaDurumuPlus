package com.kampplus.hava.feature.weather.data.mapper

import com.kampplus.hava.feature.weather.data.remote.dto.AirQualityResponseDto
import com.kampplus.hava.feature.weather.domain.model.AirQuality
import javax.inject.Inject

class AirQualityDtoMapper @Inject constructor() {

    fun map(dto: AirQualityResponseDto): AirQuality {
        val current = dto.current
        return AirQuality(
            europeanAqi = current?.europeanAqi ?: 0,
            usAqi = current?.usAqi,
            pm10 = current?.pm10,
            pm25 = current?.pm25,
            ozone = current?.ozone,
            nitrogenDioxide = current?.nitrogenDioxide,
            uvIndex = current?.uvIndex
        )
    }
}
