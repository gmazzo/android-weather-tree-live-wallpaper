@file:JvmName("SettingsUtils")

package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.content.ContextCompat

private fun Context.hasPermission(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED

val Context.hasLocationPermission
    get() = hasPermission(ACCESS_COARSE_LOCATION)

val Context.hasBackgroundLocationPermission
    get() =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) hasLocationPermission
        else hasPermission(ACCESS_BACKGROUND_LOCATION)