package io.github.gmazzo.android.livewallpaper.weather.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.gmazzo.android.livewallpaper.weather.WeatherState
import io.github.gmazzo.android.livewallpaper.weather.api.LocationForecastAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@HiltWorker
class WeatherConditionsUpdateWorker @AssistedInject constructor(
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
