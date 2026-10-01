# Mimari ve Tasarım Dokümantasyonu

Tek `:app` modülü, **feature-first** paketleme; her feature kendi `data / domain / presentation` katmanına sahiptir.
Bağımlılık yönü: `presentation → domain ← data`. Domain saf Kotlin'dir; `LayerDependencyTest` domain'de Android/Retrofit/Room importunu, presentation'da data importunu yasaklar.

## Paket Haritası

```
com.kampplus.hava
├── HavaApplication.kt / MainActivity.kt / HavaApp.kt
├── core/
│   ├── common/        AppResult, AppError, ErrorMapper (+ Default), dispatcher qualifier'ları, CommonModule
│   ├── network/       NetworkModule (OkHttp, Json, Forecast, Geocoding, AirQuality Retrofit'leri), NetworkErrorMapper
│   ├── database/      HavaDatabase, DatabaseModule
│   ├── notification/  WeatherNotificationManager, WeatherNotificationWorker, WeatherNotificationScheduler
│   ├── ui/            theme (HavaTheme, WeatherThemePalette), component, UiState, UiText
│   └── navigation/    Destinations (List, Map, Favorites, Settings, Forecast), TopLevelDestination, BottomBar, HavaNavHost
└── feature/
    ├── weather/
    │   ├── domain/    model (City, Coordinates, WeatherCode, AirQuality, CurrentWeather, Forecast…),
    │   │              repository (WeatherRepository, CityRepository, AirQualityRepository), usecase, policy
    │   ├── data/      local (TurkishCityCatalog), remote (OpenMeteo, AirQuality api, dto), mapper, repository, di
    │   └── presentation/ list, detail (AirQualityCard, Hourly, Daily), model, di
    ├── map/
    │   └── presentation/ WeatherMapScreen, WeatherMapViewModel, WeatherMapRoute
    ├── favorites/
    │   ├── domain/    FavoriteCity, FavoriteCityRepository, usecase'ler
    │   ├── data/      RoomFavoriteCityDataSource, dao, entity, repository, di
    │   └── presentation/ FavoritesScreen, FavoritesViewModel, FavoritesRoute
    ├── settings/
    │   ├── domain/    AppSettings, ThemeMode, TemperatureUnit, SettingsRepository
    │   ├── data/      SettingsRepositoryImpl, di
    │   └── presentation/ SettingsScreen, SettingsViewModel, SettingsRoute
    └── widget/
        └── WeatherWidgetProvider, WeatherWidgetUpdater
```

## Eklenen Servisler ve Yetenekler

1. **Air Quality API Entegrasyonu**: Open-Meteo Air Quality endpoint'i ile gerçek zamanlı AQI, PM2.5, PM10, UV ve Ozon verileri.
2. **Interactive Map**: Canvas ve koordinat izdüşümü ile Türkiye geneli sıcaklık rozetleri ve seçim kartı.
3. **Dynamic Weather Theming**: Hava durumunun koşuluna (açık, yağmurlu, karlı, fırtınalı) göre otomatik değişebilen renk paletleri.
4. **Periodic Notifications**: WorkManager ile arka planda çalışan ve hava durumunu periyodik bildiren mekanizma.
5. **AppWidget**: Android ana ekranına eklenebilir, favori şehrin anlık durumunu gösteren widget.
