package com.kampplus.hava.feature.weather.data.local

import com.kampplus.hava.feature.weather.domain.model.City
import com.kampplus.hava.feature.weather.domain.model.Coordinates
import javax.inject.Inject

/**
 * Türkiye'nin 81 ilinin tamamına ait ilçe kataloğu.
 * Koordinatlar Open-Meteo Geocoding API sonuçlarıyla uyumludur.
 */
class DistrictCatalog @Inject constructor() {

    /** Verilen il id'sine ait ilçeleri döner. */
    fun districtsOf(provinceId: Long): List<City> {
        val list = DISTRICTS[provinceId]
        if (!list.isNullOrEmpty()) return list

        val prov = TurkishCityCatalog().cities().find { it.id == provinceId }
        return if (prov != null) {
            listOf(
                district(
                    id = provinceId * 10_000L + 1L,
                    name = "${prov.name} (Merkez)",
                    province = prov.name,
                    lat = prov.coordinates.latitude,
                    lon = prov.coordinates.longitude
                )
            )
        } else {
            emptyList()
        }
    }

    companion object {
        private fun district(id: Long, name: String, province: String, lat: Double, lon: Double) = City(
            id = id,
            name = name,
            region = province,
            country = "Türkiye",
            coordinates = Coordinates(latitude = lat, longitude = lon)
        )

        val DISTRICTS: Map<Long, List<City>> = mapOf(
            // 01 ADANA (325363)
            325363L to listOf(
                district(900101, "Seyhan", "Adana", 37.0014, 35.3213),
                district(900102, "Çukurova", "Adana", 37.0226, 35.3639),
                district(900103, "Yüreğir", "Adana", 36.9726, 35.3886),
                district(900104, "Sarıçam", "Adana", 37.0631, 35.4567),
                district(900105, "Ceyhan", "Adana", 37.0267, 35.8133),
                district(900106, "Kozan", "Adana", 37.4533, 35.8167),
                district(900107, "İmamoğlu", "Adana", 37.2867, 35.6967),
                district(900108, "Karataş", "Adana", 36.5733, 35.3867),
                district(900109, "Karaisalı", "Adana", 37.2533, 35.0667),
                district(900110, "Pozantı", "Adana", 37.4233, 34.8767),
                district(900111, "Yumurtalık", "Adana", 36.7667, 35.7800),
                district(900112, "Aladağ", "Adana", 37.5467, 35.3975),
                district(900113, "Feke", "Adana", 37.8067, 35.9167),
                district(900114, "Saimbeyli", "Adana", 37.9867, 36.0933),
                district(900115, "Tufanbeyli", "Adana", 38.2633, 36.2133)
            ),
            // 02 ADIYAMAN (325330)
            325330L to listOf(
                district(900201, "Merkez", "Adıyaman", 37.7644, 38.2763),
                district(900202, "Kahta", "Adıyaman", 37.7833, 38.6167),
                district(900203, "Besni", "Adıyaman", 37.6917, 37.8617),
                district(900204, "Gölbaşı", "Adıyaman", 37.7833, 37.6500),
                district(900205, "Gerger", "Adıyaman", 38.0167, 39.0333),
                district(900206, "Sincik", "Adıyaman", 38.0333, 38.6167),
                district(900207, "Çelikhan", "Adıyaman", 38.0333, 38.2333),
                district(900208, "Tut", "Adıyaman", 37.8000, 37.9333),
                district(900209, "Samsat", "Adıyaman", 37.5667, 38.4833)
            ),
            // 03 AFYONKARAHİSAR (324907)
            324907L to listOf(
                district(900301, "Merkez", "Afyonkarahisar", 38.7569, 30.5387),
                district(900302, "Sandıklı", "Afyonkarahisar", 38.4667, 30.2667),
                district(900303, "Dinar", "Afyonkarahisar", 38.0667, 30.1667),
                district(900304, "Bolvadin", "Afyonkarahisar", 38.7167, 31.0500),
                district(900305, "Emirdağ", "Afyonkarahisar", 39.0167, 31.1500),
                district(900306, "Çay", "Afyonkarahisar", 38.5833, 31.0333),
                district(900307, "Şuhut", "Afyonkarahisar", 38.5333, 30.5500),
                district(900308, "İscehisar", "Afyonkarahisar", 38.8667, 30.7500),
                district(900309, "Sinanpaşa", "Afyonkarahisar", 38.7333, 30.2500),
                district(900310, "Dazkırı", "Afyonkarahisar", 37.9167, 29.8667)
            ),
            // 04 AĞRI (324707)
            324707L to listOf(
                district(900401, "Merkez", "Ağrı", 39.7191, 43.0513),
                district(900402, "Doğubayazıt", "Ağrı", 39.5500, 44.0833),
                district(900403, "Patnos", "Ağrı", 39.2333, 42.8667),
                district(900404, "Diyadin", "Ağrı", 39.5500, 43.6667),
                district(900405, "Eleşkirt", "Ağrı", 39.8000, 42.6833),
                district(900406, "Tutak", "Ağrı", 39.5333, 42.7833),
                district(900407, "Taşlıçay", "Ağrı", 39.6333, 43.3667),
                district(900408, "Hamur", "Ağrı", 39.6167, 42.9833)
            ),
            // 05 AMASYA (324083)
            324083L to listOf(
                district(900501, "Merkez", "Amasya", 40.6534, 35.8331),
                district(900502, "Merzifon", "Amasya", 40.8744, 35.4628),
                district(900503, "Suluova", "Amasya", 40.8333, 35.6500),
                district(900504, "Taşova", "Amasya", 40.7667, 36.3333),
                district(900505, "Gümüşhacıköy", "Amasya", 40.8667, 35.2167),
                district(900506, "Göynücek", "Amasya", 40.4000, 35.5333),
                district(900507, "Hamamözü", "Amasya", 40.8000, 35.0333)
            ),
            // 06 ANKARA (323786)
            323786L to listOf(
                district(900601, "Çankaya", "Ankara", 39.9180, 32.8610),
                district(900602, "Keçiören", "Ankara", 39.9989, 32.8547),
                district(900603, "Yenimahalle", "Ankara", 39.9667, 32.8006),
                district(900604, "Mamak", "Ankara", 39.9331, 32.9172),
                district(900605, "Etimesgut", "Ankara", 39.9508, 32.6811),
                district(900606, "Sincan", "Ankara", 39.9717, 32.5819),
                district(900607, "Altındağ", "Ankara", 39.9500, 32.8667),
                district(900608, "Pursaklar", "Ankara", 40.0350, 32.9019),
                district(900609, "Gölbaşı", "Ankara", 39.7872, 32.8044),
                district(900610, "Polatlı", "Ankara", 39.5847, 32.1439),
                district(900611, "Çubuk", "Ankara", 40.2381, 33.0361),
                district(900612, "Kahramankazan", "Ankara", 40.2269, 32.6819),
                district(900613, "Beypazarı", "Ankara", 40.1686, 31.9214),
                district(900614, "Elmadağ", "Ankara", 39.9211, 33.2303),
                district(900615, "Akyurt", "Ankara", 40.1339, 33.0839),
                district(900616, "Nallıhan", "Ankara", 40.1875, 31.3511),
                district(900617, "Kızılcahamam", "Ankara", 40.4758, 32.6519),
                district(900618, "Haymana", "Ankara", 39.4322, 32.4975),
                district(900619, "Şereflikoçhisar", "Ankara", 38.9369, 33.5347),
                district(900620, "Bala", "Ankara", 39.5594, 33.1217),
                district(900621, "Ayaş", "Ankara", 40.0206, 32.3378),
                district(900622, "Kalecik", "Ankara", 40.0972, 33.4164),
                district(900623, "Güdül", "Ankara", 40.2217, 32.2461),
                district(900624, "Çamlıdere", "Ankara", 40.4914, 32.2803),
                district(900625, "Evren", "Ankara", 39.0239, 33.8058)
            ),
            // 07 ANTALYA (323777)
            323777L to listOf(
                district(900701, "Muratpaşa", "Antalya", 36.8969, 30.6931),
                district(900702, "Kepez", "Antalya", 36.9556, 30.7139),
                district(900703, "Konyaaltı", "Antalya", 36.8672, 30.6097),
                district(900704, "Alanya", "Antalya", 36.5431, 32.0028),
                district(900705, "Manavgat", "Antalya", 36.7861, 31.4417),
                district(900706, "Serik", "Antalya", 36.9181, 31.1019),
                district(900707, "Aksu", "Antalya", 36.9178, 30.8133),
                district(900708, "Döşemealtı", "Antalya", 37.0417, 30.6231),
                district(900709, "Kemer", "Antalya", 36.5986, 30.5589),
                district(900710, "Kumluca", "Antalya", 36.3706, 30.2842),
                district(900711, "Kaş", "Antalya", 36.2017, 29.6417),
                district(900712, "Finike", "Antalya", 36.2981, 30.1503),
                district(900713, "Gazipaşa", "Antalya", 36.2694, 32.5133),
                district(900714, "Korkuteli", "Antalya", 37.0581, 30.1953),
                district(900715, "Elmalı", "Antalya", 36.7356, 29.9183),
                district(900716, "Demre", "Antalya", 36.2458, 29.9842),
                district(900717, "Akseki", "Antalya", 37.0511, 31.7908),
                district(900718, "Gündoğmuş", "Antalya", 36.8200, 32.0375),
                district(900719, "İbradı", "Antalya", 37.1328, 31.5942)
            ),
            // 08 ARTVİN (323607)
            323607L to listOf(
                district(900801, "Merkez", "Artvin", 41.1828, 41.8183),
                district(900802, "Hopa", "Artvin", 41.4000, 41.4333),
                district(900803, "Borçka", "Artvin", 41.3500, 41.6833),
                district(900804, "Arhavi", "Artvin", 41.3500, 41.3000),
                district(900805, "Yusufeli", "Artvin", 40.8167, 41.5333),
                district(900806, "Şavşat", "Artvin", 41.2500, 42.3667),
                district(900807, "Ardanuç", "Artvin", 41.1333, 42.0667),
                district(900808, "Kemalpaşa", "Artvin", 41.4833, 41.5167),
                district(900809, "Murgul", "Artvin", 41.2667, 41.5667)
            ),
            // 09 AYDIN (323204)
            323204L to listOf(
                district(900901, "Efeler", "Aydın", 37.8481, 27.8453),
                district(900902, "Nazilli", "Aydın", 37.9167, 28.3167),
                district(900903, "Söke", "Aydın", 37.7500, 27.4000),
                district(900904, "Kuşadası", "Aydın", 37.8572, 27.2611),
                district(900905, "Didim", "Aydın", 37.3833, 27.2667),
                district(900906, "İncirliova", "Aydın", 37.8500, 27.7167),
                district(900907, "Çine", "Aydın", 37.6167, 28.0667),
                district(900908, "Germencik", "Aydın", 37.8667, 27.6000),
                district(900909, "Bozdoğan", "Aydın", 37.5833, 28.3167),
                district(900910, "Köşk", "Aydın", 37.8500, 28.0500),
                district(900911, "Kuyucak", "Aydın", 37.9167, 28.4667),
                district(900912, "Sultanhisar", "Aydın", 37.8833, 28.1500),
                district(900913, "Karacasu", "Aydın", 37.7333, 28.6000),
                district(900914, "Yenipazar", "Aydın", 37.8167, 28.2000),
                district(900915, "Buharkent", "Aydın", 37.9667, 28.7500)
            ),
            // 10 BALIKESİR (322648)
            322648L to listOf(
                district(901001, "Karesi", "Balıkesir", 39.6484, 27.8826),
                district(901002, "Altıeylül", "Balıkesir", 39.6333, 27.8667),
                district(901003, "Bandırma", "Balıkesir", 40.3522, 27.9772),
                district(901004, "Edremit", "Balıkesir", 39.5961, 27.0244),
                district(901005, "Gönen", "Balıkesir", 40.1047, 27.6539),
                district(901006, "Ayvalık", "Balıkesir", 39.3197, 26.6961),
                district(901007, "Burhaniye", "Balıkesir", 39.4986, 26.9728),
                district(901008, "Bigadiç", "Balıkesir", 39.4000, 28.1333),
                district(901009, "Susurluk", "Balıkesir", 39.9167, 28.1667),
                district(901010, "Dursunbey", "Balıkesir", 39.5833, 28.6333),
                district(901011, "Sındırgı", "Balıkesir", 39.2333, 28.1833),
                district(901012, "Erdek", "Balıkesir", 40.4000, 27.8000),
                district(901013, "Havran", "Balıkesir", 39.5667, 27.1000),
                district(901014, "İvrindi", "Balıkesir", 39.5833, 27.5000),
                district(901015, "Manyas", "Balıkesir", 40.0500, 27.9667)
            ),
            // 11 BİLECİK (321703)
            321703L to listOf(
                district(901101, "Merkez", "Bilecik", 40.1426, 29.9793),
                district(901102, "Bozüyük", "Bilecik", 39.9000, 30.0333),
                district(901103, "Osmaneli", "Bilecik", 40.3500, 30.0167),
                district(901104, "Söğüt", "Bilecik", 40.0167, 30.1833),
                district(901105, "Pazaryeri", "Bilecik", 39.9833, 29.9000),
                district(901106, "Gölpazarı", "Bilecik", 40.2833, 30.3167)
            ),
            // 12 BİNGÖL (321528)
            321528L to listOf(
                district(901201, "Merkez", "Bingöl", 38.8847, 40.4939),
                district(901202, "Genç", "Bingöl", 38.7500, 40.5500),
                district(901203, "Solhan", "Bingöl", 38.9667, 41.0500),
                district(901204, "Karlıova", "Bingöl", 39.3000, 41.0167),
                district(901205, "Kiğı", "Bingöl", 39.3167, 40.3500)
            ),
            // 13 BİTLİS (321453)
            321453L to listOf(
                district(901301, "Merkez", "Bitlis", 38.4006, 42.1095),
                district(901302, "Tatvan", "Bitlis", 38.5000, 42.2833),
                district(901303, "Ahlat", "Bitlis", 38.7500, 42.4833),
                district(901304, "Adilcevaz", "Bitlis", 38.8000, 42.7333),
                district(901305, "Güroymak", "Bitlis", 38.5500, 41.9833),
                district(901306, "Hizan", "Bitlis", 38.2167, 42.4167)
            ),
            // 14 BOLU (321287)
            321287L to listOf(
                district(901401, "Merkez", "Bolu", 40.7358, 31.6061),
                district(901402, "Gerede", "Bolu", 40.8000, 32.2000),
                district(901403, "Mudurnu", "Bolu", 40.4667, 31.1333),
                district(901404, "Göynük", "Bolu", 40.4000, 30.7833),
                district(901405, "Mengen", "Bolu", 40.9500, 32.0500),
                district(901406, "Yeniçağa", "Bolu", 40.7667, 32.0333)
            ),
            // 15 BURDUR (320698)
            320698L to listOf(
                district(901501, "Merkez", "Burdur", 37.7203, 30.2908),
                district(901502, "Bucak", "Burdur", 37.4500, 30.5833),
                district(901503, "Gölhisar", "Burdur", 37.1500, 29.5167),
                district(901504, "Yeşilova", "Burdur", 37.5167, 29.7667),
                district(901505, "Tefenni", "Burdur", 37.3167, 29.7833)
            ),
            // 16 BURSA (750269)
            750269L to listOf(
                district(901601, "Osmangazi", "Bursa", 40.1956, 29.0856),
                district(901602, "Nilüfer", "Bursa", 40.2228, 28.9644),
                district(901603, "Yıldırım", "Bursa", 40.1894, 29.1300),
                district(901604, "İnegöl", "Bursa", 40.0728, 29.5125),
                district(901605, "Gemlik", "Bursa", 40.4303, 29.1533),
                district(901606, "Mudanya", "Bursa", 40.3758, 28.8842),
                district(901607, "Mustafakemalpaşa", "Bursa", 40.0306, 28.4056),
                district(901608, "Karacabey", "Bursa", 40.2153, 28.3556),
                district(901609, "Orhangazi", "Bursa", 40.4897, 29.3133),
                district(901610, "Kestel", "Bursa", 40.1922, 29.2392),
                district(901611, "Gürsu", "Bursa", 40.2222, 29.1956),
                district(901612, "Yenişehir", "Bursa", 40.2653, 29.6378),
                district(901613, "İznik", "Bursa", 40.4267, 29.7197),
                district(901614, "Orhaneli", "Bursa", 39.8014, 28.9875),
                district(901615, "Keles", "Bursa", 39.9181, 29.2275),
                district(901616, "Büyükorhan", "Bursa", 39.7681, 28.9994),
                district(901617, "Harmancık", "Bursa", 39.6847, 29.1539)
            ),
            // 17 ÇANAKKALE (319488)
            319488L to listOf(
                district(901701, "Merkez", "Çanakkale", 40.1553, 26.4142),
                district(901702, "Biga", "Çanakkale", 40.2333, 27.2500),
                district(901703, "Çan", "Çanakkale", 40.0333, 27.0500),
                district(901704, "Gelibolu", "Çanakkale", 40.4167, 26.6667),
                district(901705, "Yenice", "Çanakkale", 39.9333, 27.2500),
                district(901706, "Ayvacık", "Çanakkale", 39.6000, 26.4000),
                district(901707, "Ezine", "Çanakkale", 39.7833, 26.3333),
                district(901708, "Lapseki", "Çanakkale", 40.3500, 26.6833),
                district(901709, "Bayramiç", "Çanakkale", 39.8167, 26.6167),
                district(901710, "Gökçeada", "Çanakkale", 40.1833, 25.8667),
                district(901711, "Bozcaada", "Çanakkale", 39.8333, 26.0667)
            ),
            // 18 ÇANKIRI (319342)
            319342L to listOf(
                district(901801, "Merkez", "Çankırı", 40.6013, 33.6134),
                district(901802, "Çerkeş", "Çankırı", 40.8167, 32.8833),
                district(901803, "Ilgaz", "Çankırı", 40.9167, 33.6167),
                district(901804, "Orta", "Çankırı", 40.6167, 33.1167),
                district(901805, "Şabanözü", "Çankırı", 40.4833, 33.2833),
                district(901806, "Kurşunlu", "Çankırı", 40.8333, 33.2667)
            ),
            // 19 ÇORUM (318694)
            318694L to listOf(
                district(901901, "Merkez", "Çorum", 40.5506, 34.9556),
                district(901902, "Sungurlu", "Çorum", 40.1667, 34.3833),
                district(901903, "Osmancık", "Çorum", 40.9667, 34.8000),
                district(901904, "İskilip", "Çorum", 40.7333, 34.4667),
                district(901905, "Alaca", "Çorum", 40.1667, 34.8333),
                district(901906, "Kargı", "Çorum", 41.1333, 34.5000),
                district(901907, "Mecitözü", "Çorum", 40.5500, 35.3000),
                district(901908, "Bayat", "Çorum", 40.6667, 34.2667)
            ),
            // 20 DENİZLİ (317109)
            317109L to listOf(
                district(902001, "Pamukkale", "Denizli", 37.7742, 29.0875),
                district(902002, "Merkezefendi", "Denizli", 37.7833, 29.0667),
                district(902003, "Çivril", "Denizli", 38.3000, 29.7333),
                district(902004, "Acıpayam", "Denizli", 37.4333, 29.3500),
                district(902005, "Tavas", "Denizli", 37.5833, 29.0667),
                district(902006, "Honaz", "Denizli", 37.7667, 29.2667),
                district(902007, "Sarayköy", "Denizli", 37.9167, 28.9333),
                district(902008, "Buldan", "Denizli", 38.0500, 28.8333),
                district(902009, "Kale", "Denizli", 37.4500, 28.8500),
                district(902010, "Çal", "Denizli", 37.9333, 29.4000)
            ),
            // 21 DİYARBAKIR (316541)
            316541L to listOf(
                district(902101, "Bağlar", "Diyarbakır", 37.9200, 40.1817),
                district(902102, "Kayapınar", "Diyarbakır", 37.9100, 40.2367),
                district(902103, "Sur", "Diyarbakır", 37.9136, 40.2172),
                district(902104, "Yenişehir", "Diyarbakır", 37.9217, 40.2542),
                district(902105, "Ergani", "Diyarbakır", 38.2706, 39.7575),
                district(902106, "Bismil", "Diyarbakır", 37.8539, 40.6558),
                district(902107, "Silvan", "Diyarbakır", 38.1367, 41.0083),
                district(902108, "Çınar", "Diyarbakır", 37.7217, 40.4131),
                district(902109, "Çermik", "Diyarbakır", 37.9733, 38.9317),
                district(902110, "Dicle", "Diyarbakır", 38.3636, 40.0825),
                district(902111, "Kulp", "Diyarbakır", 38.5264, 41.0133),
                district(902112, "Hani", "Diyarbakır", 38.4064, 40.3967),
                district(902113, "Lice", "Diyarbakır", 38.4572, 40.6525),
                district(902114, "Eğil", "Diyarbakır", 38.2853, 40.0553)
            ),
            // 22 EDİRNE (315918)
            315918L to listOf(
                district(902201, "Merkez", "Edirne", 41.6772, 26.5557),
                district(902202, "Keşan", "Edirne", 40.8500, 26.6333),
                district(902203, "Uzunköprü", "Edirne", 41.2667, 26.6833),
                district(902204, "İpsala", "Edirne", 40.9167, 26.3833),
                district(902205, "Havsa", "Edirne", 41.5500, 26.8167),
                district(902206, "Meriç", "Edirne", 41.1833, 26.4167),
                district(902207, "Enez", "Edirne", 40.7167, 26.0833)
            ),
            // 23 ELAZIĞ (315809)
            315809L to listOf(
                district(902301, "Merkez", "Elazığ", 38.6810, 39.2264),
                district(902302, "Kovancılar", "Elazığ", 38.7167, 39.8500),
                district(902303, "Karakoçan", "Elazığ", 38.9500, 40.0333),
                district(902304, "Palu", "Elazığ", 38.6833, 39.9333),
                district(902305, "Baskil", "Elazığ", 38.5667, 38.8167),
                district(902306, "Sivrice", "Elazığ", 38.4500, 39.3167),
                district(902307, "Maden", "Elazığ", 38.3833, 39.6667),
                district(902308, "Keban", "Elazığ", 38.8000, 38.7333)
            ),
            // 24 ERZİNCAN (315498)
            315498L to listOf(
                district(902401, "Merkez", "Erzincan", 39.7500, 39.5000),
                district(902402, "Tercan", "Erzincan", 39.7833, 40.3833),
                district(902403, "Üzümlü", "Erzincan", 39.7167, 39.7000),
                district(902404, "Çayırlı", "Erzincan", 39.9833, 40.1167),
                district(902405, "İliç", "Erzincan", 39.4500, 38.5667),
                district(902406, "Kemaliye", "Erzincan", 39.2667, 38.5000),
                district(902407, "Refahiye", "Erzincan", 39.9000, 38.7667),
                district(902408, "Kemah", "Erzincan", 39.6000, 39.0333)
            ),
            // 25 ERZURUM (315368)
            315368L to listOf(
                district(902501, "Yakutiye", "Erzurum", 39.9086, 41.2769),
                district(902502, "Palandöken", "Erzurum", 39.8781, 41.2978),
                district(902503, "Aziziye", "Erzurum", 39.9083, 41.2275),
                district(902504, "Horasan", "Erzurum", 40.0400, 42.1717),
                district(902505, "Oltu", "Erzurum", 40.5483, 41.9933),
                district(902506, "Pasinler", "Erzurum", 39.9769, 41.6800),
                district(902507, "İspir", "Erzurum", 40.4872, 40.9939),
                district(902508, "Hınıs", "Erzurum", 39.3667, 41.7000),
                district(902509, "Tortum", "Erzurum", 40.3133, 41.5500),
                district(902510, "Aşkale", "Erzurum", 39.9167, 40.6833),
                district(902511, "Karayazı", "Erzurum", 39.6833, 42.1500),
                district(902512, "Tekman", "Erzurum", 39.6383, 41.5033),
                district(902513, "Narman", "Erzurum", 40.3783, 41.8900),
                district(902514, "Şenkaya", "Erzurum", 40.5567, 42.3467),
                district(902515, "Uzundere", "Erzurum", 40.2125, 41.5650)
            ),
            // 26 ESKİŞEHİR (315202)
            315202L to listOf(
                district(902601, "Tepebaşı", "Eskişehir", 39.7897, 30.5089),
                district(902602, "Odunpazarı", "Eskişehir", 39.7589, 30.5289),
                district(902603, "Sivrihisar", "Eskişehir", 39.4500, 31.5333),
                district(902604, "Çifteler", "Eskişehir", 39.3833, 31.1333),
                district(902605, "Seyitgazi", "Eskişehir", 39.4500, 30.7000),
                district(902606, "Alpu", "Eskişehir", 39.7667, 30.9667),
                district(902607, "Mihalıççık", "Eskişehir", 39.8667, 31.5000),
                district(902608, "Mahmudiye", "Eskişehir", 39.5000, 30.9833),
                district(902609, "İnönü", "Eskişehir", 39.8167, 30.1500),
                district(902610, "Günyüzü", "Eskişehir", 39.3833, 31.8167)
            ),
            // 27 GAZİANTEP (314830)
            314830L to listOf(
                district(902701, "Şahinbey", "Gaziantep", 37.0594, 37.3825),
                district(902702, "Şehitkamil", "Gaziantep", 37.0717, 37.3228),
                district(902703, "Nizip", "Gaziantep", 37.0053, 37.7958),
                district(902704, "İslahiye", "Gaziantep", 37.0219, 36.6336),
                district(902705, "Nurdağı", "Gaziantep", 37.1756, 36.7358),
                district(902706, "Araban", "Gaziantep", 37.4247, 37.6836),
                district(902707, "Oğuzeli", "Gaziantep", 36.9622, 37.5067),
                district(902708, "Yavuzeli", "Gaziantep", 37.3167, 37.5667),
                district(902709, "Karkamış", "Gaziantep", 36.8353, 38.0050)
            ),
            // 28 GİRESUN (314144)
            314144L to listOf(
                district(902801, "Merkez", "Giresun", 40.9128, 38.3895),
                district(902802, "Bulancak", "Giresun", 40.9333, 38.2333),
                district(902803, "Espiye", "Giresun", 40.9500, 38.7167),
                district(902804, "Görele", "Giresun", 41.0333, 39.0000),
                district(902805, "Tirebolu", "Giresun", 41.0000, 38.8167),
                district(902806, "Şebinkarahisar", "Giresun", 40.2833, 38.4167),
                district(902807, "Dereli", "Giresun", 40.6500, 38.4500),
                district(902808, "Keşap", "Giresun", 40.9167, 38.5000),
                district(902809, "Piraziz", "Giresun", 40.9500, 38.1167),
                district(902810, "Yağlıdere", "Giresun", 40.8500, 38.6500)
            ),
            // 29 GÜMÜŞHANE (313550)
            313550L to listOf(
                district(902901, "Merkez", "Gümüşhane", 40.4600, 39.4700),
                district(902902, "Kelkit", "Gümüşhane", 40.1333, 39.4333),
                district(902903, "Şiran", "Gümüşhane", 40.1833, 39.1167),
                district(902904, "Torul", "Gümüşhane", 40.5667, 39.2833),
                district(902905, "Kürtün", "Gümüşhane", 40.7167, 39.1833),
                district(902906, "Köse", "Gümüşhane", 40.2667, 39.6500)
            ),
            // 30 HAKKARİ (313337)
            313337L to listOf(
                district(903001, "Merkez", "Hakkari", 37.5833, 43.7333),
                district(903002, "Yüksekova", "Hakkari", 37.5667, 44.2833),
                district(903003, "Şemdinli", "Hakkari", 37.3000, 44.5667),
                district(903004, "Çukurca", "Hakkari", 37.2500, 43.4833),
                district(903005, "Derecik", "Hakkari", 37.1000, 44.4000)
            ),
            // 31 HATAY (312880)
            312880L to listOf(
                district(903101, "Antakya", "Hatay", 36.2023, 36.1606),
                district(903102, "İskenderun", "Hatay", 36.5828, 36.1628),
                district(903103, "Defne", "Hatay", 36.2386, 36.1700),
                district(903104, "Dörtyol", "Hatay", 36.8383, 36.2239),
                district(903105, "Samandağ", "Hatay", 36.0686, 35.9811),
                district(903106, "Kırıkhan", "Hatay", 36.4983, 36.3567),
                district(903107, "Reyhanlı", "Hatay", 36.2689, 36.5681),
                district(903108, "Arsuz", "Hatay", 36.4167, 35.8833),
                district(903109, "Altınözü", "Hatay", 36.0767, 36.2100),
                district(903110, "Hassa", "Hatay", 36.8006, 36.4933),
                district(903111, "Payas", "Hatay", 36.7583, 36.2333),
                district(903112, "Erzin", "Hatay", 36.9556, 36.2017),
                district(903113, "Yayladağı", "Hatay", 35.9042, 36.0508),
                district(903114, "Belen", "Hatay", 36.4881, 36.1858),
                district(903115, "Kumlu", "Hatay", 36.4033, 36.2567)
            ),
            // 32 ISPARTA (311984)
            311984L to listOf(
                district(903201, "Merkez", "Isparta", 37.7648, 30.5566),
                district(903202, "Yalvaç", "Isparta", 38.3000, 31.1833),
                district(903203, "Eğirdir", "Isparta", 37.8667, 30.8500),
                district(903204, "Şarkikaraağaç", "Isparta", 38.0833, 31.3667),
                district(903205, "Gelendost", "Isparta", 38.1167, 31.0000),
                district(903206, "Keçiborlu", "Isparta", 37.9500, 30.3000),
                district(903207, "Senirkent", "Isparta", 38.1000, 30.5500),
                district(903208, "Sütçüler", "Isparta", 37.5000, 30.9833),
                district(903209, "Gönen", "Isparta", 37.9667, 30.5167),
                district(903210, "Uluborlu", "Isparta", 38.0833, 30.4500),
                district(903211, "Atabey", "Isparta", 37.9500, 30.6500)
            ),
            // 33 MERSİN (304531)
            304531L to listOf(
                district(903301, "Akdeniz", "Mersin", 36.8120, 34.6389),
                district(903302, "Toroslar", "Mersin", 36.8269, 34.5892),
                district(903303, "Yenişehir", "Mersin", 36.8092, 34.6214),
                district(903304, "Mezitli", "Mersin", 36.7822, 34.6439),
                district(903305, "Tarsus", "Mersin", 36.9183, 34.9000),
                district(903306, "Erdemli", "Mersin", 36.6094, 34.3133),
                district(903307, "Silifke", "Mersin", 36.3772, 33.9361),
                district(903308, "Anamur", "Mersin", 36.0783, 32.8442),
                district(903309, "Mut", "Mersin", 36.6467, 33.4358),
                district(903310, "Bozyazı", "Mersin", 36.1000, 32.9667),
                district(903311, "Gülnar", "Mersin", 36.3267, 33.3983),
                district(903312, "Aydıncık", "Mersin", 36.1500, 33.3156),
                district(903313, "Çamlıyayla", "Mersin", 37.0742, 34.5908)
            ),
            // 34 İSTANBUL (745044)
            745044L to listOf(
                district(903401, "Kadıköy", "İstanbul", 40.9819, 29.0700),
                district(903402, "Beşiktaş", "İstanbul", 41.0422, 29.0042),
                district(903403, "Şişli", "İstanbul", 41.0600, 28.9867),
                district(903404, "Üsküdar", "İstanbul", 41.0239, 29.0150),
                district(903405, "Fatih", "İstanbul", 41.0186, 28.9397),
                district(903406, "Beyoğlu", "İstanbul", 41.0369, 28.9742),
                district(903407, "Maltepe", "İstanbul", 40.9358, 29.1311),
                district(903408, "Ataşehir", "İstanbul", 40.9925, 29.1275),
                district(903409, "Ümraniye", "İstanbul", 41.0175, 29.1239),
                district(903410, "Pendik", "İstanbul", 40.8778, 29.2358),
                district(903411, "Kartal", "İstanbul", 40.9131, 29.1908),
                district(903412, "Tuzla", "İstanbul", 40.8175, 29.2989),
                district(903413, "Beykoz", "İstanbul", 41.1333, 29.1167),
                district(903414, "Sarıyer", "İstanbul", 41.1667, 29.0500),
                district(903415, "Bakırköy", "İstanbul", 40.9783, 28.8697),
                district(903416, "Bahçelievler", "İstanbul", 41.0011, 28.8619),
                district(903417, "Küçükçekmece", "İstanbul", 41.0011, 28.7794),
                district(903418, "Bağcılar", "İstanbul", 41.0378, 28.8583),
                district(903419, "Esenyurt", "İstanbul", 41.0353, 28.6694),
                district(903420, "Beylikdüzü", "İstanbul", 40.9894, 28.6378),
                district(903421, "Başakşehir", "İstanbul", 41.0939, 28.8097),
                district(903422, "Eyüpsultan", "İstanbul", 41.0583, 28.9333),
                district(903423, "Kağıthane", "İstanbul", 41.0819, 28.9717),
                district(903424, "Gaziosmanpaşa", "İstanbul", 41.0697, 28.9117),
                district(903425, "Sultangazi", "İstanbul", 41.1072, 28.9122),
                district(903426, "Sultanbeyli", "İstanbul", 40.9608, 29.2614),
                district(903427, "Sancaktepe", "İstanbul", 41.0000, 29.2167),
                district(903428, "Çekmeköy", "İstanbul", 41.0489, 29.2039),
                district(903429, "Zeytinburnu", "İstanbul", 41.0017, 28.9058),
                district(903430, "Güngören", "İstanbul", 41.0194, 28.8711),
                district(903431, "Bayrampaşa", "İstanbul", 41.0458, 28.9161),
                district(903432, "Esenler", "İstanbul", 41.0406, 28.8819),
                district(903433, "Avcılar", "İstanbul", 40.9797, 28.7211),
                district(903434, "Büyükçekmece", "İstanbul", 41.0211, 28.5811),
                district(903435, "Arnavutköy", "İstanbul", 41.1844, 28.7400),
                district(903436, "Silivri", "İstanbul", 41.0733, 28.2467),
                district(903437, "Çatalca", "İstanbul", 41.1439, 28.4600),
                district(903438, "Şile", "İstanbul", 41.1758, 29.6133),
                district(903439, "Adalar", "İstanbul", 40.8683, 29.1000)
            ),
            // 35 İZMİR (311046)
            311046L to listOf(
                district(903501, "Konak", "İzmir", 38.4122, 27.1383),
                district(903502, "Karşıyaka", "İzmir", 38.4589, 27.1075),
                district(903503, "Bornova", "İzmir", 38.4686, 27.2194),
                district(903504, "Buca", "İzmir", 38.3817, 27.1800),
                district(903505, "Bayraklı", "İzmir", 38.4628, 27.1614),
                district(903506, "Çiğli", "İzmir", 38.5078, 27.0511),
                district(903507, "Karabağlar", "İzmir", 38.3594, 27.1206),
                district(903508, "Gaziemir", "İzmir", 38.3200, 27.1322),
                district(903509, "Balçova", "İzmir", 38.3900, 27.0578),
                district(903510, "Narlıdere", "İzmir", 38.3836, 26.9822),
                district(903511, "Güzelbahçe", "İzmir", 38.3347, 26.9064),
                district(903512, "Torbalı", "İzmir", 38.1611, 27.3636),
                district(903513, "Menemen", "İzmir", 38.6072, 27.0636),
                district(903514, "Kemalpaşa", "İzmir", 38.4267, 27.4233),
                district(903515, "Menderes", "İzmir", 38.2533, 27.1344),
                district(903516, "Aliağa", "İzmir", 38.7972, 26.9769),
                district(903517, "Ödemiş", "İzmir", 38.2256, 28.0003),
                district(903518, "Bergama", "İzmir", 39.1233, 27.1808),
                district(903519, "Urla", "İzmir", 38.3228, 26.7658),
                district(903520, "Çeşme", "İzmir", 38.3264, 26.3033),
                district(903521, "Seferihisar", "İzmir", 38.1981, 26.8425),
                district(903522, "Tire", "İzmir", 38.0881, 27.7336),
                district(903523, "Dikili", "İzmir", 39.0728, 26.8906),
                district(903524, "Foça", "İzmir", 38.6617, 26.7567),
                district(903525, "Selçuk", "İzmir", 37.9508, 27.3697),
                district(903526, "Karaburun", "İzmir", 38.6317, 26.5108),
                district(903527, "Kınık", "İzmir", 39.0906, 27.3744),
                district(903528, "Kiraz", "İzmir", 38.2333, 28.2000),
                district(903529, "Beydağ", "İzmir", 38.0903, 28.2028)
            ),
            // 36 KARS (309759)
            309759L to listOf(
                district(903601, "Merkez", "Kars", 40.6013, 43.0975),
                district(903602, "Kağızman", "Kars", 40.1500, 43.1333),
                district(903603, "Sarıkamış", "Kars", 40.3333, 42.5833),
                district(903604, "Selim", "Kars", 40.4667, 42.8000),
                district(903605, "Digor", "Kars", 40.3667, 43.4167),
                district(903606, "Arpaçay", "Kars", 40.8333, 43.3333),
                district(903607, "Akyaka", "Kars", 40.7500, 43.6167),
                district(903608, "Susuz", "Kars", 40.8000, 43.1167)
            ),
            // 37 KASTAMONU (309480)
            309480L to listOf(
                district(903701, "Merkez", "Kastamonu", 41.3887, 33.7827),
                district(903702, "Tosya", "Kastamonu", 41.0167, 34.0333),
                district(903703, "Taşköprü", "Kastamonu", 41.5167, 34.2167),
                district(903704, "Cide", "Kastamonu", 41.8833, 32.9667),
                district(903705, "İnebolu", "Kastamonu", 41.9833, 33.7667),
                district(903706, "Araç", "Kastamonu", 41.2500, 33.3333),
                district(903707, "Devrekani", "Kastamonu", 41.6000, 33.8333),
                district(903708, "Bozkurt", "Kastamonu", 41.9500, 34.0167),
                district(903709, "Daday", "Kastamonu", 41.4667, 33.4667),
                district(903710, "Azdavay", "Kastamonu", 41.6500, 33.3000)
            ),
            // 38 KAYSERİ (308464)
            308464L to listOf(
                district(903801, "Melikgazi", "Kayseri", 38.7200, 35.5000),
                district(903802, "Kocasinan", "Kayseri", 38.7322, 35.4853),
                district(903803, "Talas", "Kayseri", 38.6833, 35.5500),
                district(903804, "Develi", "Kayseri", 38.3833, 35.4833),
                district(903805, "Yahyalı", "Kayseri", 38.1000, 35.6000),
                district(903806, "Bünyan", "Kayseri", 38.8500, 35.8667),
                district(903807, "İncesu", "Kayseri", 38.7167, 35.1833),
                district(903808, "Pınarbaşı", "Kayseri", 38.7167, 36.4000),
                district(903809, "Tomarza", "Kayseri", 38.4500, 35.8000),
                district(903810, "Yeşilhisar", "Kayseri", 38.3500, 35.0833),
                district(903811, "Sarıoğlan", "Kayseri", 39.0833, 35.9667),
                district(903812, "Hacılar", "Kayseri", 38.6333, 35.4500)
            ),
            // 39 KIRKLARELİ (307847)
            307847L to listOf(
                district(903901, "Merkez", "Kırklareli", 41.7333, 27.2167),
                district(903902, "Lüleburgaz", "Kırklareli", 41.4000, 27.3500),
                district(903903, "Babaeski", "Kırklareli", 41.4333, 27.0833),
                district(903904, "Vize", "Kırklareli", 41.5667, 27.7667),
                district(903905, "Pınarhisar", "Kırklareli", 41.6167, 27.5167),
                district(903906, "Demirköy", "Kırklareli", 41.8167, 27.7667),
                district(903907, "Kofçaz", "Kırklareli", 41.9167, 27.1667),
                district(903908, "Pehlivanköy", "Kırklareli", 41.3500, 26.9167)
            ),
            // 40 KIRŞEHİR (307775)
            307775L to listOf(
                district(904001, "Merkez", "Kırşehir", 39.1425, 34.1709),
                district(904002, "Kaman", "Kırşehir", 39.3500, 33.7333),
                district(904003, "Mucur", "Kırşehir", 39.0667, 34.3833),
                district(904004, "Çiçekdağı", "Kırşehir", 39.6000, 34.4167),
                district(904005, "Akpınar", "Kırşehir", 39.4333, 33.9667),
                district(904006, "Boztepe", "Kırşehir", 39.2667, 34.2500)
            ),
            // 41 KOCAELİ (745028)
            745028L to listOf(
                district(904101, "İzmit", "Kocaeli", 40.7650, 29.9293),
                district(904102, "Gebze", "Kocaeli", 40.8031, 29.4306),
                district(904103, "Darıca", "Kocaeli", 40.7664, 29.5717),
                district(904104, "Körfez", "Kocaeli", 40.7617, 29.7908),
                district(904105, "Gölcük", "Kocaeli", 40.6656, 29.8217),
                district(904106, "Derince", "Kocaeli", 40.7375, 29.8408),
                district(904107, "Çayırova", "Kocaeli", 40.8119, 29.5242),
                district(904108, "Kartepe", "Kocaeli", 40.6875, 29.9542),
                district(904109, "Başiskele", "Kocaeli", 40.7331, 29.8611),
                district(904110, "Karamürsel", "Kocaeli", 40.6922, 29.6100),
                district(904111, "Kandıra", "Kocaeli", 41.0756, 30.1547),
                district(904112, "Dilovası", "Kocaeli", 40.7875, 29.5036)
            ),
            // 42 KONYA (306571)
            306571L to listOf(
                district(904201, "Selçuklu", "Konya", 37.9331, 32.4925),
                district(904202, "Meram", "Konya", 37.8531, 32.4364),
                district(904203, "Karatay", "Konya", 37.8836, 32.5019),
                district(904204, "Ereğli", "Konya", 37.5133, 34.0500),
                district(904205, "Akşehir", "Konya", 38.3567, 31.4131),
                district(904206, "Beyşehir", "Konya", 37.6758, 31.7242),
                district(904207, "Çumra", "Konya", 37.5742, 32.7753),
                district(904208, "Seydişehir", "Konya", 37.4206, 31.8481),
                district(904209, "Ilgın", "Konya", 38.2822, 31.9217),
                district(904210, "Cihanbeyli", "Konya", 38.6556, 32.9244),
                district(904211, "Kulu", "Konya", 38.9275, 33.0817),
                district(904212, "Karapınar", "Konya", 37.7167, 33.5500),
                district(904213, "Kadınhanı", "Konya", 38.2414, 32.2286),
                district(904214, "Sarayönü", "Konya", 38.2636, 32.4047),
                district(904215, "Bozkır", "Konya", 37.1878, 32.2483),
                district(904216, "Yunak", "Konya", 38.8197, 31.7333),
                district(904217, "Doğanhisar", "Konya", 38.1500, 31.6667)
            ),
            // 43 KÜTAHYA (305681)
            305681L to listOf(
                district(904301, "Merkez", "Kütahya", 39.4167, 29.9833),
                district(904302, "Tavşanlı", "Kütahya", 39.5500, 29.5000),
                district(904303, "Simav", "Kütahya", 39.0833, 28.9833),
                district(904304, "Gediz", "Kütahya", 39.0000, 29.5667),
                district(904305, "Emet", "Kütahya", 39.3500, 29.2500),
                district(904306, "Altıntaş", "Kütahya", 39.0667, 30.0833),
                district(904307, "Domaniç", "Kütahya", 39.8000, 29.6000),
                district(904308, "Hisarcık", "Kütahya", 39.2667, 29.1833)
            ),
            // 44 MALATYA (304922)
            304922L to listOf(
                district(904401, "Battalgazi", "Malatya", 38.3502, 38.3167),
                district(904402, "Yeşilyurt", "Malatya", 38.3000, 38.2500),
                district(904403, "Doğanşehir", "Malatya", 38.0167, 37.8833),
                district(904404, "Akçadağ", "Malatya", 38.3500, 37.9667),
                district(904405, "Darende", "Malatya", 38.5667, 37.5000),
                district(904406, "Hekimhan", "Malatya", 38.8167, 37.9333),
                district(904407, "Pütürge", "Malatya", 38.2000, 38.8667),
                district(904408, "Yazıhan", "Malatya", 38.6000, 38.2500),
                district(904409, "Arapgir", "Malatya", 39.0500, 38.4833)
            ),
            // 45 MANİSA (304826)
            304826L to listOf(
                district(904501, "Şehzadeler", "Manisa", 38.6191, 27.4289),
                district(904502, "Yunusemre", "Manisa", 38.6333, 27.4000),
                district(904503, "Akhisar", "Manisa", 38.9167, 27.8333),
                district(904504, "Turgutlu", "Manisa", 38.5000, 27.7000),
                district(904505, "Salihli", "Manisa", 38.4833, 28.1333),
                district(904506, "Soma", "Manisa", 39.1833, 27.6167),
                district(904507, "Alaşehir", "Manisa", 38.3500, 28.5167),
                district(904508, "Saruhanlı", "Manisa", 38.7333, 27.5667),
                district(904509, "Kula", "Manisa", 38.5500, 28.6500),
                district(904510, "Demirci", "Manisa", 39.0500, 28.6500),
                district(904511, "Kırkağaç", "Manisa", 39.1000, 27.6667),
                district(904512, "Sarıgöl", "Manisa", 38.2500, 28.7000),
                district(904513, "Gördes", "Manisa", 38.9333, 28.2833)
            ),
            // 46 KAHRAMANMARAŞ (304895)
            304895L to listOf(
                district(904601, "Onikişubat", "Kahramanmaraş", 37.5858, 36.9371),
                district(904602, "Dulkadiroğlu", "Kahramanmaraş", 37.5800, 36.9500),
                district(904603, "Elbistan", "Kahramanmaraş", 38.2000, 37.1833),
                district(904604, "Afşin", "Kahramanmaraş", 38.2500, 36.9167),
                district(904605, "Türkoğlu", "Kahramanmaraş", 37.3833, 36.8500),
                district(904606, "Pazarcık", "Kahramanmaraş", 37.4833, 37.3000),
                district(904607, "Göksun", "Kahramanmaraş", 38.0167, 36.5000),
                district(904608, "Andırın", "Kahramanmaraş", 37.5833, 36.3500),
                district(904609, "Çağlayancerit", "Kahramanmaraş", 37.7500, 37.2833)
            ),
            // 47 MARDİN (304652)
            304652L to listOf(
                district(904701, "Artuklu", "Mardin", 37.3212, 40.7245),
                district(904702, "Kızıltepe", "Mardin", 37.1833, 40.5833),
                district(904703, "Midyat", "Mardin", 37.4167, 41.3667),
                district(904704, "Nusaybin", "Mardin", 37.0667, 41.2167),
                district(904705, "Derik", "Mardin", 37.3667, 40.2667),
                district(904706, "Mazıdağı", "Mardin", 37.5167, 40.4833),
                district(904707, "Dargeçit", "Mardin", 37.5333, 41.7167),
                district(904708, "Savur", "Mardin", 37.5333, 40.8833),
                district(904709, "Ömerli", "Mardin", 37.4333, 40.9500)
            ),
            // 48 MUĞLA (303798)
            303798L to listOf(
                district(904801, "Menteşe", "Muğla", 37.2153, 28.3636),
                district(904802, "Bodrum", "Muğla", 37.0383, 27.4292),
                district(904803, "Fethiye", "Muğla", 36.6217, 29.1164),
                district(904804, "Milas", "Muğla", 37.3167, 27.7833),
                district(904805, "Marmaris", "Muğla", 36.8550, 28.2742),
                district(904806, "Seydikemer", "Muğla", 36.6500, 29.3500),
                district(904807, "Ortaca", "Muğla", 36.8333, 28.7667),
                district(904808, "Yatağan", "Muğla", 37.3333, 28.1333),
                district(904809, "Dalaman", "Muğla", 36.7667, 28.8000),
                district(904810, "Köyceğiz", "Muğla", 36.9667, 28.6833),
                district(904811, "Ula", "Muğla", 37.1000, 28.4167),
                district(904812, "Datça", "Muğla", 36.7333, 27.6833),
                district(904813, "Kavaklıdere", "Muğla", 37.4500, 28.3667)
            ),
            // 49 MUŞ (303666)
            303666L to listOf(
                district(904901, "Merkez", "Muş", 38.7432, 41.4910),
                district(904902, "Bulanık", "Muş", 39.0833, 42.2667),
                district(904903, "Malazgirt", "Muş", 39.1500, 42.5333),
                district(904904, "Varto", "Muş", 39.1667, 41.4500),
                district(904905, "Hasköy", "Muş", 38.6833, 41.7000),
                district(904906, "Korkut", "Muş", 38.7333, 41.7833)
            ),
            // 50 NEVŞEHİR (303448)
            303448L to listOf(
                district(905001, "Merkez", "Nevşehir", 38.6244, 34.7142),
                district(905002, "Ürgüp", "Nevşehir", 38.6333, 34.9167),
                district(905003, "Avanos", "Nevşehir", 38.7167, 34.8500),
                district(905004, "Gülşehir", "Nevşehir", 38.7500, 34.6167),
                district(905005, "Derinkuyu", "Nevşehir", 38.3667, 34.7333),
                district(905006, "Kozaklı", "Nevşehir", 39.2167, 34.8500),
                district(905007, "Acıgöl", "Nevşehir", 38.5500, 34.5167),
                district(905008, "Hacıbektaş", "Nevşehir", 38.9333, 34.5667)
            ),
            // 51 NİĞDE (303357)
            303357L to listOf(
                district(905101, "Merkez", "Niğde", 37.9667, 34.6833),
                district(905102, "Bor", "Niğde", 37.9000, 34.5667),
                district(905103, "Çiftlik", "Niğde", 38.1667, 34.5000),
                district(905104, "Ulukışla", "Niğde", 37.5500, 34.4833),
                district(905105, "Altunhisar", "Niğde", 37.9833, 34.3667),
                district(905106, "Çamardı", "Niğde", 37.8333, 35.0000)
            ),
            // 52 ORDU (302824)
            302824L to listOf(
                district(905201, "Altınordu", "Ordu", 40.9839, 37.8764),
                district(905202, "Ünye", "Ordu", 41.1333, 37.2833),
                district(905203, "Fatsa", "Ordu", 41.0333, 37.5000),
                district(905204, "Gölköy", "Ordu", 40.6833, 37.6167),
                district(905205, "Korgan", "Ordu", 40.8333, 37.3500),
                district(905206, "Kumru", "Ordu", 40.8833, 37.2667),
                district(905207, "Perşembe", "Ordu", 41.0667, 37.7667),
                district(905208, "Aybastı", "Ordu", 40.6833, 37.4000),
                district(905209, "Akkuş", "Ordu", 40.8000, 37.0167),
                district(905210, "Ulubey", "Ordu", 40.8833, 37.7500),
                district(905211, "Mesudiye", "Ordu", 40.4667, 37.7667),
                district(905212, "Gürgentepe", "Ordu", 40.7833, 37.6000)
            ),
            // 53 RİZE (301550)
            301550L to listOf(
                district(905301, "Merkez", "Rize", 41.0201, 40.5234),
                district(905302, "Çayeli", "Rize", 41.0833, 40.7333),
                district(905303, "Ardeşen", "Rize", 41.1833, 40.9833),
                district(905304, "Pazar", "Rize", 41.1833, 40.8833),
                district(905305, "Fındıklı", "Rize", 41.2667, 41.1333),
                district(905306, "Güneysu", "Rize", 40.9833, 40.6000),
                district(905307, "Kalkandere", "Rize", 40.9333, 40.4333),
                district(905308, "İyidere", "Rize", 41.0167, 40.3500),
                district(905309, "Derepazarı", "Rize", 41.0167, 40.4167),
                district(905310, "Çamlıhemşin", "Rize", 41.0500, 41.0000),
                district(905311, "İkizdere", "Rize", 40.7833, 40.5500),
                district(905312, "Hemşin", "Rize", 41.0667, 40.9000)
            ),
            // 54 SAKARYA (301364)
            301364L to listOf(
                district(905401, "Adapazarı", "Sakarya", 40.7569, 30.3783),
                district(905402, "Serdivan", "Sakarya", 40.7617, 30.3600),
                district(905403, "Akyazı", "Sakarya", 40.6833, 30.6333),
                district(905404, "Erenler", "Sakarya", 40.7500, 30.4000),
                district(905405, "Hendek", "Sakarya", 40.8000, 30.7500),
                district(905406, "Karasu", "Sakarya", 41.0667, 30.7000),
                district(905407, "Geyve", "Sakarya", 40.5000, 30.3000),
                district(905408, "Arifiye", "Sakarya", 40.7167, 30.3667),
                district(905409, "Sapanca", "Sakarya", 40.6833, 30.2667),
                district(905410, "Pamukova", "Sakarya", 40.5000, 30.1667),
                district(905411, "Ferizli", "Sakarya", 40.9500, 30.5000),
                district(905412, "Kaynarca", "Sakarya", 41.0333, 30.3000),
                district(905413, "Kocaali", "Sakarya", 41.0500, 30.8667),
                district(905414, "Söğütlü", "Sakarya", 40.9167, 30.4833),
                district(905415, "Karapürçek", "Sakarya", 40.6500, 30.5500),
                district(905416, "Taraklı", "Sakarya", 40.4000, 30.4667)
            ),
            // 55 SAMSUN (740264)
            740264L to listOf(
                district(905501, "İlkadım", "Samsun", 41.2928, 36.3322),
                district(905502, "Atakum", "Samsun", 41.3239, 36.2558),
                district(905503, "Canik", "Samsun", 41.2647, 36.3747),
                district(905504, "Bafra", "Samsun", 41.5664, 35.9108),
                district(905505, "Çarşamba", "Samsun", 41.2006, 36.7228),
                district(905506, "Tekkeköy", "Samsun", 41.2028, 36.4672),
                district(905507, "Vezirköprü", "Samsun", 41.1367, 35.4542),
                district(905508, "Terme", "Samsun", 41.2058, 36.9822),
                district(905509, "Havza", "Samsun", 40.9689, 35.6658),
                district(905510, "Alaçam", "Samsun", 41.6253, 35.5978),
                district(905511, "19 Mayıs", "Samsun", 41.5000, 36.0833),
                district(905512, "Kavak", "Samsun", 41.0833, 36.0333),
                district(905513, "Salıpazarı", "Samsun", 41.0639, 36.5897),
                district(905514, "Asarcık", "Samsun", 41.0333, 36.2333),
                district(905515, "Ladik", "Samsun", 40.9261, 35.8933),
                district(905516, "Yakakent", "Samsun", 41.6333, 35.5333),
                district(905517, "Ayvacık", "Samsun", 41.4600, 36.6358)
            ),
            // 56 SİİRT (300627)
            300627L to listOf(
                district(905601, "Merkez", "Siirt", 37.9333, 41.9500),
                district(905602, "Kurtalan", "Siirt", 37.9333, 41.7000),
                district(905603, "Pervari", "Siirt", 37.9333, 42.5500),
                district(905604, "Baykan", "Siirt", 38.1667, 41.7833),
                district(905605, "Şirvan", "Siirt", 38.0667, 42.0333),
                district(905606, "Eruh", "Siirt", 37.7500, 42.1833)
            ),
            // 57 SİNOP (300552)
            300552L to listOf(
                district(905701, "Merkez", "Sinop", 42.0231, 35.1531),
                district(905702, "Boyabat", "Sinop", 41.4667, 34.7667),
                district(905703, "Gerze", "Sinop", 41.8000, 35.2000),
                district(905704, "Ayancık", "Sinop", 41.9500, 34.5833),
                district(905705, "Durağan", "Sinop", 41.4167, 35.0500),
                district(905706, "Türkeli", "Sinop", 41.9500, 34.3333),
                district(905707, "Erfelek", "Sinop", 41.8833, 34.9167)
            ),
            // 58 SİVAS (300489)
            300489L to listOf(
                district(905801, "Merkez", "Sivas", 39.7477, 37.0179),
                district(905802, "Şarkışla", "Sivas", 39.3500, 36.4167),
                district(905803, "Yıldızeli", "Sivas", 39.8667, 36.6500),
                district(905804, "Suşehri", "Sivas", 40.1667, 38.0833),
                district(905805, "Zara", "Sivas", 39.9000, 37.7500),
                district(905806, "Gemerek", "Sivas", 39.1833, 36.0833),
                district(905807, "Kangal", "Sivas", 39.2333, 37.2333),
                district(905808, "Gürün", "Sivas", 38.7167, 37.2667),
                district(905809, "Divriği", "Sivas", 39.3667, 38.1167),
                district(905810, "Koyulhisar", "Sivas", 40.3000, 37.8333),
                district(905811, "Altınyayla", "Sivas", 39.2667, 36.7500),
                district(905812, "Hafik", "Sivas", 39.8667, 37.3833),
                district(905813, "İmranlı", "Sivas", 39.8833, 38.1167)
            ),
            // 59 TEKİRDAĞ (299564)
            299564L to listOf(
                district(905901, "Süleymanpaşa", "Tekirdağ", 40.9833, 27.5167),
                district(905902, "Çorlu", "Tekirdağ", 41.1589, 27.7997),
                district(905903, "Çerkezköy", "Tekirdağ", 41.2861, 28.0006),
                district(905904, "Kapaklı", "Tekirdağ", 41.3167, 27.9833),
                district(905905, "Ergene", "Tekirdağ", 41.2500, 27.7000),
                district(905906, "Malkara", "Tekirdağ", 40.8833, 26.9000),
                district(905907, "Saray", "Tekirdağ", 41.4500, 27.9167),
                district(905908, "Hayrabolu", "Tekirdağ", 41.2167, 27.1167),
                district(905909, "Şarköy", "Tekirdağ", 40.6167, 27.1167),
                district(905910, "Muratlı", "Tekirdağ", 41.1667, 27.5000),
                district(905911, "Marmaraereğlisi", "Tekirdağ", 40.9667, 27.9500)
            ),
            // 60 TOKAT (298970)
            298970L to listOf(
                district(906001, "Merkez", "Tokat", 40.3167, 36.5500),
                district(906002, "Erbaa", "Tokat", 40.6667, 36.5667),
                district(906003, "Turhal", "Tokat", 40.3833, 36.0833),
                district(906004, "Niksar", "Tokat", 40.5833, 36.9500),
                district(906005, "Zile", "Tokat", 40.3000, 35.8833),
                district(906006, "Reşadiye", "Tokat", 40.3833, 37.3333),
                district(906007, "Almus", "Tokat", 40.3667, 36.9000),
                district(906008, "Pazar", "Tokat", 40.2667, 36.2833)
            ),
            // 61 TRABZON (738648)
            738648L to listOf(
                district(906101, "Ortahisar", "Trabzon", 41.0050, 39.7269),
                district(906102, "Akçaabat", "Trabzon", 40.9978, 39.5639),
                district(906103, "Araklı", "Trabzon", 40.9506, 40.0719),
                district(906104, "Of", "Trabzon", 40.9517, 40.2644),
                district(906105, "Yomra", "Trabzon", 40.9675, 39.8469),
                district(906106, "Arsin", "Trabzon", 40.9633, 39.9200),
                district(906107, "Vakfıkebir", "Trabzon", 41.0156, 39.2747),
                district(906108, "Sürmene", "Trabzon", 40.9072, 40.1156),
                district(906109, "Maçka", "Trabzon", 40.8133, 39.6133),
                district(906110, "Beşikdüzü", "Trabzon", 41.0589, 39.2283),
                district(906111, "Çaykara", "Trabzon", 40.7525, 40.3033),
                district(906112, "Tonya", "Trabzon", 40.8767, 39.2967),
                district(906113, "Düzköy", "Trabzon", 40.8833, 39.4333),
                district(906114, "Şalpazarı", "Trabzon", 40.8692, 39.1767),
                district(906115, "Hayrat", "Trabzon", 40.8681, 40.4019),
                district(906116, "Köprübaşı", "Trabzon", 40.7619, 39.1919),
                district(906117, "Dernekpazarı", "Trabzon", 40.7711, 40.2883)
            ),
            // 62 TUNCELİ (298797)
            298797L to listOf(
                district(906201, "Merkez", "Tunceli", 39.1079, 39.5401),
                district(906202, "Pertek", "Tunceli", 38.8667, 39.3167),
                district(906203, "Mazgirt", "Tunceli", 39.0167, 39.6000),
                district(906204, "Çemişgezek", "Tunceli", 39.0667, 38.9167),
                district(906205, "Hozat", "Tunceli", 39.1000, 39.2167),
                district(906206, "Ovacık", "Tunceli", 39.3667, 39.2167),
                district(906207, "Nazımiye", "Tunceli", 39.1833, 39.8000),
                district(906208, "Pülümür", "Tunceli", 39.4833, 39.9000)
            ),
            // 63 ŞANLIURFA (298333)
            298333L to listOf(
                district(906301, "Haliliye", "Şanlıurfa", 37.1671, 38.7939),
                district(906302, "Eyyübiye", "Şanlıurfa", 37.1500, 38.7833),
                district(906303, "Karaköprü", "Şanlıurfa", 37.2000, 38.8000),
                district(906304, "Siverek", "Şanlıurfa", 37.7500, 39.3167),
                district(906305, "Viranşehir", "Şanlıurfa", 37.2333, 39.7667),
                district(906306, "Birecik", "Şanlıurfa", 37.0333, 37.9833),
                district(906307, "Suruç", "Şanlıurfa", 36.9833, 38.4167),
                district(906308, "Akçakale", "Şanlıurfa", 36.7167, 38.9500),
                district(906309, "Ceylanpınar", "Şanlıurfa", 36.8333, 40.0333),
                district(906310, "Harran", "Şanlıurfa", 36.8667, 39.0333),
                district(906311, "Bozova", "Şanlıurfa", 37.3667, 38.5333),
                district(906312, "Hilvan", "Şanlıurfa", 37.5833, 38.9500),
                district(906313, "Halfeti", "Şanlıurfa", 37.2500, 37.8667)
            ),
            // 64 UŞAK (297880)
            297880L to listOf(
                district(906401, "Merkez", "Uşak", 38.6823, 29.4082),
                district(906402, "Banaz", "Uşak", 38.7333, 29.7500),
                district(906403, "Eşme", "Uşak", 38.4000, 28.9667),
                district(906404, "Sivaslı", "Uşak", 38.5000, 29.6833),
                district(906405, "Ulubey", "Uşak", 38.4167, 29.2833),
                district(906406, "Karahallı", "Uşak", 38.3167, 29.5333)
            ),
            // 65 VAN (298117)
            298117L to listOf(
                district(906501, "İpekyolu", "Van", 38.4946, 43.3832),
                district(906502, "Tuşba", "Van", 38.5122, 43.4219),
                district(906503, "Edremit", "Van", 38.3908, 43.2922),
                district(906504, "Erciş", "Van", 39.0081, 43.3561),
                district(906505, "Özalp", "Van", 38.6600, 44.0733),
                district(906506, "Çaldıran", "Van", 39.1400, 43.9167),
                district(906507, "Başkale", "Van", 38.0442, 44.0328),
                district(906508, "Muradiye", "Van", 38.9894, 43.7594),
                district(906509, "Gürpınar", "Van", 38.3200, 43.4033),
                district(906510, "Gevaş", "Van", 38.2933, 43.1100),
                district(906511, "Saray", "Van", 38.4817, 44.1400),
                district(906512, "Çatak", "Van", 38.0217, 43.0417),
                district(906513, "Bahçesaray", "Van", 38.0833, 42.7833)
            ),
            // 66 YOZGAT (297298)
            297298L to listOf(
                district(906601, "Merkez", "Yozgat", 39.8181, 34.8147),
                district(906602, "Sorgun", "Yozgat", 39.8167, 35.1833),
                district(906603, "Akdağmadeni", "Yozgat", 39.6667, 35.8833),
                district(906604, "Yerköy", "Yozgat", 39.6333, 34.4667),
                district(906605, "Boğazlıyan", "Yozgat", 39.1833, 35.2500),
                district(906606, "Saraykent", "Yozgat", 39.7000, 35.5000),
                district(906607, "Çayıralan", "Yozgat", 39.3000, 35.6333),
                district(906608, "Şefaatli", "Yozgat", 39.5000, 34.7500)
            ),
            // 67 ZONGULDAK (297126)
            297126L to listOf(
                district(906701, "Merkez", "Zonguldak", 41.4564, 31.7987),
                district(906702, "Karadeniz Ereğli", "Zonguldak", 41.2833, 31.4167),
                district(906703, "Çaycuma", "Zonguldak", 41.4167, 32.0833),
                district(906704, "Devrek", "Zonguldak", 41.2167, 31.9500),
                district(906705, "Kozlu", "Zonguldak", 41.4333, 31.7500),
                district(906706, "Kilimli", "Zonguldak", 41.4833, 31.8500),
                district(906707, "Alaplı", "Zonguldak", 41.1833, 31.3833),
                district(906708, "Gökçebey", "Zonguldak", 41.3167, 32.1500)
            ),
            // 68 AKSARAY (324908)
            324908L to listOf(
                district(906801, "Merkez", "Aksaray", 38.3687, 34.0370),
                district(906802, "Ortaköy", "Aksaray", 38.7333, 33.9333),
                district(906803, "Eskil", "Aksaray", 38.5667, 33.4167),
                district(906804, "Gülağaç", "Aksaray", 38.4000, 34.3333),
                district(906805, "Güzelyurt", "Aksaray", 38.2833, 34.3667),
                district(906806, "Ağaçören", "Aksaray", 38.8667, 33.9167),
                district(906807, "Sultanhanı", "Aksaray", 38.2500, 33.5500)
            ),
            // 69 BAYBURT (321852)
            321852L to listOf(
                district(906901, "Merkez", "Bayburt", 40.2552, 40.2249),
                district(906902, "Demirözü", "Bayburt", 40.1667, 39.8833),
                district(906903, "Aydıntepe", "Bayburt", 40.3833, 40.1500)
            ),
            // 70 KARAMAN (310022)
            310022L to listOf(
                district(907001, "Merkez", "Karaman", 37.1759, 33.2287),
                district(907002, "Ermenek", "Karaman", 36.6333, 32.8333),
                district(907003, "Sarıveliler", "Karaman", 36.7000, 32.6167),
                district(907004, "Ayrancı", "Karaman", 37.3667, 33.7000),
                district(907005, "Kazımkarabekir", "Karaman", 37.2333, 32.9667),
                district(907006, "Başyayla", "Karaman", 36.7500, 32.6833)
            ),
            // 71 KIRIKKALE (307901)
            307901L to listOf(
                district(907101, "Merkez", "Kırıkkale", 39.8468, 33.5153),
                district(907102, "Yahşihan", "Kırıkkale", 39.8333, 33.4500),
                district(907103, "Keskin", "Kırıkkale", 39.6667, 33.6167),
                district(907104, "Delice", "Kırıkkale", 39.9500, 34.0333),
                district(907105, "Bahşılı", "Kırıkkale", 39.8000, 33.4333),
                district(907106, "Sulakyurt", "Kırıkkale", 40.1667, 33.7167),
                district(907107, "Balışeyh", "Kırıkkale", 39.9167, 33.7167)
            ),
            // 72 BATMAN (321949)
            321949L to listOf(
                district(907201, "Merkez", "Batman", 37.8812, 41.1294),
                district(907202, "Kozluk", "Batman", 38.1833, 41.4833),
                district(907203, "Sason", "Batman", 38.3333, 41.4167),
                district(907204, "Beşiri", "Batman", 37.9167, 41.2833),
                district(907205, "Gercüş", "Batman", 37.5667, 41.3333),
                district(907206, "Hasankeyf", "Batman", 37.7167, 41.4167)
            ),
            // 73 ŞIRNAK (300305)
            300305L to listOf(
                district(907301, "Merkez", "Şırnak", 37.5164, 42.4594),
                district(907302, "Cizre", "Şırnak", 37.3333, 42.1833),
                district(907303, "Silopi", "Şırnak", 37.2500, 42.4667),
                district(907304, "İdil", "Şırnak", 37.3500, 41.8833),
                district(907305, "Uludere", "Şırnak", 37.4500, 42.8667),
                district(907306, "Beytüşşebap", "Şırnak", 37.5667, 43.1667),
                district(907307, "Güçlükonak", "Şırnak", 37.4833, 41.9833)
            ),
            // 74 BARTIN (322301)
            322301L to listOf(
                district(907401, "Merkez", "Bartın", 41.6344, 32.3375),
                district(907402, "Amasra", "Bartın", 41.7500, 32.3833),
                district(907403, "Ulus", "Bartın", 41.5833, 32.6333),
                district(907404, "Kurucaşile", "Bartın", 41.8500, 32.7167)
            ),
            // 75 ARDAHAN (323676)
            323676L to listOf(
                district(907501, "Merkez", "Ardahan", 41.1105, 42.7022),
                district(907502, "Göle", "Ardahan", 40.7833, 42.5667),
                district(907503, "Çıldır", "Ardahan", 41.1333, 43.1333),
                district(907504, "Hanak", "Ardahan", 41.2333, 42.8333),
                district(907505, "Posof", "Ardahan", 41.5167, 42.7333),
                district(907506, "Damal", "Ardahan", 41.2833, 42.8333)
            ),
            // 76 IĞDIR (312067)
            312067L to listOf(
                district(907601, "Merkez", "Iğdır", 39.9196, 44.0450),
                district(907602, "Tuzluca", "Iğdır", 40.0333, 43.6667),
                district(907603, "Aralık", "Iğdır", 39.8667, 44.5167),
                district(907604, "Karakoyunlu", "Iğdır", 39.9667, 44.1667)
            ),
            // 77 YALOVA (297424)
            297424L to listOf(
                district(907701, "Merkez", "Yalova", 40.6550, 29.2769),
                district(907702, "Çiftlikköy", "Yalova", 40.6667, 29.3500),
                district(907703, "Çınarcık", "Yalova", 40.6333, 29.1167),
                district(907704, "Altınova", "Yalova", 40.7000, 29.5000),
                district(907705, "Armutlu", "Yalova", 40.5167, 28.8333),
                district(907706, "Termal", "Yalova", 40.6167, 29.1833)
            ),
            // 78 KARABÜK (310188)
            310188L to listOf(
                district(907801, "Merkez", "Karabük", 41.2061, 32.6204),
                district(907802, "Safranbolu", "Karabük", 41.2500, 32.6833),
                district(907803, "Yenice", "Karabük", 41.2000, 32.3333),
                district(907804, "Eskipazar", "Karabük", 40.9500, 32.5333),
                district(907805, "Eflani", "Karabük", 41.4167, 32.9500),
                district(907806, "Ovacık", "Karabük", 41.1000, 32.8333)
            ),
            // 79 KİLİS (308064)
            308064L to listOf(
                district(907901, "Merkez", "Kilis", 36.7184, 37.1212),
                district(907902, "Musabeyli", "Kilis", 36.8833, 36.9167),
                district(907903, "Elbeyli", "Kilis", 36.6667, 37.4667),
                district(907904, "Polateli", "Kilis", 36.8333, 37.0333)
            ),
            // 80 OSMANİYE (302720)
            302720L to listOf(
                district(908001, "Merkez", "Osmaniye", 37.0742, 36.2478),
                district(908002, "Kadirli", "Osmaniye", 37.3667, 36.1000),
                district(908003, "Düziçi", "Osmaniye", 37.2333, 36.4500),
                district(908004, "Bahçe", "Osmaniye", 37.2000, 36.5833),
                district(908005, "Toprakkale", "Osmaniye", 37.0667, 36.1500),
                district(908006, "Sumbas", "Osmaniye", 37.4667, 36.0333),
                district(908007, "Hasanbeyli", "Osmaniye", 37.1167, 36.5667)
            ),
            // 81 DÜZCE (315998)
            315998L to listOf(
                district(908101, "Merkez", "Düzce", 40.8438, 31.1565),
                district(908102, "Akçakoca", "Düzce", 41.0833, 31.1167),
                district(908103, "Kaynaşlı", "Düzce", 40.7667, 31.3167),
                district(908104, "Gölyaka", "Düzce", 40.7500, 31.0333),
                district(908105, "Çilimli", "Düzce", 40.9000, 31.0500),
                district(908106, "Yığılca", "Düzce", 40.9667, 31.4500),
                district(908107, "Gümüşova", "Düzce", 40.8500, 30.9333),
                district(908108, "Cumayeri", "Düzce", 40.8833, 30.9833)
            )
        )
    }
}
