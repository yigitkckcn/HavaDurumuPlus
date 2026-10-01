package com.kampplus.hava.feature.settings.domain.repository

import com.kampplus.hava.feature.settings.domain.model.AppSettings
import com.kampplus.hava.feature.settings.domain.model.NotificationCitySource
import com.kampplus.hava.feature.settings.domain.model.TemperatureUnit
import com.kampplus.hava.feature.settings.domain.model.ThemeMode
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<AppSettings>
    val activeWeatherCondition: StateFlow<WeatherCondition?>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setNotificationTime(hour: Int, minute: Int)
    suspend fun setNotificationCitySource(source: NotificationCitySource)
    suspend fun setNotificationCustomCity(cityId: Long, cityName: String)
    suspend fun setTemperatureUnit(unit: TemperatureUnit)
    suspend fun updateActiveWeatherCondition(condition: WeatherCondition)
}
