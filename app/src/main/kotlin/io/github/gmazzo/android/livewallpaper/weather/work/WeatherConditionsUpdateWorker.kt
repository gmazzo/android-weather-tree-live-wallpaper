package io.github.gmazzo.android.livewallpaper.weather.work

import android.content.Context
import android.location.Location
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.WeatherConditions
import io.github.gmazzo.android.livewallpaper.weather.api.LocationForecastAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Provider

@HiltWorker
class WeatherConditionsUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val weatherConditions: MutableStateFlow<WeatherConditions>,
    private val location: Provider<Location>,
    private val forecastAPI: LocationForecastAPI,
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val location = location.get() ?: return Result.retry()

        val response = forecastAPI
            .getForecast(location.latitude.toFloat(), location.longitude.toFloat(), null)
            .execute().body() ?: return Result.retry()

        val series = response.properties.timeSeries.firstOrNull() ?: return Result.failure()
        weatherConditions.update {
            it.copy(
                latitude = location.latitude.toFloat(),
                longitude = location.longitude.toFloat(),
                weatherType = series.data.nextHour.weatherType
            )
        }
        return Result.success()
    }

}
