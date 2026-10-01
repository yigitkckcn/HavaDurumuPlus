package com.kampplus.hava.feature.map.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sinh
import kotlin.math.tan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

/** OSM tile koordinatı. */
data class TileCoord(val zoom: Int, val x: Int, val y: Int)

/** Tile yükleyici — aynı tile'ı tekrar tekrar indirmez (in-memory cache). */
@Stable
class TileCache {
    private val cache = mutableMapOf<TileCoord, Bitmap?>()
    private val loading = mutableSetOf<TileCoord>()
    private val client = OkHttpClient.Builder()
        .callTimeout(java.time.Duration.ofSeconds(4))
        .build()

    var version by mutableStateOf(0) // recompose trigger

    fun getTile(coord: TileCoord, scope: CoroutineScope): Bitmap? {
        if (cache.containsKey(coord)) return cache[coord]
        if (!loading.contains(coord)) {
            loading.add(coord)
            scope.launch(Dispatchers.IO) {
                val bitmap = fetchTile(coord)
                cache[coord] = bitmap
                loading.remove(coord)
                // Compose'u yeniden çiz
                kotlinx.coroutines.withContext(Dispatchers.Main) { version++ }
            }
        }
        return null
    }

    private fun fetchTile(coord: TileCoord): Bitmap? {
        val urls = listOf(
            "https://tile.openstreetmap.org/${coord.zoom}/${coord.x}/${coord.y}.png",
            "https://a.basemaps.cartocdn.com/rastertiles/voyager/${coord.zoom}/${coord.x}/${coord.y}.png",
            "https://a.tile.openstreetmap.fr/osmfr/${coord.zoom}/${coord.x}/${coord.y}.png"
        )

        for (url in urls) {
            try {
                val req = Request.Builder()
                    .url(url)
                    .header("User-Agent", "HavaApp/1.0 (Android)")
                    .build()
                client.newCall(req).execute().use { resp ->
                    if (resp.isSuccessful) {
                        val bytes = resp.body?.bytes() ?: return@use
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        if (bmp != null) return bmp
                    }
                }
            } catch (_: Exception) {
                // Sonraki mirror'ı dene
            }
        }
        return null
    }
}

// ────── Mercator projeksiyon yardımcıları ──────────────────────────────────

/** Koordinatlardan OSM tile x/y hesapla. */
fun latLonToTileXY(lat: Double, lon: Double, zoom: Int): Pair<Double, Double> {
    val n = 2.0.pow(zoom)
    val xTile = (lon + 180.0) / 360.0 * n
    val latRad = Math.toRadians(lat)
    val yTile = (1.0 - ln(tan(latRad) + 1.0 / kotlin.math.cos(latRad)) / PI) / 2.0 * n
    return xTile to yTile
}

/** Tile köşesinin lat/lon değeri. */
fun tileToLatLon(xTile: Int, yTile: Int, zoom: Int): Pair<Double, Double> {
    val n = 2.0.pow(zoom)
    val lon = xTile / n * 360.0 - 180.0
    val latRad = atan(sinh(PI * (1.0 - 2.0 * yTile / n)))
    val lat = Math.toDegrees(latRad)
    return lat to lon
}

/** Merkator'da lat/lon → ekran piksel koordinatı (tile başına 256 piksel). */
fun latLonToPixel(lat: Double, lon: Double, zoom: Int): IntOffset {
    val (tx, ty) = latLonToTileXY(lat, lon, zoom)
    return IntOffset((tx * 256).toInt(), (ty * 256).toInt())
}

@Composable
fun rememberTileCache(): TileCache = remember { TileCache() }
