@file:JvmName("SettingsUtils")

package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.content.ContextCompat

private val Context.prefs
    get() = getSharedPreferences("settings", Context.MODE_PRIVATE)

var Context.weatherState: WeatherState
    get() = prefs.let { prefs ->
        WeatherState(
            latitude = prefs.getFloat("latitude", Float.NaN),
            longitude = prefs.getFloat("longitude", Float.NaN),
            weatherCondition = prefs.getString("weather", null)?.let(WeatherType::valueOf)
                ?: WeatherType.SUNNY_DAY
        )
    }
    set(value) = prefs.edit()
        .putFloat("latitude", value.latitude)
        .putFloat("longitude", value.longitude)
        .putString("weather", value.weatherCondition.name)
        .apply()

private fun Context.hasPermission(context: Context, permission: String) =
    ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED

val Context.hasLocationPermission
    get() = hasPermission(this, ACCESS_COARSE_LOCATION)

val Context.hasBackgroundLocationPermission
    get() =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) hasLocationPermission
        else hasPermission(this, ACCESS_BACKGROUND_LOCATION)
