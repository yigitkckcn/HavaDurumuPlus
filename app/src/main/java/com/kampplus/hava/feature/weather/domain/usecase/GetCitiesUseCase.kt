package com.kampplus.hava.feature.weather.domain.usecase

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.repository.CityRepository
import javax.inject.Inject

class GetCitiesUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    operator fun invoke(): List<City> = cityRepository.getPredefinedCities()
}
