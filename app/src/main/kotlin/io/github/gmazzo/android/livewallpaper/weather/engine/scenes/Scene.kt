package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import android.content.Context
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.ThingManager
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

abstract class Scene protected constructor(mContext: Context, gl: GL11) {
    protected var mLandscape: Boolean = false
    protected val textures: Textures = Textures(mContext.resources, gl)
    protected val models: Models = Models(mContext.resources, gl)
    protected var mThingManager: ThingManager? = null

    abstract fun draw(gl10: GL10, globalTime: GlobalTime)

    abstract fun load(gl10: GL10?)

    abstract fun unload(gl10: GL10?)

    open fun precacheAssets(gl: GL10?) {
    }

    fun setScreenMode(lanscape: Boolean) {
        this.mLandscape = lanscape
    }

    open fun updateWeather(weather: WeatherType?) {
    }

    open fun update(globalTime: GlobalTime?) {
    }

    open fun updateTimeOfDay(tod: TimeOfDay) {
    }

    companion object {
        var sTextures: Textures? = null
        var sModels: Models? = null
    }
}
