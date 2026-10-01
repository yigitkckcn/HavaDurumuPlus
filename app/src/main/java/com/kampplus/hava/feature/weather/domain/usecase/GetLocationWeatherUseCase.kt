package com.kampplus.hava.feature.weather.domain.usecase

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.repository.WeatherRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetLocationWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    operator fun invoke(city: City): Flow<AppResult<List<CityWeather>>> = weatherRepository.getCurrentWeather(listOf(city))
}
