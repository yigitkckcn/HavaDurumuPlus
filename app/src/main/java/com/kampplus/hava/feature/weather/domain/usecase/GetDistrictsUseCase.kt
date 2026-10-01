package com.kampplus.hava.feature.weather.domain.usecase

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.repository.DistrictRepository
import javax.inject.Inject

class GetDistrictsUseCase @Inject constructor(
    private val repository: DistrictRepository
) {
    operator fun invoke(provinceId: Long): List<City> = repository.districtsOf(provinceId)
}
