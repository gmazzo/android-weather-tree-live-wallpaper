package io.github.gmazzo.android.livewallpaper.weather

import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.WeatherInfoManager
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.WeatherInfoManager.WeatherStateReceiver

@AndroidEntryPoint
class WallpaperService : GLWallpaperService() {
    var mWeatherInfo: WeatherInfoManager? = null

    override fun onCreateEngine() = WeatherWallpaperEngine()

    inner class WeatherWallpaperEngine : GLEngine(), WeatherStateReceiver {
        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                this@WallpaperService.mWeatherInfo = WeatherInfoManager.getWeatherInfo(
                    this@WallpaperService, this
                )
                mWeatherInfo!!.update(1000)
            } else if (this@WallpaperService.mWeatherInfo != null) {
                mWeatherInfo!!.onStop()
            }
            super.onVisibilityChanged(visible)
        }

        @Synchronized
        override fun updateWeatherState() {
            Log.i("HM", "updateWeatherState")
            val weather = this@WallpaperService.weatherConditions

            renderSurfaceView!!.updateWeatherType(weather)
        }

    }

}
