package com.kampplus.hava.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.core.notification.WeatherNotificationManager
import com.kampplus.hava.core.notification.WeatherNotificationScheduler
import com.kampplus.hava.core.ui.theme.ThemeMode
import com.kampplus.hava.feature.settings.domain.model.AppSettings
import com.kampplus.hava.feature.settings.domain.model.NotificationCitySource
import com.kampplus.hava.feature.settings.domain.model.TemperatureUnit
import com.kampplus.hava.feature.settings.domain.repository.SettingsRepository
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.usecase.GetCitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val getCities: GetCitiesUseCase,
    private val notificationManager: WeatherNotificationManager,
    private val notificationScheduler: WeatherNotificationScheduler
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settings
    val availableCities: List<City> = getCities()

    private val _messageEvent = MutableSharedFlow<String>()
    val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

    fun onThemeModeChanged(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun onNotificationsToggled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
            val current = settings.value
            if (enabled) {
                notificationScheduler.scheduleDailyNotification(
                    hour = current.notificationHour,
                    minute = current.notificationMinute
                )
            } else {
                notificationScheduler.cancelDailyNotification()
            }
        }
    }

    fun onNotificationTimeChanged(hour: Int, minute: Int) {
        viewModelScope.launch {
            settingsRepository.setNotificationTime(hour, minute)
            if (settings.value.notificationsEnabled) {
                notificationScheduler.scheduleDailyNotification(hour, minute)
            }
        }
    }

    fun onNotificationCitySourceChanged(source: NotificationCitySource) {
        viewModelScope.launch {
            settingsRepository.setNotificationCitySource(source)
        }
    }

    fun onNotificationCustomCitySelected(city: City) {
        viewModelScope.launch {
            settingsRepository.setNotificationCustomCity(city.id, city.name)
        }
    }

    fun onTemperatureUnitChanged(unit: TemperatureUnit) {
        viewModelScope.launch {
            settingsRepository.setTemperatureUnit(unit)
        }
    }

    fun onSendTestNotification() {
        val s = settings.value
        val cityName = if (s.notificationCitySource == NotificationCitySource.CURRENT_LOCATION) {
            "Mevcut Konumun"
        } else {
            s.notificationCustomCityName
        }

        notificationManager.showWeatherNotification(
            title = "$cityName Hava Durumu",
            content = "22°C ⛅ Parçalı bulutlu — Günlük hava durumu özeti."
        )
        viewModelScope.launch {
            _messageEvent.emit("Test bildirimi gönderildi!")
        }
    }
}
