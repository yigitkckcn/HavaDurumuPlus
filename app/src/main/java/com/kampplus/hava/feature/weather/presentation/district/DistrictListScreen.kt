package com.kampplus.hava.feature.weather.presentation.district

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.component.EmptyView
import com.kampplus.hava.core.ui.component.ErrorView
import com.kampplus.hava.core.ui.component.FavoriteToggleButton
import com.kampplus.hava.core.ui.component.ShimmerList
import com.kampplus.hava.core.ui.component.TemperatureBadge
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.weather.presentation.list.component.CityWeatherCard
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.model.temperatureColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictListScreen(
    provinceName: String,
    provinceCard: CityWeatherUiModel?,
    districtUiState: UiState<List<CityWeatherUiModel>>,
    isRefreshing: Boolean,
    onBack: () -> Unit,
    onProvinceClick: () -> Unit,
    onProvinceFavoriteClick: () -> Unit,
    onDistrictClick: (Long) -> Unit,
    onDistrictFavoriteClick: (Long) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = provinceName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Üstte ilin genel hava kartı
                item(key = "province_header") {
                    ProvinceHeaderCard(
                        card = provinceCard,
                        provinceName = provinceName,
                        onClick = onProvinceClick,
                        onFavoriteClick = onProvinceFavoriteClick
                    )
                }

                // 2. Bölüm başlığı
                item(key = "districts_title") {
                    Text(
                        text = stringResource(R.string.district_section_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                // 3. İlçe listesi
                when (districtUiState) {
                    UiState.Loading -> item { ShimmerList() }
                    UiState.Empty -> item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyView(
                                icon = Icons.Filled.LocationCity,
                                title = stringResource(R.string.district_empty_title),
                                message = stringResource(R.string.district_empty_message)
                            )
                        }
                    }
                    is UiState.Error -> item {
                        ErrorView(message = districtUiState.message.asString(), onRetry = onRetry)
                    }
                    is UiState.Success -> items(
                        items = districtUiState.data,
                        key = { it.cityId }
                    ) { item ->
                        CityWeatherCard(
                            item = item,
                            onClick = { onDistrictClick(item.cityId) },
                            onFavoriteClick = { onDistrictFavoriteClick(item.cityId) }
                        )
                    }
                }
            }
        }
    }
}

/** Üstteki büyük il kartı — sıcaklık, durum, favori butonu ve detay yönlendirmesi içerir. */
@Composable
private fun ProvinceHeaderCard(
    card: CityWeatherUiModel?,
    provinceName: String,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (card != null) {
                TemperatureBadge(
                    text = card.temperatureText,
                    containerColor = temperatureColor(card.temperatureC),
                    size = 72.dp
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 3.dp
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = provinceName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (card != null) {
                    Text(
                        text = "${card.conditionEmoji} ${card.conditionLabel.asString()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = stringResource(R.string.district_view_all),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            FavoriteToggleButton(
                isFavorite = card?.isFavorite ?: false,
                onClick = onFavoriteClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DistrictListScreenPreview() {
    HavaTheme {
        DistrictListScreen(
            provinceName = "İstanbul",
            provinceCard = CityWeatherUiModel(
                cityId = 745044,
                title = "İstanbul",
                subtitle = "İstanbul, Türkiye",
                temperatureText = "19°",
                temperatureC = 19.0,
                conditionEmoji = "⛅",
                conditionLabel = UiText.Dynamic("Parçalı bulutlu"),
                isFavorite = true
            ),
            districtUiState = UiState.Success(
                listOf(
                    CityWeatherUiModel(
                        cityId = 903401,
                        title = "Kadıköy",
                        subtitle = "İstanbul, Türkiye",
                        temperatureText = "20°",
                        temperatureC = 20.0,
                        conditionEmoji = "☀️",
                        conditionLabel = UiText.Dynamic("Açık"),
                        isFavorite = false
                    ),
                    CityWeatherUiModel(
                        cityId = 903402,
                        title = "Beşiktaş",
                        subtitle = "İstanbul, Türkiye",
                        temperatureText = "19°",
                        temperatureC = 19.0,
                        conditionEmoji = "⛅",
                        conditionLabel = UiText.Dynamic("Parçalı bulutlu"),
                        isFavorite = true
                    )
                )
            ),
            isRefreshing = false,
            onBack = {},
            onProvinceClick = {},
            onProvinceFavoriteClick = {},
            onDistrictClick = {},
            onDistrictFavoriteClick = {},
            onRetry = {},
            onRefresh = {}
        )
    }
}
