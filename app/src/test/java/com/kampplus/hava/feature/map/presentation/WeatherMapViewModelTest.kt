package com.kampplus.hava.feature.map.presentation

import app.cash.turbine.test
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.feature.favorites.data.local.InMemoryFavoriteCityDataSource
import com.kampplus.hava.feature.favorites.data.repository.FavoriteCityRepositoryImpl
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCityIdsUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.feature.weather.domain.usecase.GetLocationWeatherUseCase
import com.kampplus.hava.testing.FakeSettingsRepository
import com.kampplus.hava.testing.FakeWeatherRepository
import com.kampplus.hava.testing.MainDispatcherRule
import com.kampplus.hava.testing.city
import com.kampplus.hava.testing.cityWeather
import com.kampplus.hava.testing.testUiMapper
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class WeatherMapViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testCity = city(id = 1, name = "Ankara", coordinates = Coordinates(39.9334, 32.8597))
    private val weatherRepository = FakeWeatherRepository(
        cityWeathersResult = { AppResult.Success(listOf(cityWeather(city = testCity))) }
    )
    private val favoritesRepository = FavoriteCityRepositoryImpl(InMemoryFavoriteCityDataSource())
    private val settingsRepository = FakeSettingsRepository()

    private fun createViewModel() = WeatherMapViewModel(
        getCityWeathers = GetCityWeathersUseCase(weatherRepository),
        getLocationWeather = GetLocationWeatherUseCase(weatherRepository),
        observeFavoriteCityIds = ObserveFavoriteCityIdsUseCase(favoritesRepository),
        toggleFavoriteCity = ToggleFavoriteCityUseCase(favoritesRepository),
        settingsRepository = settingsRepository,
        uiMapper = testUiMapper()
    )

    @Test
    fun `loads cities on map and selects first or Ankara by default`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            val state = awaitItem()
            assertTrue(state is UiState.Success)
            val markers = (state as UiState.Success).data
            assertEquals(1, markers.size)
            assertEquals("Ankara", markers.first().city.name)
        }
    }

    @Test
    fun `updateLiveUserLocation adds user pin and selects it`() = runTest {
        val viewModel = createViewModel()
        val userCity = city(id = 999999L, name = "Beşiktaş", coordinates = Coordinates(41.0422, 29.0042))

        viewModel.updateLiveUserLocation(userCity)
        testScheduler.advanceUntilIdle()

        assertEquals("Beşiktaş", viewModel.selectedCity.value?.city?.name)
        assertTrue(viewModel.selectedCity.value?.isUserLocation == true)
    }
}
