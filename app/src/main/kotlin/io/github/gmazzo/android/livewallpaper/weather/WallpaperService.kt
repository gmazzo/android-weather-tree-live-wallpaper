package io.github.gmazzo.android.livewallpaper.weather

import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
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
    internal lateinit var weatherConditions: MutableStateFlow<WeatherConditions>

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
                    weatherConditions.collectLatest(::updateWeatherState)
                }
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            renderSurfaceView!!.mBaseRenderer.renderer.weatherConditions = weatherConditions
        }

        private fun updateWeatherState(state: WeatherConditions) {
            Log.i("WeatherWallpaperEngine", "updateWeatherState")

            renderSurfaceView!!.updateWeatherType(state.weatherType)
        }

        override fun onTouchEvent(event: MotionEvent) {
            if (renderSurfaceView!!.isDemoMode && event.action == MotionEvent.ACTION_DOWN) {
                val current = weatherConditions.value.weatherType.scene
                val scenes: List<SceneMode> = SceneMode.entries
                val next = scenes[(scenes.indexOf(current) + 1) % scenes.size]
                var nextWeather = WeatherType.SUNNY_DAY
                for (weather in WeatherType.entries) {
                    if (weather.scene == next) {
                        nextWeather = weather
                        break
                    }
                }

                weatherConditions.update {
                    it.copy(weatherType = nextWeather)
                }
            }
            super.onTouchEvent(event)
        }

    }

}
