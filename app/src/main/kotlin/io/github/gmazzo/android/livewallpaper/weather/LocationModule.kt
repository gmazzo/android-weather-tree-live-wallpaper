package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import androidx.core.content.ContextCompat.checkSelfPermission as checkSelfPermissionCompat

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    private const val TAG = "LocationModule"

    @Provides
    @Named("hasLocationPermission")
    fun hasLocationPermission(@ApplicationContext context: Context): Boolean =
        checkSelfPermissionCompat(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    @Provides
    fun provideLocation(@ApplicationContext context: Context): Location? {
        if (!hasLocationPermission(context)) {
            Log.e(TAG, "Missing $ACCESS_COARSE_LOCATION to access location")
            return null
        }

        val manager: LocationManager = context.getSystemService() ?: return null
        return manager.allProviders.asSequence()
            .mapNotNull(manager::getLastKnownLocation)
            .firstOrNull()
            ?.also { Log.i(TAG, "LastKnownLocation: lat=${it.latitude}, lng=${it.longitude}") }
    }

}
