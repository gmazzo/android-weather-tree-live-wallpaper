package io.github.gmazzo.android.livewallpaper.weather.sky_manager

import android.content.Context
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
import io.github.gmazzo.android.livewallpaper.weather.WeatherState
import io.github.gmazzo.android.livewallpaper.weather.api.LocationForecastAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherInfoManager @Inject constructor() {

    @Inject
    fun scheduleWeatherUpdate(context: Context) {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "weatherUpdate",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<WeatherUpdateWorker>(1, TimeUnit.HOURS, 8, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        )
    }

    @HiltWorker
    class WeatherUpdateWorker @AssistedInject constructor(
        @Assisted context: Context,
        @Assisted workerParams: WorkerParameters,
        private val weatherState: MutableStateFlow<WeatherState>,
        private val forecastAPI: LocationForecastAPI,
    ) : Worker(context, workerParams) {

        override fun doWork(): Result {
            val state = weatherState.value
            if (state.latitude.isNaN() || state.longitude.isNaN()) return Result.retry()

            val response = forecastAPI
                .getForecast(state.latitude, state.longitude, null)
                .execute().body() ?: return Result.retry()

            val series = response.properties.timeSeries.firstOrNull() ?: return Result.failure()
            weatherState.update {
                it.copy(weatherCondition = series.data.nextHour.weatherType)
            }
            return Result.success()
        }

    }

}
