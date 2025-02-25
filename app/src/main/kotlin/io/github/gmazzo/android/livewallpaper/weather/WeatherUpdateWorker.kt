package io.github.gmazzo.android.livewallpaper.weather

import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class WeatherUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val location: StateFlow<Location?>,
    private val weather: MutableStateFlow<WeatherType>,
    private val forecastAPI: LocationForecastAPI,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.i(TAG, "Updating weather conditions")
        val location = location.firstOrNull() ?: return Result.retry()

        val response =
            withContext(Dispatchers.IO) {
                forecastAPI.getForecast(location.latitude, location.longitude, null)
            }

        val series = response.properties.timeSeries.firstOrNull() ?: return Result.failure()
        weather.value = sequenceOf(
            series.data.nextHour,
            series.data.next6Hours,
            series.data.next12Hours
        ).filterNotNull().first().weatherType
        Log.i(TAG, "Weather conditions updated: ${weather.value}")
        return Result.success()
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
