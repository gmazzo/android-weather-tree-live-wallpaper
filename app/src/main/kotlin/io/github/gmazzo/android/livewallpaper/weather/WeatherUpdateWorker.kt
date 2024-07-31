package io.github.gmazzo.android.livewallpaper.weather

import android.content.Context
import android.location.Location
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.api.LocationForecastAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit
import javax.inject.Provider

@HiltWorker
class WeatherUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val state: MutableStateFlow<WeatherState>,
    private val location: Provider<Location>,
    private val forecastAPI: LocationForecastAPI,
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val location = location.get() ?: return Result.retry()

        val response = forecastAPI
            .getForecast(location.latitude.toFloat(), location.longitude.toFloat(), null)
            .execute().body() ?: return Result.retry()

        val series = response.properties.timeSeries.firstOrNull() ?: return Result.failure()
        state.update {
            it.copy(
                location = location,
                weatherType = series.data.nextHour.weatherType
            )
        }
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
