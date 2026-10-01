package com.kampplus.hava.feature.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.toUiText
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCityIdsUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.settings.domain.repository.SettingsRepository
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.feature.weather.domain.usecase.GetLocationWeatherUseCase
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import com.kampplus.hava.feature.weather.presentation.model.toFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MapMarkerUiModel(
    val weatherItem: CityWeatherUiModel,
    val city: City,
    val latitude: Double,
    val longitude: Double,
    val isUserLocation: Boolean = false
)

@HiltViewModel
class WeatherMapViewModel @Inject constructor(
    private val getCityWeathers: GetCityWeathersUseCase,
    private val getLocationWeather: GetLocationWeatherUseCase,
    observeFavoriteCityIds: ObserveFavoriteCityIdsUseCase,
    private val toggleFavoriteCity: ToggleFavoriteCityUseCase,
    private val settingsRepository: SettingsRepository,
    private val uiMapper: WeatherUiMapper
) : ViewModel() {

    private val _selectedCity = MutableStateFlow<MapMarkerUiModel?>(null)
    val selectedCity: StateFlow<MapMarkerUiModel?> = _selectedCity.asStateFlow()

    private val exactLocationMarker = MutableStateFlow<MapMarkerUiModel?>(null)

    private val reloadTrigger = MutableStateFlow(0)

    val uiState: StateFlow<UiState<List<MapMarkerUiModel>>> = combine(
        getCityWeathers(),
        observeFavoriteCityIds(),
        settingsRepository.settings.map { it.temperatureUnit },
        exactLocationMarker,
        reloadTrigger
    ) { result, favoriteIds, tempUnit, userMarker, _ ->
        when (result) {
            is AppResult.Success -> {
                val baseMarkers = result.data.map { item ->
                    MapMarkerUiModel(
                        weatherItem = uiMapper.toListItem(item, isFavorite = item.city.id in favoriteIds, unit = tempUnit),
                        city = item.city,
                        latitude = item.city.coordinates.latitude,
                        longitude = item.city.coordinates.longitude,
                        isUserLocation = false
                    )
                }

                // Canlı GPS konum marker'ını harita pinlerinin en başına ekle
                val allMarkers = if (userMarker != null) {
                    listOf(userMarker) + baseMarkers
                } else {
                    baseMarkers
                }

                if (_selectedCity.value == null && allMarkers.isNotEmpty()) {
                    _selectedCity.value = userMarker ?: allMarkers.firstOrNull { it.city.name == "Ankara" } ?: allMarkers.first()
                }

                UiState.Success(allMarkers)
            }
            is AppResult.Failure -> UiState.Error(result.error.toUiText())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = UiState.Loading
    )

    /** Canlı GPS koordinatlarının hava durumunu doğrudan Open-Meteo'dan çeker ve haritaya pin olarak ekler. */
    fun updateLiveUserLocation(city: City) {
        viewModelScope.launch {
            getLocationWeather(city).collect { result ->
                if (result is AppResult.Success) {
                    val weather = result.data.firstOrNull()
                    if (weather != null) {
                        val unit = settingsRepository.settings.value.temperatureUnit
                        val marker = MapMarkerUiModel(
                            weatherItem = uiMapper.toListItem(weather, isFavorite = false, unit = unit),
                            city = city,
                            latitude = city.coordinates.latitude,
                            longitude = city.coordinates.longitude,
                            isUserLocation = true
                        )
                        exactLocationMarker.value = marker
                        _selectedCity.value = marker
                    }
                }
            }
        }
    }

    fun selectCity(marker: MapMarkerUiModel) {
        _selectedCity.value = marker
    }

    fun onToggleFavorite(city: City) {
        viewModelScope.launch {
            toggleFavoriteCity(city.toFavorite())
        }
    }

    fun onRetry() {
        reloadTrigger.value += 1
    }
}
