package com.kampplus.hava.feature.map.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.component.EmptyView
import com.kampplus.hava.core.ui.component.ErrorView
import com.kampplus.hava.core.ui.component.FavoriteToggleButton
import com.kampplus.hava.core.ui.component.LoadingView
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.presentation.model.temperatureColor
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

private const val TILE_SIZE_PX = 256
private const val DEFAULT_ZOOM = 6
private const val MIN_ZOOM = 4
private const val MAX_ZOOM = 12

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherMapScreen(
    uiState: UiState<List<MapMarkerUiModel>>,
    selectedCity: MapMarkerUiModel?,
    userLocation: Pair<Double, Double>? = null,
    onCitySelect: (MapMarkerUiModel) -> Unit,
    onCityNavigate: (City) -> Unit,
    onToggleFavorite: (City) -> Unit,
    onRetry: () -> Unit,
    onLocationRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.map_title)) },
                actions = {
                    IconButton(onClick = onLocationRefresh) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                UiState.Loading -> LoadingView()
                UiState.Empty -> EmptyView(
                    icon = Icons.Filled.Map,
                    title = stringResource(R.string.list_empty_title),
                    message = stringResource(R.string.list_empty_message)
                )
                is UiState.Error -> ErrorView(message = uiState.message.asString(), onRetry = onRetry)
                is UiState.Success -> {
                    OsmMapContent(
                        markers = uiState.data,
                        selectedCity = selectedCity,
                        userLocation = userLocation,
                        onCitySelect = onCitySelect,
                        onLocationRefresh = onLocationRefresh,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Seçili şehir kartı
                    selectedCity?.let { marker ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically { it } + fadeIn(),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            SelectedCityCard(
                                marker = marker,
                                onCityNavigate = { onCityNavigate(marker.city) },
                                onToggleFavorite = { onToggleFavorite(marker.city) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── OSM Harita İçeriği ────────────────────────────────────────────────────

@Composable
private fun OsmMapContent(
    markers: List<MapMarkerUiModel>,
    selectedCity: MapMarkerUiModel?,
    userLocation: Pair<Double, Double>?,
    onCitySelect: (MapMarkerUiModel) -> Unit,
    onLocationRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tileCache = rememberTileCache()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_anim")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    BoxWithConstraints(modifier = modifier) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        var zoom by remember { mutableStateOf(DEFAULT_ZOOM) }

        fun calculateCenterOffset(lat: Double, lon: Double, targetZoom: Int): Offset {
            val (tx, ty) = latLonToTileXY(lat, lon, targetZoom)
            return Offset(
                (tx * TILE_SIZE_PX - widthPx / 2).toFloat(),
                (ty * TILE_SIZE_PX - heightPx / 2).toFloat()
            )
        }

        var panOffset by remember(widthPx, heightPx) {
            val initialLat = userLocation?.first ?: 39.1
            val initialLon = userLocation?.second ?: 35.3
            mutableStateOf(calculateCenterOffset(initialLat, initialLon, DEFAULT_ZOOM))
        }

        // Konum ilk geldiğinde haritayı hemen kullanıcının canlı konumuna ortala
        var hasCenteredToUser by remember { mutableStateOf(false) }
        LaunchedEffect(userLocation) {
            if (userLocation != null && !hasCenteredToUser) {
                hasCenteredToUser = true
                zoom = 8
                panOffset = calculateCenterOffset(userLocation.first, userLocation.second, 8)
            }
        }

        var zoomAccumulator by remember { mutableFloatStateOf(0f) }

        @Suppress("UNUSED_EXPRESSION")
        tileCache.version

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFAAD3DF))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoomChange, _ ->
                        panOffset = Offset(panOffset.x - pan.x, panOffset.y - pan.y)

                        val delta = ln(zoomChange.toDouble()).toFloat() * 2.5f
                        zoomAccumulator += delta

                        if (zoomAccumulator >= 0.35f && zoom < MAX_ZOOM) {
                            val oldZoom = zoom
                            zoom += 1
                            zoomAccumulator = 0f
                            val scale = 2.0.pow(zoom - oldZoom).toFloat()
                            val cx = panOffset.x + widthPx / 2
                            val cy = panOffset.y + heightPx / 2
                            panOffset = Offset(cx * scale - widthPx / 2, cy * scale - heightPx / 2)
                        } else if (zoomAccumulator <= -0.35f && zoom > MIN_ZOOM) {
                            val oldZoom = zoom
                            zoom -= 1
                            zoomAccumulator = 0f
                            val scale = 2.0.pow(zoom - oldZoom).toFloat()
                            val cx = panOffset.x + widthPx / 2
                            val cy = panOffset.y + heightPx / 2
                            panOffset = Offset(cx * scale - widthPx / 2, cy * scale - heightPx / 2)
                        }
                    }
                }
        ) {
            // 1. Tile Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawTiles(zoom, panOffset, tileCache, scope)

                // Canlı GPS Konum Noktası
                if (userLocation != null) {
                    val (utx, uty) = latLonToTileXY(userLocation.first, userLocation.second, zoom)
                    val ux = (utx * TILE_SIZE_PX - panOffset.x).toFloat()
                    val uy = (uty * TILE_SIZE_PX - panOffset.y).toFloat()

                    drawCircle(
                        color = Color(0x440284C7),
                        radius = pulseRadius * density.density,
                        center = Offset(ux, uy)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 8.dp.toPx(),
                        center = Offset(ux, uy)
                    )
                    drawCircle(
                        color = Color(0xFF0284C7),
                        radius = 5.dp.toPx(),
                        center = Offset(ux, uy)
                    )
                }
            }

            // 2. Pin'ler — Layout ile piksel yerleşimi
            Layout(
                modifier = Modifier.fillMaxSize(),
                content = {
                    markers.forEach { marker ->
                        MapPin(
                            marker = marker,
                            isSelected = selectedCity?.city?.id == marker.city.id,
                            modifier = Modifier
                                .wrapContentSize()
                                .clickable { onCitySelect(marker) }
                        )
                    }
                }
            ) { measurables, constraints ->
                val w = constraints.maxWidth
                val h = constraints.maxHeight

                val placeables = measurables.mapIndexed { idx, m ->
                    m.measure(Constraints()) to markers[idx]
                }

                layout(w, h) {
                    placeables.forEach { (placeable, marker) ->
                        val (tx, ty) = latLonToTileXY(
                            marker.latitude,
                            marker.longitude,
                            zoom
                        )
                        val screenX = (tx * TILE_SIZE_PX - panOffset.x).roundToInt()
                        val screenY = (ty * TILE_SIZE_PX - panOffset.y).roundToInt()

                        placeable.placeRelative(
                            screenX - placeable.width / 2,
                            screenY - placeable.height
                        )
                    }
                }
            }

            // 3. Sağ Üst Kontroller
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        if (zoom < MAX_ZOOM) {
                            val oldZoom = zoom
                            zoom += 1
                            val scale = 2.0.pow(zoom - oldZoom).toFloat()
                            val cx = panOffset.x + widthPx / 2
                            val cy = panOffset.y + heightPx / 2
                            panOffset = Offset(cx * scale - widthPx / 2, cy * scale - heightPx / 2)
                        }
                    },
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.zoom_in), modifier = Modifier.size(20.dp))
                }

                FloatingActionButton(
                    onClick = {
                        if (zoom > MIN_ZOOM) {
                            val oldZoom = zoom
                            zoom -= 1
                            val scale = 2.0.pow(zoom - oldZoom).toFloat()
                            val cx = panOffset.x + widthPx / 2
                            val cy = panOffset.y + heightPx / 2
                            panOffset = Offset(cx * scale - widthPx / 2, cy * scale - heightPx / 2)
                        }
                    },
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = stringResource(R.string.zoom_out), modifier = Modifier.size(20.dp))
                }

                FloatingActionButton(
                    onClick = {
                        onLocationRefresh()
                        if (userLocation != null) {
                            zoom = 8
                            panOffset = calculateCenterOffset(userLocation.first, userLocation.second, 8)
                            markers.firstOrNull { it.isUserLocation }?.let { onCitySelect(it) }
                        } else {
                            zoom = DEFAULT_ZOOM
                            panOffset = calculateCenterOffset(39.1, 35.3, DEFAULT_ZOOM)
                        }
                    },
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(6.dp)
                ) {
                    Icon(
                        Icons.Filled.MyLocation,
                        contentDescription = stringResource(R.string.recenter_map),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawTiles(zoom: Int, panOffset: Offset, tileCache: TileCache, scope: kotlinx.coroutines.CoroutineScope) {
    val tileSize = TILE_SIZE_PX.toFloat()
    val maxTile = 2.0.pow(zoom).toInt()

    val startX = floor(panOffset.x / tileSize).toInt().coerceIn(0, maxTile - 1)
    val startY = floor(panOffset.y / tileSize).toInt().coerceIn(0, maxTile - 1)
    val endX = ceil((panOffset.x + size.width) / tileSize).toInt().coerceIn(0, maxTile - 1)
    val endY = ceil((panOffset.y + size.height) / tileSize).toInt().coerceIn(0, maxTile - 1)

    val minX = minOf(startX, endX)
    val maxX = maxOf(startX, endX)
    val minY = minOf(startY, endY)
    val maxY = maxOf(startY, endY)

    for (tx in minX..maxX) {
        for (ty in minY..maxY) {
            val coord = TileCoord(zoom, tx, ty)
            val bitmap = tileCache.getTile(coord, scope)
            val screenX = tx * tileSize - panOffset.x
            val screenY = ty * tileSize - panOffset.y

            if (bitmap != null) {
                drawImage(
                    image = bitmap.asImageBitmap(),
                    dstOffset = IntOffset(screenX.roundToInt(), screenY.roundToInt())
                )
            } else {
                drawRect(
                    color = Color(0xFFD4E9F7),
                    topLeft = Offset(screenX, screenY),
                    size = androidx.compose.ui.geometry.Size(tileSize, tileSize)
                )
                drawRect(
                    color = Color(0xFFB0C8D8),
                    topLeft = Offset(screenX, screenY),
                    size = androidx.compose.ui.geometry.Size(tileSize, tileSize),
                    style = Stroke(width = 0.5f)
                )
            }
        }
    }
}

// ─── Pin Bileşeni ──────────────────────────────────────────────────────────

@Composable
private fun MapPin(marker: MapMarkerUiModel, isSelected: Boolean, modifier: Modifier = Modifier) {
    val tempColor = temperatureColor(marker.weatherItem.temperatureC)
    val isUser = marker.isUserLocation

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = when {
                isUser -> Color(0xFF0284C7)
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surface
            },
            shadowElevation = if (isSelected || isUser) 8.dp else 3.dp,
            border = if (isSelected || isUser) null else androidx.compose.foundation.BorderStroke(1.dp, tempColor.copy(alpha = 0.8f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                if (isUser) {
                    Icon(
                        imageVector = Icons.Filled.NearMe,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
                Text(text = marker.weatherItem.conditionEmoji, fontSize = 11.sp)
                Text(
                    text = marker.weatherItem.temperatureText,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected || isUser) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        // Balon kuyruğu
        Box(
            modifier = Modifier
                .size(width = 2.dp, height = 4.dp)
                .background(
                    when {
                        isUser -> Color(0xFF0284C7)
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> tempColor.copy(alpha = 0.7f)
                    }
                )
        )
        // Şehir/İlçe adı
        Text(
            text = if (isUser) "📍 ${marker.city.name}" else marker.city.name,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            fontWeight = if (isSelected || isUser) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isUser) {
                Color(0xFF0284C7)
            } else if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 4.dp, vertical = 1.dp)
        )
    }
}

// ─── Seçili Şehir Kartı ────────────────────────────────────────────────────

@Composable
private fun SelectedCityCard(
    marker: MapMarkerUiModel,
    onCityNavigate: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = marker.isUserLocation

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUser) {
                MaterialTheme.colorScheme.primaryContainer.copy(
                    alpha = 0.5f
                )
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isUser) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0284C7).copy(alpha = 0.6f)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(temperatureColor(marker.weatherItem.temperatureC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = marker.weatherItem.conditionEmoji, fontSize = 22.sp)
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (isUser) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF0284C7)
                                ) {
                                    Text(
                                        text = "CANLI KONUM",
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            Text(
                                text = marker.city.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${marker.city.region ?: ""} • ${marker.weatherItem.conditionLabel.asString()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = marker.weatherItem.temperatureText,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = temperatureColor(marker.weatherItem.temperatureC)
                    )
                    if (!isUser) {
                        FavoriteToggleButton(
                            isFavorite = marker.weatherItem.isFavorite,
                            onClick = onToggleFavorite
                        )
                    }
                }
            }
            Button(
                onClick = onCityNavigate,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = stringResource(R.string.map_view_forecast))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
