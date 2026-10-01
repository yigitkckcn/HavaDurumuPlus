package com.kampplus.hava.feature.weather.data.repository

import com.kampplus.hava.core.common.dispatcher.IoDispatcher
import com.kampplus.hava.core.common.error.ErrorMapper
import com.kampplus.hava.core.common.result.AppResult
import com.kampplus.hava.core.common.result.runCatchingApp
import com.kampplus.hava.feature.weather.data.remote.AirQualityRemoteDataSource
import com.kampplus.hava.feature.weather.domain.model.AirQuality
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import com.kampplus.hava.feature.weather.domain.repository.AirQualityRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AirQualityRepositoryImpl @Inject constructor(
    private val remoteDataSource: AirQualityRemoteDataSource,
    private val errorMapper: ErrorMapper,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AirQualityRepository {

    override suspend fun getAirQuality(coordinates: Coordinates): AppResult<AirQuality> = withContext(ioDispatcher) {
        errorMapper.runCatchingApp { remoteDataSource.getAirQuality(coordinates) }
    }
}
