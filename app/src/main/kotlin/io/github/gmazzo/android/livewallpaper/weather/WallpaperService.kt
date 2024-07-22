package io.github.gmazzo.android.livewallpaper.weather

import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WallpaperService : GLWallpaperService() {

    @Inject
    internal lateinit var weatherState: StateFlow<WeatherState>

    override fun onCreateEngine() = WeatherWallpaperEngine()

    inner class WeatherWallpaperEngine : GLEngine() {

        private var job: Job? = null

        // TODO once all redering is refactored, go back to `onCreate` and `onDestroy` with `SupervisorJob`
        //  https://stackoverflow.com/a/63407811
        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (!visible) {
                job?.cancel()
                job = null

            } else if (job == null) {
                job = MainScope().launch {
                    weatherState.collectLatest(::updateWeatherState)
                }
            }
        }

        private fun updateWeatherState(state: WeatherState) {
            Log.i("WeatherWallpaperEngine", "updateWeatherState")

            renderSurfaceView!!.updateWeatherType(state.weatherCondition)
        }

    }

}
