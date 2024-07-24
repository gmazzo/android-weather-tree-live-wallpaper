package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures
import javax.microedition.khronos.opengles.GL10

abstract class Scene protected constructor(
    protected val models: Models,
    protected val textures: Textures,
) {
    var landscape: Boolean = false

    abstract fun draw(gl: GL10, time: GlobalTime)

    abstract fun load(gl: GL10)

    abstract fun unload(gl: GL10)

    open fun precacheAssets(gl: GL10) {
    }

    open fun updateWeather(weather: WeatherType) {
    }

    open fun update(time: GlobalTime) {
    }

    open fun updateTimeOfDay(tod: TimeOfDay) {
    }

}
