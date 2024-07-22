package io.github.gmazzo.android.livewallpaper.weather

import android.util.Log
import android.view.MotionEvent
import dagger.hilt.android.AndroidEntryPoint
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WallpaperService : GLWallpaperService() {

    @Inject
    internal lateinit var weatherState: MutableStateFlow<WeatherState>

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

        override fun onTouchEvent(event: MotionEvent) {
            if (renderSurfaceView!!.isDemoMode && event.action == MotionEvent.ACTION_DOWN) {
                val current = weatherState.value.weatherCondition.scene
                val scenes: List<SceneMode> = SceneMode.entries
                val next = scenes[(scenes.indexOf(current) + 1) % scenes.size]
                var nextWeather = WeatherType.SUNNY_DAY
                for (weather in WeatherType.entries) {
                    if (weather.scene == next) {
                        nextWeather = weather
                        break
                    }
                }

                weatherState.update {
                    it.copy(weatherCondition = nextWeather)
                }
            }
            super.onTouchEvent(event)
        }

    }

}
