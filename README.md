# Hava Durumu — Gelişmiş Keşif ve Takip Uygulaması

`weather-app` referans projesi temel alınarak geliştirilmiş, Clean Architecture prensiplerine uygun, modern **Jetpack Compose (Material 3)** hava durumu uygulaması.

---

## 🌟 Eklenen Yeni Özellikler

1. **Hava Durumu Bildirimleri (WorkManager & NotificationManager)**
   - Periyodik arka plan işçisi (`WeatherNotificationWorker`) ile günlük hava durumu güncellemeleri.
   - Anlık test bildirimi tetikleme ve Android 13+ bildirim izni yönetimi.
2. **Ana Ekran Hava Durumu Widget'ı (AppWidgetProvider)**
   - Ana ekrana eklenebilir modern widget (`widget_weather.xml` & `WeatherWidgetProvider`).
   - Şehir adı, anlık sıcaklık, hava koşulu ikonu ve son güncelleme saatini gösterir.
3. **Hava Kalitesi İndeksi (Air Quality - AQI)**
   - Open-Meteo Air Quality API entegrasyonu.
   - PM2.5, PM10, O₃ (Ozon), NO₂ ve UV İndeksi metrikleri; renk kodlu AQI gösterge çubuğu (`AirQualityCard`).
4. **İnteraktif Hava Durumu Haritası (Map View)**
   - Türkiye koordinat projeksiyonu ile oluşturulmuş interaktif harita (`WeatherMapScreen`).
   - Şehirlerin sıcaklık ve hava ikonu rozetleri, dokunarak önizleme kartı ve tek dokunuşla detay tahminine geçiş.
5. **Karanlık/Aydınlık Tema & Hava Durumuna Göre Dinamik Renkler**
   - Sistem Varsayılanı, Açık Tema, Koyu Tema ve **Hava Durumuna Göre Dinamik Tema** desteği.
   - Güneşli/Açık havalarda sıcak amber/altın, yağmurlu havalarda mavi/gri, karlı havalarda buz mavisi, fırtınalı havalarda mor/indigo tonları.
6. **Ayarlar Ekranı (Settings Screen)**
   - Tema modu seçimi, bildirim açma/kapama, sıcaklık birimi (°C / °F), widget bilgisi ve uygulama detayları.

---

## 🏗️ Mimari & Katmanlar

Tek `:app` modüllü **feature-first** Clean Architecture:

```
com.kampplus.hava
├── core/
│   ├── common/        -> AppResult, AppError, ErrorMapper, Dispatcher'lar
│   ├── database/      -> HavaDatabase (Room), DatabaseModule
│   ├── network/       -> ForecastRetrofit, GeocodingRetrofit, AirQualityRetrofit, NetworkModule
│   ├── notification/  -> WeatherNotificationManager, WeatherNotificationWorker, Scheduler
│   ├── ui/            -> HavaTheme, WeatherThemePalette, UiState, UiText, Components
│   └── navigation/    -> Destinations (List, Map, Favorites, Settings, Forecast), TopLevelDestination, HavaNavHost
└── feature/
    ├── weather/       -> Şehir listesi, detay tahmini, hava kalitesi (data/domain/presentation)
    ├── map/           -> İnteraktif hava durumu haritası (presentation)
    ├── favorites/     -> Favori şehirler ve Room veritabanı (data/domain/presentation)
    ├── settings/      -> Ayarlar, tema ve bildirim yönetimi (data/domain/presentation)
    └── widget/        -> Android Ana Ekran Widget'ı (WeatherWidgetProvider)
```

---

## 🚀 Çalıştırma & Test

```bash
# Debug APK üretimi
./gradlew assembleDebug

# Tüm Unit testler ve Mimari katman testleri
./gradlew testDebugUnitTest

# Kod stili denetimi ve otomatik düzeltme
./gradlew spotlessApply
```
