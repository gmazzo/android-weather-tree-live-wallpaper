package io.github.gmazzo.android.livewallpaper.weather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.getSystemService
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.api.LocationForecastAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import android.location.Location as AndroidLocation

@HiltWorker
class WeatherUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val location: MutableStateFlow<Location>,
    private val weather: MutableStateFlow<WeatherType>,
    private val forecastAPI: LocationForecastAPI,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val location = applicationContext.location ?: return Result.retry()
        this.location.value = Location(location.latitude, location.longitude)

        val response =
            withContext(Dispatchers.IO) {
                forecastAPI.getForecast(location.latitude, location.longitude, null)
            }

        val series = response.properties.timeSeries.firstOrNull() ?: return Result.failure()
        weather.value = series.data.nextHour.weatherType
        return Result.success()
    }

    private val Context.location: AndroidLocation?
        get() {
            if (checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                Log.e(TAG, "Missing $ACCESS_COARSE_LOCATION to access location")
                return null
            }

            val manager: LocationManager = getSystemService() ?: return null
            return manager.allProviders.asSequence()
                .mapNotNull(manager::getLastKnownLocation)
                .firstOrNull()
                ?.also { Log.i(TAG, "LastKnownLocation: lat=${it.latitude}, lng=${it.longitude}") }
        }

    companion object {
        private const val TAG = "weatherUpdate"

        fun WorkManager.enableWeatherConditionsUpdate() {
            enqueueUniquePeriodicWork(
                TAG,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                PeriodicWorkRequestBuilder<WeatherUpdateWorker>(
                    1,
                    TimeUnit.HOURS,
                    8,
                    TimeUnit.HOURS
                ).setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .build()
                ).build()
            )
        }

        fun WorkManager.disableWeatherConditionsUpdate() {
            cancelAllWorkByTag(TAG)
        }

    }

}
