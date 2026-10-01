package com.kampplus.hava.feature.weather.domain.usecase

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.AirQuality
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.feature.weather.domain.repository.AirQualityRepository
import javax.inject.Inject

class GetAirQualityUseCase @Inject constructor(
    private val repository: AirQualityRepository
) {
    suspend operator fun invoke(coordinates: Coordinates): AppResult<AirQuality> = repository.getAirQuality(coordinates)
}
