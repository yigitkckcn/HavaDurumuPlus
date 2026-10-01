package com.kampplus.hava.feature.weather.data.repository

import com.kampplus.hava.feature.weather.data.local.DistrictCatalog
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.repository.DistrictRepository
import javax.inject.Inject

class DistrictRepositoryImpl @Inject constructor(
    private val catalog: DistrictCatalog
) : DistrictRepository {
    override fun districtsOf(provinceId: Long): List<City> = catalog.districtsOf(provinceId)
}
