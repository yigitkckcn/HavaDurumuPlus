package com.kampplus.hava.feature.weather.domain.policy

import com.kampplus.hava.feature.weather.domain.model.AlertSeverity
import com.kampplus.hava.feature.weather.domain.model.CurrentWeather
import com.kampplus.hava.feature.weather.domain.model.WeatherAlert
import javax.inject.Inject

class WeatherAlertDetector @Inject constructor() {

    fun detectAlerts(current: CurrentWeather): List<WeatherAlert> {
        val alerts = mutableListOf<WeatherAlert>()
        val code = current.weatherCode.value

        // Fırtına / Yıldırım / Dolu uyarısı
        if (code in listOf(95, 96, 99)) {
            alerts.add(
                WeatherAlert(
                    title = "Şiddetli Gök Gürültülü Fırtına",
                    description = "Bölgede şimşek, dolu ve şiddetli sağanak riski var. Açık alanlarda bulunmaktan kaçının.",
                    severity = AlertSeverity.DANGER,
                    emoji = "⛈️"
                )
            )
        } else if (code in listOf(75, 86)) {
            alerts.add(
                WeatherAlert(
                    title = "Yoğun Kar Yağışı & Tipi",
                    description = "Görüş mesafesinde azalma ve yollarda şiddetli buzlanma riski. Zorunlu olmadıkça trafiğe çıkmayın.",
                    severity = AlertSeverity.WARNING,
                    emoji = "❄️"
                )
            )
        } else if (code in listOf(65, 82)) {
            alerts.add(
                WeatherAlert(
                    title = "Kuvvetli Sağanak & Sel Tehlikesi",
                    description = "Ani su birikintileri ve dere taşkınlarına karşı tedbirli olun.",
                    severity = AlertSeverity.WARNING,
                    emoji = "🌧️"
                )
            )
        } else if (code in listOf(45, 48)) {
            alerts.add(
                WeatherAlert(
                    title = "Yoğun Sis ve Düşük Görüş",
                    description = "Görüş mesafesi oldukça düşüktür. Sürüş hızınızı azaltın ve sis farlarınızı açın.",
                    severity = AlertSeverity.INFO,
                    emoji = "🌫️"
                )
            )
        }

        // Rüzgar uyarısı
        current.windSpeedKmh?.let { wind ->
            if (wind >= 60.0) {
                alerts.add(
                    WeatherAlert(
                        title = "Fırtına Şiddetinde Rüzgâr (${wind.toInt()} km/sa)",
                        description = "Çatı uçması, ağaç devrilmesi ve ulaşımda aksamalara karşı tedbirli olun.",
                        severity = AlertSeverity.DANGER,
                        emoji = "💨"
                    )
                )
            } else if (wind >= 45.0) {
                alerts.add(
                    WeatherAlert(
                        title = "Kuvvetli Rüzgâr (${wind.toInt()} km/sa)",
                        description = "Rüzgâr hamleleri etkili olabilir.",
                        severity = AlertSeverity.WARNING,
                        emoji = "🌬️"
                    )
                )
            }
        }

        // Sıcaklık aşırılıkları
        if (current.temperatureC >= 37.0) {
            alerts.add(
                WeatherAlert(
                    title = "Aşırı Sıcaklık Dalgası (${current.temperatureC.toInt()}°C)",
                    description = "Öğle saatlerinde doğrudan güneş ışığından kaçının, bol sıvı tüketin.",
                    severity = AlertSeverity.DANGER,
                    emoji = "🔥"
                )
            )
        } else if (current.temperatureC <= -7.0) {
            alerts.add(
                WeatherAlert(
                    title = "Şiddetli Don & Buzlanma (${current.temperatureC.toInt()}°C)",
                    description = "Yollarda ve kaldırımlarda gizli buzlanma tehlikesi bulunmaktadır.",
                    severity = AlertSeverity.WARNING,
                    emoji = "🧊"
                )
            )
        }

        return alerts
    }
}
