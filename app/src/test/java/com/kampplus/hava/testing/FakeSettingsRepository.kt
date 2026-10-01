package com.kampplus.hava.testing

import com.kampplus.hava.feature.settings.domain.model.AppSettings
import com.kampplus.hava.feature.settings.domain.model.NotificationCitySource
import com.kampplus.hava.feature.settings.domain.model.TemperatureUnit
import com.kampplus.hava.feature.settings.domain.model.ThemeMode
import com.kampplus.hava.feature.settings.domain.repository.SettingsRepository
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsRepository : SettingsRepository {

    private val _settings = MutableStateFlow(AppSettings())
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _activeWeatherCondition = MutableStateFlow<WeatherCondition?>(WeatherCondition.Clear)
    override val activeWeatherCondition: StateFlow<WeatherCondition?> = _activeWeatherCondition.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        _settings.value = _settings.value.copy(notificationsEnabled = enabled)
    }

    override suspend fun setNotificationTime(hour: Int, minute: Int) {
        _settings.value = _settings.value.copy(notificationHour = hour, notificationMinute = minute)
    }

    override suspend fun setNotificationCitySource(source: NotificationCitySource) {
        _settings.value = _settings.value.copy(notificationCitySource = source)
    }

    override suspend fun setNotificationCustomCity(cityId: Long, cityName: String) {
        _settings.value = _settings.value.copy(
            notificationCitySource = NotificationCitySource.CUSTOM_CITY,
            notificationCustomCityId = cityId,
            notificationCustomCityName = cityName
        )
    }

    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        _settings.value = _settings.value.copy(temperatureUnit = unit)
    }

    override suspend fun updateActiveWeatherCondition(condition: WeatherCondition) {
        _activeWeatherCondition.value = condition
    }
}
