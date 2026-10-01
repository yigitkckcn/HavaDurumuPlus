package com.kampplus.hava.feature.weather.data.repository

import com.kampplus.hava.core.common.dispatcher.IoDispatcher
import com.kampplus.hava.core.common.error.ErrorMapper
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.common.result.runCatchingApp
import com.kampplus.hava.feature.weather.data.local.CityCatalog
import com.kampplus.hava.feature.weather.data.remote.WeatherRemoteDataSource
import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.CityWeather
import com.kampplus.hava.feature.weather.domain.model.Forecast
import com.kampplus.hava.feature.weather.domain.repository.WeatherRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl @Inject constructor(
    private val cityCatalog: CityCatalog,
    private val remoteDataSource: WeatherRemoteDataSource,
    private val errorMapper: ErrorMapper,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WeatherRepository {

    /** Şehir kimliğine göre önbellek: cityId -> (timestamp to CityWeather) */
    private val weatherCache = ConcurrentHashMap<Long, Pair<Long, CityWeather>>()

    override fun getCityWeathers(): Flow<AppResult<List<CityWeather>>> = getCurrentWeather(cityCatalog.cities())

    override fun getCurrentWeather(cities: List<City>): Flow<AppResult<List<CityWeather>>> = flow {
        if (cities.isEmpty()) {
            emit(AppResult.Success(emptyList()))
            return@flow
        }

        val now = System.currentTimeMillis()
        val missingCities = mutableListOf<City>()
        val resultMap = mutableMapOf<Long, CityWeather>()

        for (city in cities) {
            val cached = weatherCache[city.id]
            if (cached != null && (now - cached.first) < CACHE_TTL_MS) {
                resultMap[city.id] = cached.second
            } else {
                missingCities.add(city)
            }
        }

        if (missingCities.isEmpty()) {
            val sortedResult = cities.mapNotNull { resultMap[it.id] }
            emit(AppResult.Success(sortedResult))
            return@flow
        }

        val remoteResult = errorMapper.runCatchingApp { remoteDataSource.getCurrentWeather(missingCities) }
        when (remoteResult) {
            is AppResult.Success -> {
                val fetchTime = System.currentTimeMillis()
                for (cw in remoteResult.data) {
                    weatherCache[cw.city.id] = fetchTime to cw
                    resultMap[cw.city.id] = cw
                }
                val finalResult = cities.mapNotNull { resultMap[it.id] }
                emit(AppResult.Success(finalResult))
            }
            is AppResult.Failure -> {
                if (resultMap.size == cities.size) {
                    emit(AppResult.Success(cities.mapNotNull { resultMap[it.id] }))
                } else {
                    emit(remoteResult)
                }
            }
        }
    }.flowOn(ioDispatcher)

    override fun invalidateCache() {
        weatherCache.clear()
    }

    override suspend fun getForecast(city: City): AppResult<Forecast> = withContext(ioDispatcher) {
        errorMapper.runCatchingApp { remoteDataSource.getForecast(city) }
    }

    private companion object {
        /** 10 dakika boyunca aynı şehrin verisini önbellekten döndür, API'ye tekrar gitme. */
        const val CACHE_TTL_MS = 10 * 60 * 1_000L
    }
}
