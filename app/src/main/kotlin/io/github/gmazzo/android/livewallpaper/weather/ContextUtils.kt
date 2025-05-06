@file:JvmName("SettingsUtils")

package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
import android.content.pm.PackageManager.DONT_KILL_APP
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.pm.PackageManager.SYNCHRONOUS
import android.os.Build
import androidx.core.content.ContextCompat
import io.github.gmazzo.android.livewallpaper.weather.settings.SettingsActivity

private fun Context.hasPermission(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED

val Context.hasLocationPermission
    get() = hasPermission(ACCESS_COARSE_LOCATION)

val Context.hasBackgroundLocationPermission
    get() =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) hasLocationPermission
        else hasPermission(ACCESS_BACKGROUND_LOCATION)

private val Context.launcherIconComponent
    get() = ComponentName(this, "$packageName.MainActivity")

var Context.launcherIconEnabled: Boolean
    get() = when (packageManager.getComponentEnabledSetting(launcherIconComponent)) {
        COMPONENT_ENABLED_STATE_DEFAULT,
        COMPONENT_ENABLED_STATE_ENABLED -> true

        else -> false
    }
    set(value) {
        packageManager.setComponentEnabledSetting(
            launcherIconComponent,
            if (value) COMPONENT_ENABLED_STATE_ENABLED else COMPONENT_ENABLED_STATE_DISABLED,
            DONT_KILL_APP,
        )
    }
