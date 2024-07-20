@file:JvmName("SettingsUtils")
package io.github.gmazzo.android.livewallpaper.weather

import android.content.Context

private val Context.prefs
    get() = getSharedPreferences("settings", Context.MODE_PRIVATE)

var Context.latitude: Float?
    get() = prefs.getFloat("latitude", Float.NaN).takeUnless { it.isNaN() }
    set(value) = prefs.edit().putFloat("latitude", value ?: Float.NaN).apply()

var Context.longitude: Float?
    get() = prefs.getFloat("longitude", Float.NaN).takeUnless { it.isNaN() }
    set(value) = prefs.edit().putFloat("longitude", value ?: Float.NaN).apply()

var Context.weatherConditions: WeatherType
    get() = prefs.getString("weather", null)?.let(WeatherType::valueOf) ?: WeatherType.SUNNY_DAY
    set(value) = prefs.edit().putString("weather", value.name).apply()
