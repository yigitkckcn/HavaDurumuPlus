package com.kampplus.hava.feature.weather.domain.usecase

import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.feature.weather.domain.model.AirQualityLevel
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.testing.FakeAirQualityRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAirQualityUseCaseTest {

    private val repository = FakeAirQualityRepository()
    private val useCase = GetAirQualityUseCase(repository)

    @Test
    fun `returns air quality data and computes correct level`() = runTest {
        val result = useCase(Coordinates(41.0082, 28.9784))

        assertTrue(result is AppResult.Success)
        val airQuality = (result as AppResult.Success).data
        assertEquals(15, airQuality.europeanAqi)
        assertEquals(AirQualityLevel.GOOD, airQuality.level)
    }
}
