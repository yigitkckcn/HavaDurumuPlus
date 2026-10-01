package com.kampplus.hava.feature.weather.domain.repository

import com.kampplus.hava.feature.weather.domain.model.City

interface DistrictRepository {
    fun districtsOf(provinceId: Long): List<City>
}
