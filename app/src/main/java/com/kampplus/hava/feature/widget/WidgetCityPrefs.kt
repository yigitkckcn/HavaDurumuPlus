package com.kampplus.hava.feature.widget

import android.content.Context
import android.content.SharedPreferences
import com.kampplus.hava.feature.weather.data.local.TurkishCityCatalog
import com.kampplus.hava.feature.weather.domain.model.City

/**
 * Widget'ın göstereceği şehri SharedPreferences'ta saklar.
 * Her widget ID'si kendi şehrini ayrı tutar.
 */
object WidgetCityPrefs {

    private const val PREFS_NAME = "hava_widget_prefs"
    private const val KEY_CITY_ID_PREFIX = "widget_city_id_"
    private const val KEY_CITY_NAME_PREFIX = "widget_city_name_"
    private const val KEY_CITY_LAT_PREFIX = "widget_city_lat_"
    private const val KEY_CITY_LON_PREFIX = "widget_city_lon_"
    private const val KEY_CITY_REGION_PREFIX = "widget_city_region_"

    private fun prefs(context: Context): SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveCity(context: Context, appWidgetId: Int, city: City) {
        prefs(context).edit()
            .putLong(KEY_CITY_ID_PREFIX + appWidgetId, city.id)
            .putString(KEY_CITY_NAME_PREFIX + appWidgetId, city.name)
            .putFloat(KEY_CITY_LAT_PREFIX + appWidgetId, city.coordinates.latitude.toFloat())
            .putFloat(KEY_CITY_LON_PREFIX + appWidgetId, city.coordinates.longitude.toFloat())
            .putString(KEY_CITY_REGION_PREFIX + appWidgetId, city.region ?: "")
            .apply()
    }

    fun getCity(context: Context, appWidgetId: Int): City? {
        val p = prefs(context)
        val name = p.getString(KEY_CITY_NAME_PREFIX + appWidgetId, null) ?: return null
        val id = p.getLong(KEY_CITY_ID_PREFIX + appWidgetId, -1L)
        if (id == -1L) return null
        val lat = p.getFloat(KEY_CITY_LAT_PREFIX + appWidgetId, 0f).toDouble()
        val lon = p.getFloat(KEY_CITY_LON_PREFIX + appWidgetId, 0f).toDouble()
        val region = p.getString(KEY_CITY_REGION_PREFIX + appWidgetId, null)
        return City(
            id = id,
            name = name,
            region = region,
            country = "Türkiye",
            coordinates = com.kampplus.hava.feature.weather.domain.model.Coordinates(lat, lon)
        )
    }

    /** Widget silindiğinde temizle. */
    fun delete(context: Context, appWidgetId: Int) {
        prefs(context).edit()
            .remove(KEY_CITY_ID_PREFIX + appWidgetId)
            .remove(KEY_CITY_NAME_PREFIX + appWidgetId)
            .remove(KEY_CITY_LAT_PREFIX + appWidgetId)
            .remove(KEY_CITY_LON_PREFIX + appWidgetId)
            .remove(KEY_CITY_REGION_PREFIX + appWidgetId)
            .apply()
    }

    /** Kayıtlı şehir yoksa TurkishCityCatalog'un ilk şehrini döner. */
    fun getCityOrDefault(context: Context, appWidgetId: Int): City = getCity(context, appWidgetId) ?: TurkishCityCatalog().cities().first()
}
