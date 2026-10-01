package com.kampplus.hava.feature.weather.presentation.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.component.EmptyView
import com.kampplus.hava.core.ui.component.ErrorView
import com.kampplus.hava.core.ui.component.ShimmerList
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.weather.presentation.list.component.CitySearchField
import com.kampplus.hava.feature.weather.presentation.list.component.CityWeatherCard
import com.kampplus.hava.feature.weather.presentation.list.component.CurrentLocationCard
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    uiState: CityListUiState,
    currentLocationWeather: CityWeatherUiModel?,
    isLoadingLocation: Boolean,
    onQueryChange: (String) -> Unit,
    onCityClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    onLocationClick: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_hava_logo),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = stringResource(R.string.list_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLocationClick) {
                        Icon(
                            imageVector = Icons.Filled.MyLocation,
                            contentDescription = stringResource(R.string.location_find_me),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CitySearchField(
                query = uiState.query,
                onQueryChange = onQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                ListContent(
                    uiState = uiState,
                    currentLocationWeather = currentLocationWeather,
                    isLoadingLocation = isLoadingLocation,
                    onRequestLocation = onLocationClick,
                    onCityClick = onCityClick,
                    onFavoriteClick = onFavoriteClick,
                    onRetry = onRetry
                )
            }
        }
    }
}

@Composable
private fun ListContent(
    uiState: CityListUiState,
    currentLocationWeather: CityWeatherUiModel?,
    isLoadingLocation: Boolean,
    onRequestLocation: () -> Unit,
    onCityClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val content = uiState.content) {
            UiState.Loading -> ShimmerList()
            UiState.Empty -> EmptyView(
                icon = Icons.Filled.Search,
                title = stringResource(R.string.list_empty_title),
                message = if (uiState.isSearching) {
                    stringResource(R.string.search_empty_message, uiState.query.trim())
                } else {
                    stringResource(R.string.list_empty_message)
                }
            )
            is UiState.Error -> ErrorView(message = content.message.asString(), onRetry = onRetry)
            is UiState.Success -> CityList(
                items = content.data,
                currentLocationWeather = currentLocationWeather,
                isLoadingLocation = isLoadingLocation,
                onRequestLocation = onRequestLocation,
                onCityClick = onCityClick,
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}

@Composable
private fun CityList(
    items: List<CityWeatherUiModel>,
    currentLocationWeather: CityWeatherUiModel?,
    isLoadingLocation: Boolean,
    onRequestLocation: () -> Unit,
    onCityClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // En Üstte Sürekli Duran Canlı GPS Konum Kartı
        item(key = "current_location_card") {
            CurrentLocationCard(
                currentCityWeather = currentLocationWeather,
                isLoadingLocation = isLoadingLocation,
                onRequestLocation = onRequestLocation,
                onClick = {
                    currentLocationWeather?.let { onCityClick(it.cityId) } ?: onRequestLocation()
                }
            )
        }

        items(items = items, key = { it.cityId }) { item ->
            CityWeatherCard(
                item = item,
                onClick = { onCityClick(item.cityId) },
                onFavoriteClick = { onFavoriteClick(item.cityId) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CityListScreenPreview() {
    HavaTheme {
        CityListScreen(
            uiState = CityListUiState(
                content = UiState.Success(
                    List(5) { index ->
                        CityWeatherUiModel(
                            cityId = index.toLong(),
                            title = "İstanbul",
                            subtitle = "İstanbul, Türkiye",
                            temperatureText = "2$index°",
                            temperatureC = 20.0 + index,
                            conditionEmoji = "⛅",
                            conditionLabel = UiText.Dynamic("Parçalı bulutlu")
                        )
                    }
                )
            ),
            currentLocationWeather = null,
            isLoadingLocation = false,
            onQueryChange = {},
            onCityClick = {},
            onFavoriteClick = {},
            onLocationClick = {},
            onRetry = {},
            onRefresh = {}
        )
    }
}
