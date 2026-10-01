package com.kampplus.hava.testing

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.AirQuality
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.feature.weather.domain.repository.AirQualityRepository

class FakeAirQualityRepository : AirQualityRepository {

    var result: (Coordinates) -> AppResult<AirQuality> = {
        AppResult.Success(
            AirQuality(
                europeanAqi = 15,
                usAqi = 25,
                pm10 = 12.0,
                pm25 = 8.0,
                ozone = 35.0,
                nitrogenDioxide = 10.0,
                uvIndex = 3.5
            )
        )
    }

    override suspend fun getAirQuality(coordinates: Coordinates): AppResult<AirQuality> = result(coordinates)
}
