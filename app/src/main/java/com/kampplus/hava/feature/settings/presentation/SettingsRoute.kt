package com.kampplus.hava.feature.settings.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsRoute(viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.messageEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    SettingsScreen(
        settings = settings,
        availableCities = viewModel.availableCities,
        onThemeModeChanged = viewModel::onThemeModeChanged,
        onNotificationsToggled = viewModel::onNotificationsToggled,
        onNotificationTimeChanged = viewModel::onNotificationTimeChanged,
        onNotificationCitySourceChanged = viewModel::onNotificationCitySourceChanged,
        onNotificationCustomCitySelected = viewModel::onNotificationCustomCitySelected,
        onTemperatureUnitChanged = viewModel::onTemperatureUnitChanged,
        onSendTestNotification = viewModel::onSendTestNotification,
        snackbarHostState = snackbarHostState
    )
}
