# Hava Durumu+ ☀️🌧️

> **Bu proje, T.C. Gençlik ve Spor Bakanlığı ile Turkcell iş birliğinde düzenlenen Kamp+ 2026 etkinliği kapsamında geliştirilmiştir.**

---

Modern Android mimarisi (**Clean Architecture & MVI/MVVM**), **Jetpack Compose (Material 3)**, **Kotlin Coroutines & Flow** ve **Room Database** kullanılarak sıfırdan hayata geçirilmiş gelişmiş hava durumu ve keşif uygulaması.

---

## 🚀 Özellikler

1. **81 İl ve Kapsamlı İlçe Kataloğu:**
   - Türkiye'nin 81 ilinin anlık hava durumu göstergeleri.
   - İle tıklandığında üstte genel il kartı, altta tüm ilçelerin tek tek canlı sıcaklık rozetleri ve hava durumu emojileri.
   - Hem iller hem de her bir ilçe için bağımsız **❤️ Favorilere Ekleme/Çıkarma** desteği.

2. **Detaylı 7 Günlük Tahmin & 24 Saatlik Grafik:**
   - Canvas tabanlı 24 saatlik etkileşimli sıcaklık ve yağış ihtimali eğrisi (`HourlyWeatherChart`).
   - Gün doğumu/batımı döngüsü takibi (`SunTrackerCard`) ve UV indeksi göstergesi.
   - Şiddetli hava durumu olayları için otomatik alarm paneli (`WeatherAlertBanner`).
   - Sosyal medya ve mesajlaşma uygulamalarıyla yerel hava durumu paylaşımı.

3. **Hava Kalitesi İndeksi (AQI):**
   - Open-Meteo Air Quality API entegrasyonu.
   - PM2.5, PM10, O₃ (Ozon), NO₂ ve UV İndeksi metrikleri; Avrupa AQI standartlarında renk kodlu gösterge çubuğu (`AirQualityCard`).

4. **İnteraktif Hava Durumu Haritası (Map View):**
   - OpenStreetMap tile motoru ile Türkiye koordinat projeksiyonu.
   - Canlı GPS konumunu harita üzerinde nabız animasyonuyla gösterme ve otomatik odaklanma.
   - Harita üzerindeki şehirlere dokunarak önizleme kartı açma ve tek tıkla detaylı tahmine geçiş.

5. **Akıllı Önbellek & Ağ Optimizasyonu (429 Koruması):**
   - OkHttp seviyesinde çalışan exponential backoff `RateLimitInterceptor`.
   - Şehir bazlı `ConcurrentHashMap` bellek içi akıllı önbellek (10 dk TTL) ile gereksiz ağ isteklerinin ve 429 Too Many Requests hatalarının önlenmesi.

6. **Optimize Edilmiş GPS & Konum Yaşam Döngüsü:**
   - Uygulama açılışında ve butonla istek üzerine (on-demand) çalışan batarya dostu konum tespiti.
   - Sürekli arka plan sorgulamaları kaldırılarak pil tasarrufu sağlandı.

7. **Ana Ekran Widget'ı (AppWidgetProvider):**
   - Canlı sıcaklık, ilçe/şehir adı ve hava durumu ikonu içeren modern widget (`WeatherWidgetProvider`).

8. **Kişiselleştirilebilir Ayarlar & Dinamik Tema:**
   - Açık Tema, Koyu Tema ve **Hava Durumuna Göre Dinamik Renkler**.
   - Günlük hava durumu bildirimi saat ve konum ayarı (WorkManager).
   - Sıcaklık birimi seçimi (°C / °F).

---

## 🏗️ Mimari & Katmanlar

Tek `:app` modüllü **feature-first** Clean Architecture prensiplerine tam uyum:

```
com.kampplus.hava
├── core/
│   ├── common/        -> AppResult, AppError, ErrorMapper, Dispatcher'lar
│   ├── database/      -> HavaDatabase (Room), DatabaseModule
│   ├── location/      -> LocationHelper (GPS, Geocoder, IP Fallback)
│   ├── network/       -> RateLimitInterceptor, Retrofit modülleri
│   ├── notification/  -> WeatherNotificationManager, WeatherNotificationWorker, Scheduler
│   ├── ui/            -> HavaTheme, TemperatureBadge, Components, UiState
│   └── navigation/    -> Destinations (Type-Safe), HavaNavHost, BottomBar
└── feature/
    ├── weather/       -> Şehirler, 81 ilin ilçeleri, detay tahmini, hava kalitesi
    ├── map/           -> İnteraktif OpenStreetMap haritası ve pin yönetimi
    ├── favorites/     -> Favori şehir/ilçeler Room veritabanı senkronizasyonu
    ├── settings/      -> Ayarlar, dinamik tema, bildirim zamanlaması
    └── widget/        -> Android Ana Ekran Widget'ı ve konfigürasyonu
```

---

## 🛠️ Çalıştırma, Derleme & Test

```bash
# Release APK üretimi (İmzalı ve kuruluma hazır: app/build/outputs/apk/release/app-release.apk)
./gradlew assembleRelease

# Debug APK üretimi
./gradlew assembleDebug

# Tüm Unit testler ve Clean Architecture mimari katman testleri (49 test)
./gradlew testDebugUnitTest
```

---

## 📦 Sürüm Notları (Changelog)

### [v1.0.0] - 2026-10-01
- **Eklenen:** 81 il ve tüm ilçeleri kapsayan ilçe kataloğu ve bağımsız favori ekleme sistemi.
- **Eklenen:** Hava Durumu+ marka ismi ve Android Adaptive Icon logo tasarımı.
- **Eklenen:** Open-Meteo 429 Too Many Requests önleyici `RateLimitInterceptor` ve şehir bazlı `weatherCache`.
- **Eklenen:** Optimize edilmiş GPS ve canlı harita konumu.
- **Geliştirilen:** Canvas 24 saatlik grafik, hava kalitesi (AQI) paneli ve ana ekran widget'ı.
