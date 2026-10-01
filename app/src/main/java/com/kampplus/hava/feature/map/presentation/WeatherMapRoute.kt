package com.kampplus.hava.feature.map.presentation

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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kampplus.hava.core.location.LocationHelper
import com.kampplus.hava.feature.weather.domain.model.City
import kotlinx.coroutines.launch

@Composable
fun WeatherMapRoute(onCityClick: (City) -> Unit, viewModel: WeatherMapViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationHelper = remember { LocationHelper(context) }
    var userCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    fun refreshLocation() {
        scope.launch {
            val exactCity = locationHelper.getExactLocationCity()
            userCoordinates = exactCity.coordinates.latitude to exactCity.coordinates.longitude
            viewModel.updateLiveUserLocation(exactCity)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        refreshLocation()
    }

    // Uygulamaya girerken ve haritaya geçerken konumu 1 kez çek (sürekli dinlemez, pil tasarrufu)
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            refreshLocation()
        }
    }

    WeatherMapScreen(
        uiState = uiState,
        selectedCity = selectedCity,
        userLocation = userCoordinates,
        onCitySelect = viewModel::selectCity,
        onCityNavigate = onCityClick,
        onToggleFavorite = viewModel::onToggleFavorite,
        onRetry = viewModel::onRetry,
        onLocationRefresh = {
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
        }
    )
}
