package com.kampplus.hava.feature.weather.data.di

import com.kampplus.hava.core.network.di.AirQualityRetrofit
import com.kampplus.hava.core.network.di.ForecastRetrofit
import com.kampplus.hava.core.network.di.GeocodingRetrofit
import com.kampplus.hava.feature.weather.data.local.CityCatalog
import com.kampplus.hava.feature.weather.data.local.TurkishCityCatalog
import com.kampplus.hava.feature.weather.data.remote.AirQualityRemoteDataSource
import com.kampplus.hava.feature.weather.data.remote.CityRemoteDataSource
import com.kampplus.hava.feature.weather.data.remote.OpenMeteoAirQualityRemoteDataSource
import com.kampplus.hava.feature.weather.data.remote.OpenMeteoCityRemoteDataSource
import com.kampplus.hava.feature.weather.data.remote.OpenMeteoWeatherRemoteDataSource
import com.kampplus.hava.feature.weather.data.remote.WeatherRemoteDataSource
import com.kampplus.hava.feature.weather.data.remote.api.OpenMeteoAirQualityApi
import com.kampplus.hava.feature.weather.data.remote.api.OpenMeteoForecastApi
import com.kampplus.hava.feature.weather.data.remote.api.OpenMeteoGeocodingApi
import com.kampplus.hava.feature.weather.data.repository.AirQualityRepositoryImpl
import com.kampplus.hava.feature.weather.data.repository.CityRepositoryImpl
import com.kampplus.hava.feature.weather.data.repository.DistrictRepositoryImpl
import com.kampplus.hava.feature.weather.data.repository.WeatherRepositoryImpl
import com.kampplus.hava.feature.weather.domain.policy.WeatherConditionClassifier
import com.kampplus.hava.feature.weather.domain.policy.WmoWeatherConditionClassifier
import com.kampplus.hava.feature.weather.domain.repository.AirQualityRepository
import com.kampplus.hava.feature.weather.domain.repository.CityRepository
import com.kampplus.hava.feature.weather.domain.repository.DistrictRepository
import com.kampplus.hava.feature.weather.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

/** Composition root: hangi implementasyonun kullanılacağına yalnızca burada karar verilir. */
@Module
@InstallIn(SingletonComponent::class)
abstract class WeatherDataModule {
    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    abstract fun bindWeatherRemoteDataSource(impl: OpenMeteoWeatherRemoteDataSource): WeatherRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindDistrictRepository(impl: DistrictRepositoryImpl): DistrictRepository

    @Binds
    @Singleton
    abstract fun bindCityRepository(impl: CityRepositoryImpl): CityRepository

    @Binds
    abstract fun bindCityRemoteDataSource(impl: OpenMeteoCityRemoteDataSource): CityRemoteDataSource

    @Binds
    abstract fun bindCityCatalog(impl: TurkishCityCatalog): CityCatalog

    @Binds
    abstract fun bindWeatherConditionClassifier(impl: WmoWeatherConditionClassifier): WeatherConditionClassifier

    @Binds
    @Singleton
    abstract fun bindAirQualityRepository(impl: AirQualityRepositoryImpl): AirQualityRepository

    @Binds
    abstract fun bindAirQualityRemoteDataSource(impl: OpenMeteoAirQualityRemoteDataSource): AirQualityRemoteDataSource

    companion object {
        @Provides
        @Singleton
        fun provideForecastApi(@ForecastRetrofit retrofit: Retrofit): OpenMeteoForecastApi =
            retrofit.create(OpenMeteoForecastApi::class.java)

        @Provides
        @Singleton
        fun provideGeocodingApi(@GeocodingRetrofit retrofit: Retrofit): OpenMeteoGeocodingApi =
            retrofit.create(OpenMeteoGeocodingApi::class.java)

        @Provides
        @Singleton
        fun provideAirQualityApi(@AirQualityRetrofit retrofit: Retrofit): OpenMeteoAirQualityApi =
            retrofit.create(OpenMeteoAirQualityApi::class.java)
    }
}
