package com.kampplus.hava.feature.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.kampplus.hava.MainActivity
import com.kampplus.hava.R
import com.kampplus.hava.core.location.LocationHelper
import com.kampplus.hava.feature.weather.data.remote.api.OpenMeteoForecastApi
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition
import com.kampplus.hava.feature.weather.domain.policy.WmoWeatherConditionClassifier
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class WeatherWidgetProvider : AppWidgetProvider() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateSingleWidget(context, appWidgetManager, id)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds.forEach { WidgetCityPrefs.delete(context, it) }
    }

    companion object {

        fun updateSingleWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_weather)

            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                try {
                    val city = WidgetCityPrefs.getCity(context, appWidgetId) ?: LocationHelper(context).resolveCurrentCity()
                    views.setTextViewText(R.id.widget_city_name, city.name)
                    appWidgetManager.updateAppWidget(appWidgetId, views)

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
                    val response = api.getForecast(
                        latitude = city.coordinates.latitude.toString(),
                        longitude = city.coordinates.longitude.toString(),
                        current = "temperature_2m,weather_code,apparent_temperature,relative_humidity_2m,wind_speed_10m"
                    )

                    val current = response.current ?: return@launch
                    val classifier = WmoWeatherConditionClassifier()
                    val condition = classifier.classify(WeatherCode(current.weatherCode))

                    val emoji = conditionToEmoji(condition)
                    val label = conditionToLabel(condition)
                    val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                    val subMetrics = listOfNotNull<String>(
                        current.apparentTemperature?.let { "Hissedilen ${it.roundToInt()}°" },
                        current.relativeHumidity?.let { "Nem %$it" },
                        current.windSpeed?.let { "${it.roundToInt()} km/s" }
                    ).joinToString(" • ")

                    val updatedViews = RemoteViews(context.packageName, R.layout.widget_weather)
                    updatedViews.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
                    updatedViews.setTextViewText(R.id.widget_city_name, city.name)
                    updatedViews.setTextViewText(R.id.widget_temperature, "${current.temperature.roundToInt()}°")
                    updatedViews.setTextViewText(R.id.widget_emoji, emoji)
                    updatedViews.setTextViewText(R.id.widget_condition_label, label)
                    updatedViews.setTextViewText(R.id.widget_sub_metrics, subMetrics)
                    updatedViews.setTextViewText(R.id.widget_updated_time, "Güncelleme: $time")
                    appWidgetManager.updateAppWidget(appWidgetId, updatedViews)
                } catch (_: Exception) {
                    // Ignore on network failure
                }
            }
        }

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, WeatherWidgetProvider::class.java))
            ids.forEach { updateSingleWidget(context, manager, it) }
        }

        private fun conditionToEmoji(condition: WeatherCondition) = when (condition) {
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

        private fun conditionToLabel(condition: WeatherCondition) = when (condition) {
            WeatherCondition.Clear -> "Açık"
            WeatherCondition.MainlyClear -> "Az bulutlu"
            WeatherCondition.PartlyCloudy -> "Parçalı bulutlu"
            WeatherCondition.Overcast -> "Kapalı"
            WeatherCondition.Fog -> "Sisli"
            WeatherCondition.Drizzle -> "Çisenti"
            WeatherCondition.Rain, WeatherCondition.RainShowers -> "Yağmurlu"
            WeatherCondition.Snow, WeatherCondition.SnowShowers -> "Karlı"
            WeatherCondition.Thunderstorm -> "Gök gürültülü fırtına"
            else -> "Bilinmiyor"
        }
    }
}
