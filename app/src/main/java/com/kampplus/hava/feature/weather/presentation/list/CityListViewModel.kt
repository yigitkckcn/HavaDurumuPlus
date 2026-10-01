package com.kampplus.hava.feature.weather.presentation.list

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
import com.kampplus.hava.feature.weather.domain.policy.WeatherConditionClassifier
import com.kampplus.hava.feature.weather.domain.usecase.GetCityWeathersUseCase
import com.kampplus.hava.feature.weather.domain.usecase.GetLocationWeatherUseCase
import com.kampplus.hava.feature.weather.domain.usecase.SearchCityWeathersUseCase
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.model.WeatherUiMapper
import com.kampplus.hava.feature.weather.presentation.model.toFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class CityListViewModel @Inject constructor(
    private val getCityWeathers: GetCityWeathersUseCase,
    private val searchCityWeathers: SearchCityWeathersUseCase,
    private val getLocationWeather: GetLocationWeatherUseCase,
    observeFavoriteCityIds: ObserveFavoriteCityIdsUseCase,
    private val toggleFavoriteCity: ToggleFavoriteCityUseCase,
    private val settingsRepository: SettingsRepository,
    private val conditionClassifier: WeatherConditionClassifier,
    private val uiMapper: WeatherUiMapper
) : ViewModel() {

    /** Detaya giderken ve favori eklerken şehrin tamamına ihtiyaç var; son yüklenen liste burada tutulur. */
    private var loadedCities: Map<Long, City> = emptyMap()

    private var exactLocationCity: City? = null
    private val _exactLocationWeather = MutableStateFlow<CityWeatherUiModel?>(null)
    val exactLocationWeather: StateFlow<CityWeatherUiModel?> = _exactLocationWeather.asStateFlow()

    private val query = MutableStateFlow("")

    /** Her artışta liste yeniden yüklenir (tekrar dene / yenile). */
    private val reloadTrigger = MutableStateFlow(0)

    /** Yenilemede mevcut liste ekranda kalır, Loading'e düşülmez. */
    private val isRefreshing = MutableStateFlow(false)

    /**
     * Yazarken her tuşa istek atılmaz: arama [SEARCH_DEBOUNCE_MS] kadar bekletilir.
     * Kısa sorguda öne çıkan şehirler gösterilir. flatMapLatest, yeni sorgu gelince eski isteği iptal eder.
     */
    private val results: Flow<AppResult<List<CityWeather>>?> = combine(
        query
            .map { it.trim().takeIf { text -> text.length >= MIN_QUERY_LENGTH }.orEmpty() }
            .debounce { if (it.isEmpty()) 0L else SEARCH_DEBOUNCE_MS }
            .distinctUntilChanged(),
        reloadTrigger
    ) { searchText, _ -> searchText }
        .flatMapLatest { searchText ->
            val source = if (searchText.isEmpty()) getCityWeathers() else searchCityWeathers(searchText)
            source
                .onEach { result ->
                    if (result is AppResult.Success) {
                        loadedCities = result.data.associate { it.city.id to it.city }
                        val firstCity = result.data.firstOrNull()
                        if (firstCity != null) {
                            val condition = conditionClassifier.classify(firstCity.current.weatherCode)
                            settingsRepository.updateActiveWeatherCondition(condition)
                        }
                    }
                    isRefreshing.value = false
                }
                .map<AppResult<List<CityWeather>>, AppResult<List<CityWeather>>?> { it }
                .onStart { if (!isRefreshing.value) emit(null) }
        }

    val uiState: StateFlow<CityListUiState> = combine(
        query,
        results,
        observeFavoriteCityIds(),
        settingsRepository.settings.map { it.temperatureUnit },
        isRefreshing
    ) { query, result, favoriteIds, tempUnit, refreshing ->
        CityListUiState(
            query = query,
            content = when (result) {
                null -> UiState.Loading
                is AppResult.Success ->
                    if (result.data.isEmpty()) {
                        UiState.Empty
                    } else {
                        val items = result.data
                            .map { uiMapper.toListItem(it, isFavorite = it.city.id in favoriteIds, unit = tempUnit) }
                            .sortedWith(compareByDescending<CityWeatherUiModel> { it.isFavorite }.thenBy { it.title })
                        UiState.Success(items)
                    }
                is AppResult.Failure -> UiState.Error(result.error.toUiText())
            },
            isRefreshing = refreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = CityListUiState()
    )

    /** Kullanıcının tam GPS koordinatlarına ait canlı hava durumunu doğrudan çeker. */
    fun updateExactLocation(city: City) {
        exactLocationCity = city
        viewModelScope.launch {
            getLocationWeather(city).collect { result ->
                if (result is AppResult.Success) {
                    val cityWeather = result.data.firstOrNull()
                    if (cityWeather != null) {
                        val unit = settingsRepository.settings.value.temperatureUnit
                        _exactLocationWeather.value = uiMapper.toListItem(cityWeather, isFavorite = false, unit = unit)
                    }
                }
            }
        }
    }

    fun findCity(cityId: Long): City? = if (exactLocationCity?.id == cityId) {
        exactLocationCity
    } else {
        loadedCities[cityId]
    }

    fun onQueryChange(text: String) {
        query.value = text
    }

    fun onRetry() {
        reloadTrigger.update { it + 1 }
    }

    fun onRefresh() {
        isRefreshing.value = true
        getCityWeathers.invalidateCache()
        reloadTrigger.update { it + 1 }
        exactLocationCity?.let { updateExactLocation(it) }
    }

    fun onToggleFavorite(cityId: Long) {
        val city = findCity(cityId) ?: return
        viewModelScope.launch { toggleFavoriteCity(city.toFavorite()) }
    }

    companion object {
        const val MIN_QUERY_LENGTH = 2
        const val SEARCH_DEBOUNCE_MS = 400L
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}
