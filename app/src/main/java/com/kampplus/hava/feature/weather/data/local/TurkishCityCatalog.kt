package com.kampplus.hava.feature.weather.data.local

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import javax.inject.Inject

/**
 * Türkiye'nin 81 ilinin tamamı. Kimlikler ve koordinatlar Open-Meteo geocoding
 * sonuçlarıyla ve Türkiye MERNİS/il plaka kodlarıyla uyumludur.
 */
class TurkishCityCatalog @Inject constructor() : CityCatalog {
    override fun cities(): List<City> = CITIES

    private companion object {
        val CITIES = listOf(
            city(325363, "Adana", "Akdeniz", 36.9862, 35.3253),
            city(325330, "Adıyaman", "Güneydoğu Anadolu", 37.7644, 38.2763),
            city(324907, "Afyonkarahisar", "Ege", 38.7569, 30.5387),
            city(324707, "Ağrı", "Doğu Anadolu", 39.7191, 43.0513),
            city(324083, "Amasya", "Karadeniz", 40.6534, 35.8331),
            city(323786, "Ankara", "İç Anadolu", 39.9199, 32.8543),
            city(323777, "Antalya", "Akdeniz", 36.9081, 30.6956),
            city(323607, "Artvin", "Karadeniz", 41.1828, 41.8183),
            city(323204, "Aydın", "Ege", 37.8481, 27.8453),
            city(322648, "Balıkesir", "Marmara", 39.6484, 27.8826),
            city(321703, "Bilecik", "Marmara", 40.1426, 29.9793),
            city(321528, "Bingöl", "Doğu Anadolu", 38.8847, 40.4939),
            city(321453, "Bitlis", "Doğu Anadolu", 38.4006, 42.1095),
            city(321287, "Bolu", "Karadeniz", 40.7358, 31.6061),
            city(320698, "Burdur", "Akdeniz", 37.7203, 30.2908),
            city(750269, "Bursa", "Marmara", 40.1956, 29.0601),
            city(319488, "Çanakkale", "Marmara", 40.1553, 26.4142),
            city(319342, "Çankırı", "İç Anadolu", 40.6013, 33.6134),
            city(318694, "Çorum", "Karadeniz", 40.5506, 34.9556),
            city(317109, "Denizli", "Ege", 37.7742, 29.0875),
            city(316541, "Diyarbakır", "Güneydoğu Anadolu", 37.9136, 40.2172),
            city(315918, "Edirne", "Marmara", 41.6772, 26.5557),
            city(315809, "Elazığ", "Doğu Anadolu", 38.6810, 39.2264),
            city(315498, "Erzincan", "Doğu Anadolu", 39.7500, 39.5000),
            city(315368, "Erzurum", "Doğu Anadolu", 39.9086, 41.2769),
            city(315202, "Eskişehir", "İç Anadolu", 39.7767, 30.5206),
            city(314830, "Gaziantep", "Güneydoğu Anadolu", 37.0594, 37.3825),
            city(314144, "Giresun", "Karadeniz", 40.9128, 38.3895),
            city(313550, "Gümüşhane", "Karadeniz", 40.4600, 39.4700),
            city(313337, "Hakkari", "Doğu Anadolu", 37.5833, 43.7333),
            city(312880, "Hatay", "Akdeniz", 36.2023, 36.1606),
            city(311984, "Isparta", "Akdeniz", 37.7648, 30.5566),
            city(304531, "Mersin", "Akdeniz", 36.8120, 34.6389),
            city(745044, "İstanbul", "İstanbul", 41.0138, 28.9497),
            city(311046, "İzmir", "İzmir", 38.4127, 27.1384),
            city(309759, "Kars", "Doğu Anadolu", 40.6013, 43.0975),
            city(309480, "Kastamonu", "Karadeniz", 41.3887, 33.7827),
            city(308464, "Kayseri", "İç Anadolu", 38.7322, 35.4853),
            city(307847, "Kırklareli", "Marmara", 41.7333, 27.2167),
            city(307775, "Kırşehir", "İç Anadolu", 39.1425, 34.1709),
            city(745028, "Kocaeli", "Kocaeli", 40.7650, 29.9293),
            city(306571, "Konya", "İç Anadolu", 37.8713, 32.4846),
            city(305681, "Kütahya", "Ege", 39.4167, 29.9833),
            city(304922, "Malatya", "Doğu Anadolu", 38.3502, 38.3167),
            city(304826, "Manisa", "Ege", 38.6191, 27.4289),
            city(304895, "Kahramanmaraş", "Akdeniz", 37.5858, 36.9371),
            city(304652, "Mardin", "Güneydoğu Anadolu", 37.3212, 40.7245),
            city(303798, "Muğla", "Ege", 37.2153, 28.3636),
            city(303666, "Muş", "Doğu Anadolu", 38.7432, 41.4910),
            city(303448, "Nevşehir", "İç Anadolu", 38.6244, 34.7142),
            city(303357, "Niğde", "İç Anadolu", 37.9667, 34.6833),
            city(302824, "Ordu", "Karadeniz", 40.9839, 37.8764),
            city(301550, "Rize", "Karadeniz", 41.0201, 40.5234),
            city(301364, "Sakarya", "Marmara", 40.7569, 30.3783),
            city(740264, "Samsun", "Karadeniz", 41.2798, 36.3361),
            city(300627, "Siirt", "Güneydoğu Anadolu", 37.9333, 41.9500),
            city(300552, "Sinop", "Karadeniz", 42.0231, 35.1531),
            city(300489, "Sivas", "İç Anadolu", 39.7477, 37.0179),
            city(299564, "Tekirdağ", "Marmara", 40.9833, 27.5167),
            city(298970, "Tokat", "Karadeniz", 40.3167, 36.5500),
            city(738648, "Trabzon", "Karadeniz", 41.0050, 39.7269),
            city(298797, "Tunceli", "Doğu Anadolu", 39.1079, 39.5401),
            city(298333, "Şanlıurfa", "Güneydoğu Anadolu", 37.1671, 38.7939),
            city(297880, "Uşak", "Ege", 38.6823, 29.4082),
            city(298117, "Van", "Doğu Anadolu", 38.4946, 43.3832),
            city(297298, "Yozgat", "İç Anadolu", 39.8181, 34.8147),
            city(297126, "Zonguldak", "Karadeniz", 41.4564, 31.7987),
            city(324908, "Aksaray", "İç Anadolu", 38.3687, 34.0370),
            city(321852, "Bayburt", "Karadeniz", 40.2552, 40.2249),
            city(310022, "Karaman", "İç Anadolu", 37.1759, 33.2287),
            city(307901, "Kırıkkale", "İç Anadolu", 39.8468, 33.5153),
            city(321949, "Batman", "Güneydoğu Anadolu", 37.8812, 41.1294),
            city(300305, "Şırnak", "Güneydoğu Anadolu", 37.5164, 42.4594),
            city(322301, "Bartın", "Karadeniz", 41.6344, 32.3375),
            city(323676, "Ardahan", "Doğu Anadolu", 41.1105, 42.7022),
            city(312067, "Iğdır", "Doğu Anadolu", 39.9196, 44.0450),
            city(297424, "Yalova", "Marmara", 40.6550, 29.2769),
            city(310188, "Karabük", "Karadeniz", 41.2061, 32.6204),
            city(308064, "Kilis", "Güneydoğu Anadolu", 36.7184, 37.1212),
            city(302720, "Osmaniye", "Akdeniz", 37.0742, 36.2478),
            city(315998, "Düzce", "Karadeniz", 40.8438, 31.1565)
        )

        fun city(id: Long, name: String, region: String, latitude: Double, longitude: Double) = City(
            id = id,
            name = name,
            region = region,
            country = "Türkiye",
            coordinates = Coordinates(latitude = latitude, longitude = longitude)
        )
    }
}
