package com.kampplus.hava.feature.settings.domain.model

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

enum class NotificationCitySource {
    CURRENT_LOCATION,
    CUSTOM_CITY
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = false,
    val notificationHour: Int = 8,
    val notificationMinute: Int = 0,
    val notificationCitySource: NotificationCitySource = NotificationCitySource.CURRENT_LOCATION,
    val notificationCustomCityId: Long = 745044L,
    val notificationCustomCityName: String = "İstanbul",
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS
)
