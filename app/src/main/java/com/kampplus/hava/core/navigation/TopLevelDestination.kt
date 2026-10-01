package com.kampplus.hava.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.kampplus.hava.R
import kotlin.reflect.KClass

/** Alt navigasyon çubuğundaki sekmeler. Yeni sekme = yeni enum değeri + NavHost'ta bir `composable`. */
enum class TopLevelDestination(
    val route: Any,
    val routeClass: KClass<*>,
    val icon: ImageVector,
    @param:StringRes val labelRes: Int
) {
    List(ListDestination, ListDestination::class, Icons.AutoMirrored.Filled.List, R.string.nav_list),
    Map(MapDestination, MapDestination::class, Icons.Filled.Map, R.string.nav_map),
    Favorites(FavoritesDestination, FavoritesDestination::class, Icons.Filled.Favorite, R.string.nav_favorites),
    Settings(SettingsDestination, SettingsDestination::class, Icons.Filled.Settings, R.string.nav_settings)
}
