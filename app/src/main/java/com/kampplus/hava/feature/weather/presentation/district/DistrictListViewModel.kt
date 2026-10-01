package com.kampplus.hava.feature.weather.presentation.district

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.navigation.DistrictListDestination
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.toUiText
import com.kampplus.hava.feature.favorites.domain.usecase.ObserveFavoriteCityIdsUseCase
import com.kampplus.hava.feature.favorites.domain.usecase.ToggleFavoriteCityUseCase
import com.kampplus.hava.feature.settings.domain.repository.SettingsRepository
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.feature.weather.domain.repository.WeatherRepository
import com.kampplus.hava.feature.weather.domain.usecase.GetDistrictsUseCase
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import com.kampplus.hava.feature.weather.presentation.model.toFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DistrictListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getDistricts: GetDistrictsUseCase,
    private val weatherRepository: WeatherRepository,
    observeFavoriteCityIds: ObserveFavoriteCityIdsUseCase,
    private val toggleFavoriteCity: ToggleFavoriteCityUseCase,
    private val settingsRepository: SettingsRepository,
    private val uiMapper: WeatherUiMapper,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<DistrictListDestination>()

    /** İlin domain modeli — favori toggle ve hava sorgusu için. */
    val province: City = City(
        id = args.provinceId,
        name = args.provinceName,
        region = args.provinceRegion,
        country = args.provinceCountry,
        coordinates = Coordinates(args.provinceLat, args.provinceLon)
    )

    private val districts: List<City> = getDistricts(args.provinceId)
    private val districtMap: Map<Long, City> = districts.associateBy { it.id }

    // --- Hava durumu sonuçları ---
    private val _provinceWeather = MutableStateFlow<com.kampplus.hava.feature.weather.domain.model.CityWeather?>(null)
    private val _districtResult = MutableStateFlow<AppResult<List<com.kampplus.hava.feature.weather.domain.model.CityWeather>>?>(null)
    private val _isRefreshing = MutableStateFlow(false)

    /** Üstteki il kartı — sıcaklık + favori durumu. */
    val provinceCardState: StateFlow<CityWeatherUiModel?> = combine(
        _provinceWeather,
        observeFavoriteCityIds(),
        settingsRepository.settings.map { it.temperatureUnit },
    ) { weather, favoriteIds, tempUnit ->
        weather?.let {
            uiMapper.toListItem(it, isFavorite = province.id in favoriteIds, unit = tempUnit)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = null
    )

    /** İlçe listesi. */
    val districtUiState: StateFlow<UiState<List<CityWeatherUiModel>>> = combine(
        _districtResult,
        observeFavoriteCityIds(),
        settingsRepository.settings.map { it.temperatureUnit },
    ) { result, favoriteIds, tempUnit ->
        when {
            result == null -> UiState.Loading
            result is AppResult.Failure -> UiState.Error(result.error.toUiText())
            result is AppResult.Success && result.data.isEmpty() -> UiState.Empty
            result is AppResult.Success -> UiState.Success(
                result.data
                    .map { cw -> uiMapper.toListItem(cw, isFavorite = cw.city.id in favoriteIds, unit = tempUnit) }
                    .sortedBy { it.title }
            )
            else -> UiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = UiState.Loading
    )

    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init { load() }

    fun onRetry() = load()

    fun onRefresh() {
        _isRefreshing.value = true
        load()
    }

    /** İl veya ilçe favoriye ekle/çıkar. */
    fun onToggleFavorite(cityId: Long) {
        val city = if (cityId == province.id) province else districtMap[cityId] ?: return
        viewModelScope.launch { toggleFavoriteCity(city.toFavorite()) }
    }

    fun findDistrict(cityId: Long): City? = districtMap[cityId]

    private fun load() {
        val allCities = listOf(province) + districts
        viewModelScope.launch {
            _districtResult.value = null
            weatherRepository.getCurrentWeather(allCities).collect { result ->
                when (result) {
                    is AppResult.Success -> {
                        val items = result.data
                        _provinceWeather.value = items.firstOrNull()
                        _districtResult.value = AppResult.Success(items.drop(1))
                    }
                    is AppResult.Failure -> {
                        _districtResult.value = AppResult.Failure(result.error)
                    }
                }
                _isRefreshing.value = false
            }
        }
    }
}
