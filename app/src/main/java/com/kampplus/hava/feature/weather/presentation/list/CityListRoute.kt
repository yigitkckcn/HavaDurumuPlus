package com.kampplus.hava.feature.weather.presentation.list

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kampplus.hava.core.location.LocationHelper
import com.kampplus.hava.feature.weather.domain.model.City
import kotlinx.coroutines.launch

/** ViewModel'i ekrana bağlayan katman. Ekranın kendisi ([CityListScreen]) stateless'tır. */
@Composable
fun CityListRoute(onCityClick: (City) -> Unit, modifier: Modifier = Modifier, viewModel: CityListViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val exactLocationWeather by viewModel.exactLocationWeather.collectAsStateWithLifecycle()
    val locationHelper = remember { LocationHelper(context) }

    var isLoadingLocation by remember { mutableStateOf(false) }

    fun refreshLocation() {
        scope.launch {
            isLoadingLocation = true
            val exactCity = locationHelper.getExactLocationCity()
            viewModel.updateExactLocation(exactCity)
            isLoadingLocation = false
        }
    }

    // Uygulama açılışında gerçek GPS konumunun anlık havasını çek
    LaunchedEffect(Unit) {
        refreshLocation()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        refreshLocation()
    }

    CityListScreen(
        uiState = uiState,
        currentLocationWeather = exactLocationWeather,
        isLoadingLocation = isLoadingLocation,
        onQueryChange = viewModel::onQueryChange,
        onCityClick = { cityId -> viewModel.findCity(cityId)?.let(onCityClick) },
        onFavoriteClick = viewModel::onToggleFavorite,
        onLocationClick = {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                refreshLocation()
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        },
        onRetry = viewModel::onRetry,
        onRefresh = {
            refreshLocation()
            viewModel.onRefresh()
        },
        modifier = modifier
    )
}
