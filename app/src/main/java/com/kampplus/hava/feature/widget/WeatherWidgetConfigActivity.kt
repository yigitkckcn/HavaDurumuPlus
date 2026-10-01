package com.kampplus.hava.feature.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kampplus.hava.R
import com.kampplus.hava.feature.weather.data.local.TurkishCityCatalog
import com.kampplus.hava.feature.weather.domain.model.City

/**
 * Kullanıcı widget eklerken "Şehir seç" diyaloğu görevi gören Activity.
 * Android widget altyapısı, widget eklenirken varsa bu Activity'yi açar.
 */
class WeatherWidgetConfigActivity : Activity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Widget ekleme iptal edilirse RESULT_CANCELED dön
        setResult(RESULT_CANCELED)

        // Widget ID'sini intent'ten al
        appWidgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContentView(R.layout.activity_widget_config)

        val container = findViewById<LinearLayout>(R.id.widget_city_list)
        val cities = TurkishCityCatalog().cities()
        val inflater = LayoutInflater.from(this)

        cities.forEach { city ->
            val itemView = inflater.inflate(R.layout.item_widget_city, container, false)
            itemView.findViewById<TextView>(R.id.city_item_name).text = city.name
            itemView.findViewById<TextView>(R.id.city_item_region).text =
                listOfNotNull(city.region, city.country).distinct().joinToString(", ")
            itemView.setOnClickListener { onCitySelected(city) }
            container.addView(itemView)
        }
    }

    private fun onCitySelected(city: City) {
        // Seçimi kaydet
        WidgetCityPrefs.saveCity(this, appWidgetId, city)

        // Widget'ı hemen güncelle
        val appWidgetManager = AppWidgetManager.getInstance(this)
        WeatherWidgetProvider.updateSingleWidget(this, appWidgetManager, appWidgetId)

        // Başarılı sonuç döndür
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(RESULT_OK, resultValue)
        finish()
    }
}
