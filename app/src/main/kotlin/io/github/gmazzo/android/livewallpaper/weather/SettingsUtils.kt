@file:JvmName("SettingsUtils")
package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat.checkSelfPermission

private val Context.prefs
    get() = getSharedPreferences("settings", Context.MODE_PRIVATE)

var Context.weatherState: WeatherState
    get() = prefs.let { prefs ->
        WeatherState(
            latitude = prefs.getFloat("latitude", Float.NaN),
            longitude = prefs.getFloat("longitude", Float.NaN),
            weatherCondition = prefs.getString("weather", null)?.let(WeatherType::valueOf) ?: WeatherType.SUNNY_DAY
        )
    }
    set(value) = prefs.edit()
        .putFloat("latitude", value.latitude)
        .putFloat("longitude", value.longitude)
        .putString("weather", value.weatherCondition.name)
        .apply()

val Context.hasLocationPermission
    get() = checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
