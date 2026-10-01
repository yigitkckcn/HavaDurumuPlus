package com.kampplus.hava.feature.settings.presentation

import com.kampplus.hava.core.notification.WeatherNotificationManager
import com.kampplus.hava.core.notification.WeatherNotificationScheduler
import com.kampplus.hava.core.ui.theme.ThemeMode
import com.kampplus.hava.feature.settings.domain.model.NotificationCitySource
import com.kampplus.hava.feature.settings.domain.model.TemperatureUnit
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.feature.weather.domain.usecase.GetCitiesUseCase
import com.kampplus.hava.testing.FakeSettingsRepository
import com.kampplus.hava.testing.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeSettingsRepository()
    private val getCities = mockk<GetCitiesUseCase> {
        every { this@mockk.invoke() } returns listOf(
            City(745044L, "İstanbul", "Marmara", "Türkiye", Coordinates(41.01, 28.97))
        )
    }
    private val notificationManager = mockk<WeatherNotificationManager>(relaxed = true)
    private val notificationScheduler = mockk<WeatherNotificationScheduler>(relaxed = true)

    private fun createViewModel() = SettingsViewModel(
        settingsRepository = repository,
        getCities = getCities,
        notificationManager = notificationManager,
        notificationScheduler = notificationScheduler
    )

    @Test
    fun `theme mode change updates repository settings`() = runTest {
        val viewModel = createViewModel()
        viewModel.onThemeModeChanged(ThemeMode.DARK)
        advanceUntilIdle()

        assertEquals(ThemeMode.DARK, viewModel.settings.value.themeMode)
    }

    @Test
    fun `enabling notifications schedules work`() = runTest {
        val viewModel = createViewModel()
        viewModel.onNotificationsToggled(true)
        advanceUntilIdle()

        assertEquals(true, viewModel.settings.value.notificationsEnabled)
        verify { notificationScheduler.scheduleDailyNotification(hour = 8, minute = 0) }
    }

    @Test
    fun `notification time change updates repository and schedules work`() = runTest {
        val viewModel = createViewModel()
        viewModel.onNotificationsToggled(true)
        advanceUntilIdle()

        viewModel.onNotificationTimeChanged(hour = 9, minute = 30)
        advanceUntilIdle()

        assertEquals(9, viewModel.settings.value.notificationHour)
        assertEquals(30, viewModel.settings.value.notificationMinute)
        verify { notificationScheduler.scheduleDailyNotification(hour = 9, minute = 30) }
    }

    @Test
    fun `notification city source change updates repository`() = runTest {
        val viewModel = createViewModel()
        viewModel.onNotificationCitySourceChanged(NotificationCitySource.CUSTOM_CITY)
        advanceUntilIdle()

        assertEquals(NotificationCitySource.CUSTOM_CITY, viewModel.settings.value.notificationCitySource)
    }

    @Test
    fun `notification custom city select updates repository`() = runTest {
        val viewModel = createViewModel()
        val izmir = City(
            id = 311046L,
            name = "İzmir",
            region = "Ege",
            country = "Türkiye",
            coordinates = Coordinates(38.41, 27.13)
        )
        viewModel.onNotificationCustomCitySelected(izmir)
        advanceUntilIdle()

        assertEquals(NotificationCitySource.CUSTOM_CITY, viewModel.settings.value.notificationCitySource)
        assertEquals(311046L, viewModel.settings.value.notificationCustomCityId)
        assertEquals("İzmir", viewModel.settings.value.notificationCustomCityName)
    }

    @Test
    fun `temperature unit change updates repository settings`() = runTest {
        val viewModel = createViewModel()
        viewModel.onTemperatureUnitChanged(TemperatureUnit.FAHRENHEIT)
        advanceUntilIdle()

        assertEquals(TemperatureUnit.FAHRENHEIT, viewModel.settings.value.temperatureUnit)
    }
}
