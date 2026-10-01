package com.kampplus.hava.core.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kampplus.hava.core.location.LocationHelper
import com.kampplus.hava.feature.weather.data.local.TurkishCityCatalog
import com.kampplus.hava.feature.weather.data.remote.api.OpenMeteoForecastApi
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CurrentWeather
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import com.kampplus.hava.feature.weather.domain.policy.WeatherAlertDetector
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition
import com.kampplus.hava.feature.weather.domain.policy.WmoWeatherConditionClassifier
import java.time.LocalDateTime
import kotlin.math.roundToInt
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class WeatherNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val json = Json {
                ignoreUnknownKeys = true
                explicitNulls = false
                coerceInputValues = true
            }
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/v1/")
                .client(OkHttpClient())
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()

            val api = retrofit.create(OpenMeteoForecastApi::class.java)

            // Kullanıcının ayarlardan seçtiği bildirim şehrini (veya GPS konumunu) belirle
            val prefs = context.getSharedPreferences("hava_app_settings", Context.MODE_PRIVATE)
            val citySource = prefs.getString("key_notification_city_source", "CURRENT_LOCATION")
            val customCityId = prefs.getLong("key_notification_custom_city_id", 745044L)

            val targetCity: City = if (citySource == "CUSTOM_CITY") {
                TurkishCityCatalog().cities().firstOrNull { it.id == customCityId }
                    ?: TurkishCityCatalog().cities().first()
            } else {
                LocationHelper(context).resolveCurrentCity()
            }

            val response = api.getForecast(
                latitude = targetCity.coordinates.latitude.toString(),
                longitude = targetCity.coordinates.longitude.toString(),
                current = "temperature_2m,weather_code,wind_speed_10m"
            )

            val current = response.current
            if (current != null) {
                val classifier = WmoWeatherConditionClassifier()
                val condition = classifier.classify(WeatherCode(current.weatherCode))
                val emoji = when (condition) {
                    WeatherCondition.Clear -> "☀️"
                    WeatherCondition.MainlyClear -> "🌤️"
                    WeatherCondition.PartlyCloudy -> "⛅"
                    WeatherCondition.Overcast -> "☁️"
                    WeatherCondition.Fog -> "🌫️"
                    WeatherCondition.Drizzle, WeatherCondition.Rain, WeatherCondition.RainShowers -> "🌧️"
                    WeatherCondition.Snow, WeatherCondition.SnowShowers -> "❄️"
                    WeatherCondition.Thunderstorm -> "⛈️"
                    else -> "🌡️"
                }
                val notificationManager = WeatherNotificationManager(context)

                // Şiddetli hava durumu kontrolü
                val domainCurrent = CurrentWeather(
                    temperatureC = current.temperature,
                    weatherCode = WeatherCode(current.weatherCode),
                    observedAt = LocalDateTime.now(),
                    windSpeedKmh = current.windSpeed
                )
                val alerts = WeatherAlertDetector().detectAlerts(domainCurrent)
                if (alerts.isNotEmpty()) {
                    val topAlert = alerts.first()
                    notificationManager.showAlertNotification(
                        title = "${topAlert.emoji} ${targetCity.name}: ${topAlert.title}",
                        content = topAlert.description
                    )
                } else {
                    val sourceHint = if (citySource == "CURRENT_LOCATION") "(Mevcut Konumun)" else ""
                    notificationManager.showWeatherNotification(
                        title = "${targetCity.name} Hava Durumu $sourceHint".trim(),
                        content = "${current.temperature.roundToInt()}°C $emoji Günlük hava durumu güncellendi."
                    )
                }
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "hava_daily_weather_worker"
    }
}
