package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.getSystemService
import androidx.core.location.LocationListenerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.hours
import android.location.Location as AndroidLocation

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationListenerCompat {

    val flow = MutableStateFlow<Location?>(null)

    private val coroutineScope = MainScope()

    private val manager: LocationManager? = context.getSystemService()

    @Inject
    fun retrieveLocations() {
        coroutineScope.launch(Dispatchers.Main) {
            flow.subscriptionCount.fold(0) { prev, actual ->
                Log.d(TAG, "Subscription count: prev=$prev, actual=$actual")

                if (prev == 0 && actual > 0) {
                    if (!startListening()) return@fold 0

                } else if (prev > 0 && actual == 0) {
                    stopListening()
                }
                return@fold actual
            }
        }
    }

    private fun startListening(): Boolean {
        val manager = this.manager ?: return false

        if (checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            Log.e(TAG, "Missing $ACCESS_COARSE_LOCATION to access location")
            return false
        }

        Log.i(TAG, "Started listening for location updates")
        manager.getLastKnownLocation(NETWORK_PROVIDER)?.let(::onLocationChanged)
        manager.requestLocationUpdates(NETWORK_PROVIDER, 1.hours.inWholeMilliseconds, 1000f, this)
        return true
    }

    private fun stopListening() {
        Log.i(TAG, "Stopped listening for location updates")
        manager?.removeUpdates(this)
    }

    override fun onLocationChanged(location: AndroidLocation) {
        Log.d(TAG, "Location changed: $location")
        flow.value = Location(
            latitude = location.latitude,
            longitude = location.longitude,
        )
    }

    companion object {
        private const val TAG = "LocationManager"
    }

}
