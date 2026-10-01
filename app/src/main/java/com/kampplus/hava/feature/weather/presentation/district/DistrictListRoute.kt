package com.kampplus.hava.feature.weather.presentation.district

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kampplus.hava.feature.weather.domain.model.City

@Composable
fun DistrictListRoute(
    onBack: () -> Unit,
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DistrictListViewModel = hiltViewModel()
) {
    val provinceCard by viewModel.provinceCardState.collectAsStateWithLifecycle()
    val districtUiState by viewModel.districtUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    DistrictListScreen(
        provinceName = viewModel.province.name,
        provinceCard = provinceCard,
        districtUiState = districtUiState,
        isRefreshing = isRefreshing,
        onBack = onBack,
        onProvinceClick = { onCityClick(viewModel.province) },
        onProvinceFavoriteClick = { viewModel.onToggleFavorite(viewModel.province.id) },
        onDistrictClick = { cityId -> viewModel.findDistrict(cityId)?.let(onCityClick) },
        onDistrictFavoriteClick = viewModel::onToggleFavorite,
        onRetry = viewModel::onRetry,
        onRefresh = viewModel::onRefresh,
        modifier = modifier
    )
}
