package com.kampplus.hava.feature.weather.data.remote

import com.kampplus.hava.feature.weather.data.mapper.requireCurrent
import com.kampplus.hava.feature.weather.data.mapper.toDomain
import com.kampplus.hava.feature.weather.data.mapper.toForecast
import com.kampplus.hava.feature.weather.data.remote.api.OpenMeteoForecastApi
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Forecast
import javax.inject.Inject
import kotlinx.coroutines.delay

/** Open-Meteo'dan gerçek veri (CP4). */
class OpenMeteoWeatherRemoteDataSource @Inject constructor(
    private val api: OpenMeteoForecastApi
) : WeatherRemoteDataSource {

    /**
     * Şehirler 25'erli gruplar halinde sırayla sorgulanır; gruplar arası kısa bekleme
     * Open-Meteo rate-limit'ine (429) takılmayı önler.
     */
    override suspend fun getCurrentWeather(cities: List<City>): List<CityWeather> {
        if (cities.isEmpty()) return emptyList()
        if (cities.size == 1) {
            val city = cities.single()
            val response = api.getForecast(city.latitude(), city.longitude(), current = CURRENT_FIELDS)
            return listOf(CityWeather(city = city, current = response.requireCurrent().toDomain()))
        }

        val chunks = cities.chunked(CHUNK_SIZE)
        return buildList {
            chunks.forEachIndexed { index, chunk ->
                if (index > 0) delay(CHUNK_DELAY_MS)
                val responses = api.getForecasts(
                    latitudes = chunk.joinToString(",") { it.latitude() },
                    longitudes = chunk.joinToString(",") { it.longitude() },
                    current = CURRENT_FIELDS
                )
                chunk.zip(responses) { city, response ->
                    add(CityWeather(city = city, current = response.requireCurrent().toDomain()))
                }
            }
        }
    }

    override suspend fun getForecast(city: City): Forecast = api.getForecast(
        latitude = city.latitude(),
        longitude = city.longitude(),
        current = CURRENT_FIELDS,
        hourly = HOURLY_FIELDS,
        daily = DAILY_FIELDS,
        forecastDays = FORECAST_DAYS
    ).toForecast()

    private fun City.latitude() = coordinates.latitude.toString()

    private fun City.longitude() = coordinates.longitude.toString()

    private companion object {
        const val CHUNK_SIZE = 50
        const val CHUNK_DELAY_MS = 1_500L
        const val CURRENT_FIELDS =
            "temperature_2m,weather_code,apparent_temperature,relative_humidity_2m,wind_speed_10m,is_day"
        const val HOURLY_FIELDS =
            "temperature_2m,weather_code,precipitation_probability,uv_index"
        const val DAILY_FIELDS =
            "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,sunrise,sunset,uv_index_max"
        const val FORECAST_DAYS = 7
    }
}
