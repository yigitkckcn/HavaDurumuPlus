package com.kampplus.hava.feature.weather.presentation.model

import com.kampplus.hava.R
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.feature.settings.domain.model.TemperatureUnit
import com.kampplus.hava.feature.weather.domain.model.AirQuality
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.model.WeatherCode
import com.kampplus.hava.feature.weather.domain.policy.WeatherAlertDetector
import com.kampplus.hava.feature.weather.domain.policy.WeatherConditionClassifier
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

/** Domain modellerini ekranların ihtiyaç duyduğu biçimlendirilmiş modellere çevirir. */
class WeatherUiMapper @Inject constructor(
    private val conditionClassifier: WeatherConditionClassifier,
    private val conditionUiRegistry: WeatherConditionUiRegistry,
    private val alertDetector: WeatherAlertDetector
) {
    fun toListItem(
        cityWeather: CityWeather,
        isFavorite: Boolean = false,
        unit: TemperatureUnit = TemperatureUnit.CELSIUS
    ): CityWeatherUiModel = with(cityWeather) {
        val conditionUi = conditionUi(current.weatherCode)
        CityWeatherUiModel(
            cityId = city.id,
            title = city.name,
            subtitle = subtitle(city),
            temperatureText = degrees(current.temperatureC, unit),
            temperatureC = current.temperatureC,
            conditionEmoji = conditionUi.emoji,
            conditionLabel = conditionUi.label,
            isFavorite = isFavorite
        )
    }

    fun toForecast(
        city: City,
        forecast: Forecast,
        airQuality: AirQuality? = null,
        isFavorite: Boolean = false,
        unit: TemperatureUnit = TemperatureUnit.CELSIUS
    ): ForecastUiModel = with(forecast) {
        val condition = conditionClassifier.classify(current.weatherCode)
        val conditionUi = conditionUiRegistry.resolve(condition)
        val currentHour = current.observedAt.truncatedTo(ChronoUnit.HOURS)
        val filteredHourly = hourly.filter { !it.time.isBefore(currentHour) }.take(HOURLY_COUNT)
        val alerts = alertDetector.detectAlerts(current)

        ForecastUiModel(
            cityId = city.id,
            cityName = city.name,
            subtitle = subtitle(city),
            temperatureText = degrees(current.temperatureC, unit),
            temperatureC = current.temperatureC,
            conditionEmoji = conditionUi.emoji,
            conditionLabel = conditionUi.label,
            weatherCondition = condition,
            feelsLikeText = current.apparentTemperatureC?.let { degrees(it, unit) },
            humidityText = current.humidityPercent?.let { "%$it" },
            windText = current.windSpeedKmh?.let { "${it.roundToInt()} km/sa" },
            hourly = filteredHourly.map { hour ->
                HourlyUiModel(
                    timeText = hour.time.format(HOUR_FORMAT),
                    emoji = conditionUi(hour.weatherCode).emoji,
                    temperatureText = degrees(hour.temperatureC, unit),
                    precipitationText = percent(hour.precipitationProbability)
                )
            },
            hourlyChartPoints = filteredHourly.map { hour ->
                val displayTemp = if (unit == TemperatureUnit.FAHRENHEIT) {
                    (hour.temperatureC * 9.0 / 5.0) + 32.0
                } else {
                    hour.temperatureC
                }
                HourlyChartPointUiModel(
                    timeText = hour.time.format(HOUR_FORMAT),
                    temperatureC = displayTemp,
                    emoji = conditionUi(hour.weatherCode).emoji,
                    precipitationProbability = hour.precipitationProbability
                )
            },
            daily = daily.mapIndexed { index, day ->
                DailyUiModel(
                    dayLabel = if (index == 0) {
                        UiText.Resource(R.string.today)
                    } else {
                        UiText.Dynamic(day.date.format(DAY_FORMAT).replaceFirstChar(Char::titlecase))
                    },
                    emoji = conditionUi(day.weatherCode).emoji,
                    minText = degrees(day.minTemperatureC, unit),
                    maxText = degrees(day.maxTemperatureC, unit),
                    precipitationText = percent(day.precipitationProbability)
                )
            },
            airQuality = airQuality?.let(::toAirQuality),
            alerts = alerts,
            sunriseTimeText = sunrise?.format(HOUR_FORMAT),
            sunsetTimeText = sunset?.format(HOUR_FORMAT),
            uvIndex = uvIndexMax ?: airQuality?.uvIndex,
            isFavorite = isFavorite
        )
    }

    fun toAirQuality(airQuality: AirQuality): AirQualityUiModel = AirQualityUiModel(
        aqiValue = airQuality.europeanAqi,
        aqiLabel = airQuality.level.toLabel(),
        aqiColor = airQuality.level.toColor(),
        pm25Text = airQuality.pm25?.let { "%.1f µg/m³".format(it) },
        pm10Text = airQuality.pm10?.let { "%.1f µg/m³".format(it) },
        ozoneText = airQuality.ozone?.let { "%.1f µg/m³".format(it) },
        no2Text = airQuality.nitrogenDioxide?.let { "%.1f µg/m³".format(it) },
        uvIndexText = airQuality.uvIndex?.let { "%.1f".format(it) }
    )

    private fun conditionUi(code: WeatherCode): WeatherConditionUi = conditionUiRegistry.resolve(conditionClassifier.classify(code))

    private fun subtitle(city: City) = listOfNotNull(city.region, city.country).distinct().joinToString(", ")

    private fun degrees(celsius: Double, unit: TemperatureUnit = TemperatureUnit.CELSIUS): String {
        val temp = if (unit == TemperatureUnit.FAHRENHEIT) {
            (celsius * 9.0 / 5.0) + 32.0
        } else {
            celsius
        }
        return "${temp.roundToInt()}°"
    }

    private fun percent(value: Int?) = value?.takeIf { it > 0 }?.let { "%$it" }

    private companion object {
        const val HOURLY_COUNT = 24
        val HOUR_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val DAY_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag("tr"))
    }
}
