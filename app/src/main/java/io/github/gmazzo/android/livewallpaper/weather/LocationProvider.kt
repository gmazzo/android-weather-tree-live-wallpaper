package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.getSystemService
import androidx.core.content.ContextCompat.checkSelfPermission as checkSelfPermissionCompat

object LocationProvider {
    private const val TAG = "LocationProvider"

    val Context.hasLocationPermission
        get() = checkSelfPermissionCompat(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    val Context.lastKnownLocation
        get(): Location? {
            if (!hasLocationPermission) {
                Log.e(TAG, "Missing $ACCESS_COARSE_LOCATION to access location")
                return null
            }

            val manager: LocationManager = getSystemService() ?: return null
            return manager.allProviders.asSequence()
                .mapNotNull(manager::getLastKnownLocation)
                .firstOrNull()
                ?.also { Log.i(TAG, "LastKnownLocation: lat=${it.latitude}, lng=${it.longitude}") }
        }

}
