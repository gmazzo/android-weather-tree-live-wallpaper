package io.github.gmazzo.android.livewallpaper.weather

import android.view.SurfaceHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@AndroidEntryPoint
class WallpaperService : GLWallpaperService() {

    @Inject
    internal lateinit var weatherConditions: MutableStateFlow<WeatherConditions>

    override fun onCreateEngine() = WeatherWallpaperEngine()

    inner class WeatherWallpaperEngine : GLEngine() {

        // TODO once all redering is refactored, go back to `onCreate` and `onDestroy` with `SupervisorJob`
        //  https://stackoverflow.com/a/63407811
        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                renderSurfaceView!!.updateWeather(weatherConditions.value)
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            renderSurfaceView!!.updateWeather(weatherConditions.value)
        }

    }

}
