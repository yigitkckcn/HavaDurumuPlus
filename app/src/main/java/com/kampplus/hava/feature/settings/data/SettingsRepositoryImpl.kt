package com.kampplus.hava.feature.settings.data

import android.content.Context
import android.content.SharedPreferences
import com.kampplus.hava.core.ui.theme.ThemeMode
import com.kampplus.hava.feature.settings.domain.model.AppSettings
import com.kampplus.hava.feature.settings.domain.model.NotificationCitySource
import com.kampplus.hava.feature.settings.domain.model.TemperatureUnit
import com.kampplus.hava.feature.settings.domain.repository.SettingsRepository
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SettingsRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _activeWeatherCondition = MutableStateFlow<WeatherCondition?>(WeatherCondition.Clear)
    override val activeWeatherCondition: StateFlow<WeatherCondition?> = _activeWeatherCondition.asStateFlow()

    private fun loadSettings(): AppSettings {
        val themeModeName = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        val themeMode = runCatching { ThemeMode.valueOf(themeModeName) }.getOrDefault(ThemeMode.SYSTEM)
        val notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)
        val notificationHour = prefs.getInt(KEY_NOTIFICATION_HOUR, 8)
        val notificationMinute = prefs.getInt(KEY_NOTIFICATION_MINUTE, 0)
        val citySourceName = prefs.getString(KEY_NOTIFICATION_CITY_SOURCE, NotificationCitySource.CURRENT_LOCATION.name)
            ?: NotificationCitySource.CURRENT_LOCATION.name
        val citySource = runCatching {
            NotificationCitySource.valueOf(citySourceName)
        }.getOrDefault(NotificationCitySource.CURRENT_LOCATION)
        val customCityId = prefs.getLong(KEY_NOTIFICATION_CUSTOM_CITY_ID, 745044L)
        val customCityName = prefs.getString(KEY_NOTIFICATION_CUSTOM_CITY_NAME, "İstanbul") ?: "İstanbul"
        val tempUnitName = prefs.getString(KEY_TEMP_UNIT, TemperatureUnit.CELSIUS.name) ?: TemperatureUnit.CELSIUS.name
        val tempUnit = runCatching { TemperatureUnit.valueOf(tempUnitName) }.getOrDefault(TemperatureUnit.CELSIUS)

        return AppSettings(
            themeMode = themeMode,
            notificationsEnabled = notificationsEnabled,
            notificationHour = notificationHour,
            notificationMinute = notificationMinute,
            notificationCitySource = citySource,
            notificationCustomCityId = customCityId,
            notificationCustomCityName = customCityName,
            temperatureUnit = tempUnit
        )
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
        _settings.value = _settings.value.copy(notificationsEnabled = enabled)
    }

    override suspend fun setNotificationTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt(KEY_NOTIFICATION_HOUR, hour)
            .putInt(KEY_NOTIFICATION_MINUTE, minute)
            .apply()
        _settings.value = _settings.value.copy(
            notificationHour = hour,
            notificationMinute = minute
        )
    }

    override suspend fun setNotificationCitySource(source: NotificationCitySource) {
        prefs.edit().putString(KEY_NOTIFICATION_CITY_SOURCE, source.name).apply()
        _settings.value = _settings.value.copy(notificationCitySource = source)
    }

    override suspend fun setNotificationCustomCity(cityId: Long, cityName: String) {
        prefs.edit()
            .putLong(KEY_NOTIFICATION_CUSTOM_CITY_ID, cityId)
            .putString(KEY_NOTIFICATION_CUSTOM_CITY_NAME, cityName)
            .putString(KEY_NOTIFICATION_CITY_SOURCE, NotificationCitySource.CUSTOM_CITY.name)
            .apply()
        _settings.value = _settings.value.copy(
            notificationCitySource = NotificationCitySource.CUSTOM_CITY,
            notificationCustomCityId = cityId,
            notificationCustomCityName = cityName
        )
    }

    override suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        prefs.edit().putString(KEY_TEMP_UNIT, unit.name).apply()
        _settings.value = _settings.value.copy(temperatureUnit = unit)
    }

    override suspend fun updateActiveWeatherCondition(condition: WeatherCondition) {
        _activeWeatherCondition.value = condition
    }

    private companion object {
        const val PREFS_NAME = "hava_app_settings"
        const val KEY_THEME_MODE = "key_theme_mode"
        const val KEY_NOTIFICATIONS_ENABLED = "key_notifications_enabled"
        const val KEY_NOTIFICATION_HOUR = "key_notification_hour"
        const val KEY_NOTIFICATION_MINUTE = "key_notification_minute"
        const val KEY_NOTIFICATION_CITY_SOURCE = "key_notification_city_source"
        const val KEY_NOTIFICATION_CUSTOM_CITY_ID = "key_notification_custom_city_id"
        const val KEY_NOTIFICATION_CUSTOM_CITY_NAME = "key_notification_custom_city_name"
        const val KEY_TEMP_UNIT = "key_temp_unit"
    }
}
