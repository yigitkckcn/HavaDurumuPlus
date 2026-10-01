package com.kampplus.hava.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Looper
import androidx.core.content.ContextCompat
import com.kampplus.hava.feature.weather.data.local.TurkishCityCatalog
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

class LocationHelper(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("hava_location_prefs", Context.MODE_PRIVATE)
    }

    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    }

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(java.time.Duration.ofSeconds(4))
            .connectTimeout(java.time.Duration.ofSeconds(3))
            .readTimeout(java.time.Duration.ofSeconds(3))
            .build()
    }

    private val jsonParser by lazy {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    /** Canlı GPS konum akışı (Harita ekranında hareket ettikçe anlık günceller). */
    @SuppressLint("MissingPermission")
    fun observeLocation(): Flow<Location> = callbackFlow {
        val lm = locationManager ?: run {
            close()
            return@callbackFlow
        }

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                saveCoordinates(location.latitude, location.longitude)
                trySend(location)
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        // Önce bilinen son konumu hemen gönder
        getLastKnownHardwareLocation()?.let {
            saveCoordinates(it.latitude, it.longitude)
            trySend(it)
        }

        try {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 5f, listener, Looper.getMainLooper())
            }
            if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000L, 5f, listener, Looper.getMainLooper())
            }
        } catch (_: Exception) {
            // İzin verilmemiş olabilir
        }

        awaitClose {
            runCatching { lm.removeUpdates(listener) }
        }
    }

    /**
     * Cihazın BİREBİR ve GERÇEK GPS koordinatlarını ve bulunulan tam ilçeyi çözer.
     *
     * Sırasıyla dener:
     * 1. Canlı donanım GPS/Network konumu (4.5 sn timeout)
     * 2. Sistemdeki son bilinen donanım konumu (getLastKnownLocation)
     * 3. IP tabanlı konum servisi
     * 4. Önceden SharedPreferences'a kaydedilmiş son konum
     * 5. Son çare: İstanbul (Varsayılan Türkiye merkezi)
     */
    suspend fun getExactLocationCity(): City = withContext(Dispatchers.IO) {
        // 1) Canlı donanım GPS/A-GPS
        val hardwareLoc = getHardwareLocation()
        if (hardwareLoc != null) {
            saveCoordinates(hardwareLoc.latitude, hardwareLoc.longitude)
            val city = getCityFromCoordinates(hardwareLoc.latitude, hardwareLoc.longitude)
            saveLastDetectedCityId(city.id)
            return@withContext city
        }

        // 2) Donanımın bildiği son konum
        val lastKnown = getLastKnownHardwareLocation()
        if (lastKnown != null) {
            saveCoordinates(lastKnown.latitude, lastKnown.longitude)
            val city = getCityFromCoordinates(lastKnown.latitude, lastKnown.longitude)
            saveLastDetectedCityId(city.id)
            return@withContext city
        }

        // 3) IP tabanlı konum
        val ipLoc = fetchHttpsIpLocation()
        if (ipLoc != null) {
            saveCoordinates(ipLoc.first, ipLoc.second)
            val city = getCityFromCoordinates(ipLoc.first, ipLoc.second)
            saveLastDetectedCityId(city.id)
            return@withContext city
        }

        // 4) Önceden kaydedilmiş son koordinatlar
        val saved = getSavedCoordinates()
        if (saved != null) {
            val city = getCityFromCoordinates(saved.first, saved.second)
            return@withContext city
        }

        // 5) Son çare: İstanbul
        DEFAULT_FALLBACK_CITY
    }

    suspend fun resolveCurrentCity(): City = getExactLocationCity()

    fun getCityFromCoordinates(lat: Double, lon: Double): City {
        var district = "Mevcut Konumum"
        var province = "Türkiye"

        try {
            val geocoder = Geocoder(context, Locale("tr", "TR"))
            val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, lon, 1)
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(lat, lon, 1)
            }
            val address = addresses?.firstOrNull()
            if (address != null) {
                val dist = address.subAdminArea ?: address.locality ?: address.subLocality
                val prov = address.adminArea ?: address.subAdminArea
                if (dist != null) district = dist
                if (prov != null) province = prov
            } else {
                val closest = findClosestCity(lat, lon)
                district = closest.name
                province = closest.region ?: "Türkiye"
            }
        } catch (_: Exception) {
            val closest = findClosestCity(lat, lon)
            district = closest.name
            province = closest.region ?: "Türkiye"
        }

        return City(
            id = GPS_EXACT_CITY_ID,
            name = district,
            region = province,
            country = "Türkiye",
            coordinates = Coordinates(latitude = lat, longitude = lon)
        )
    }

    /** Donanım GPS / Network sağlayıcısından canlı konum alır. */
    @SuppressLint("MissingPermission")
    suspend fun getHardwareLocation(): Location? {
        val lm = locationManager ?: return null

        return withTimeoutOrNull(4_500L) {
            suspendCancellableCoroutine { cont ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val cancellationSignal = CancellationSignal()
                    cont.invokeOnCancellation { cancellationSignal.cancel() }
                    val executor = ContextCompat.getMainExecutor(context)
                    val provider = when {
                        lm.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                        else -> LocationManager.PASSIVE_PROVIDER
                    }
                    try {
                        lm.getCurrentLocation(provider, cancellationSignal, executor) { location ->
                            if (cont.isActive) cont.resume(location)
                        }
                    } catch (_: Exception) {
                        if (cont.isActive) cont.resume(null)
                    }
                } else {
                    val listener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            lm.removeUpdates(this)
                            if (cont.isActive) cont.resume(location)
                        }
                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    }
                    cont.invokeOnCancellation {
                        runCatching { lm.removeUpdates(listener) }
                    }
                    try {
                        val provider = when {
                            lm.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                            else -> LocationManager.PASSIVE_PROVIDER
                        }
                        lm.requestSingleUpdate(provider, listener, Looper.getMainLooper())
                    } catch (_: Exception) {
                        if (cont.isActive) cont.resume(null)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownHardwareLocation(): Location? {
        val lm = locationManager ?: return null
        var best: Location? = null
        for (provider in lm.allProviders) {
            val loc = runCatching { lm.getLastKnownLocation(provider) }.getOrNull()
            if (loc != null) {
                if (best == null || loc.time > best.time) {
                    best = loc
                }
            }
        }
        return best
    }

    private fun fetchHttpsIpLocation(): Pair<Double, Double>? {
        val endpoints = listOf(
            "http://ip-api.com/json",
            "https://ipwho.is/",
            "https://freeipapi.com/api/json",
            "https://ipapi.co/json/"
        )

        for (url in endpoints) {
            try {
                val req = Request.Builder()
                    .url(url)
                    .header("User-Agent", "HavaApp/1.0 (Android)")
                    .build()

                httpClient.newCall(req).execute().use { resp ->
                    if (resp.isSuccessful) {
                        val body = resp.body?.string() ?: return@use
                        val json = jsonParser.parseToJsonElement(body).jsonObject
                        val lat = (json["latitude"] ?: json["lat"])?.jsonPrimitive?.doubleOrNull
                        val lon = (json["longitude"] ?: json["lon"])?.jsonPrimitive?.doubleOrNull
                        if (lat != null && lon != null) {
                            return lat to lon
                        }
                    }
                }
            } catch (_: Exception) {
                // Sonraki endpoint'e geç
            }
        }
        return null
    }

    fun findClosestCity(lat: Double, lon: Double): City {
        val cities = TurkishCityCatalog().cities()
        return cities.minByOrNull { city ->
            haversineDistanceKm(lat, lon, city.coordinates.latitude, city.coordinates.longitude)
        } ?: DEFAULT_FALLBACK_CITY
    }

    fun getLastDetectedCityId(): Long? {
        val id = prefs.getLong("last_detected_city_id", -1L)
        return if (id != -1L) id else null
    }

    fun saveLastDetectedCityId(cityId: Long) {
        prefs.edit().putLong("last_detected_city_id", cityId).apply()
    }

    private fun saveCoordinates(lat: Double, lon: Double) {
        prefs.edit()
            .putFloat("saved_lat", lat.toFloat())
            .putFloat("saved_lon", lon.toFloat())
            .apply()
    }

    fun getSavedCoordinates(): Pair<Double, Double>? {
        if (!prefs.contains("saved_lat")) return null
        val lat = prefs.getFloat("saved_lat", 0f).toDouble()
        val lon = prefs.getFloat("saved_lon", 0f).toDouble()
        if (lat == 0.0 && lon == 0.0) return null
        return lat to lon
    }

    private fun haversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    companion object {
        const val GPS_EXACT_CITY_ID = 999999L

        val DEFAULT_FALLBACK_CITY = City(
            id = GPS_EXACT_CITY_ID,
            name = "İstanbul",
            region = "İstanbul",
            country = "Türkiye",
            coordinates = Coordinates(latitude = 41.0138, longitude = 28.9497)
        )
    }
}
