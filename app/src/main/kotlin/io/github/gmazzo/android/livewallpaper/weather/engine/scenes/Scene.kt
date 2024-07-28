package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import androidx.annotation.CallSuper
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.AnimPlayer
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.ThingManager
import io.github.gmazzo.android.livewallpaper.weather.engine.models.AnimatedModel
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

abstract class Scene(
    protected val gl: GL11,
    protected val models: Models,
    protected val textures: Textures,
) {
    var landscape: Boolean = false
    protected val thingManager = ThingManager()
    protected var bgPadding: Float = 20.0f
    private var treeAnimateDelayMin: Float = 3.0f
    private var treeAnimateDelayRange: Float = 7.0f
    private var globalTime: GlobalTime? = null
    protected var numClouds: Int = 0
    protected var numWisps: Int = 0
    lateinit var todEngineColors: Array<EngineColor>
    private var treeAnim: Boolean = true
    protected var reloadAssets: Boolean = false
    private var treesAnim = AnimPlayer(0, 19, 5.0f, false)
    private var treesAnimateDelay = 5f

    abstract fun draw(time: GlobalTime)

    abstract fun load()

    @CallSuper
    open fun unload() {
        thingManager.clear()
    }

    open fun precacheAssets() {
    }

    open fun updateWeather(weather: WeatherType) {
    }

    fun numCloudsFromPrefs(weather: WeatherType) {
        this.numClouds = weather.clouds
        this.numWisps = weather.wisps
    }

    fun windSpeedFromPrefs() {
        pref_windSpeed = 3 * 0.5f
    }

    fun update(time: GlobalTime) {
        this.globalTime = time
    }

    open fun updateTimeOfDay(tod: TimeOfDay) {
        val iMain = tod.mainIndex
        val iBlend = tod.blendIndex
        todEngineColorFinal!!.blend(
            todEngineColors[iMain],
            todEngineColors[iBlend], tod.blendAmount
        )
        todSunPosition = tod.sunPosition
    }

    protected fun drawTree(timeDelta: Float) {
        if (this.treeAnim && treesAnim.count > 0) {
            this.treesAnimateDelay -= timeDelta
            if (this.treesAnimateDelay <= 0.0f) {
                this.treesAnimateDelay =
                    this.treeAnimateDelayMin + (this.treeAnimateDelayRange * Random.nextFloat())
                treesAnim.reset()
            }
        }

        val treeTerrain = models[R.raw.trees_overlay_terrain]
        val treesOverlay = textures[R.drawable.trees_overlay]
        gl.glBindTexture(GL10.GL_TEXTURE_2D, treesOverlay.glId)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPushMatrix()
        if (this.landscape) {
            gl.glTranslatef(2.0f, 70.0f, -65.0f)
        } else {
            gl.glTranslatef(-8.0f, 70.0f, -70.0f)
        }
        gl.glScalef(5.0f, 5.0f, 5.0f)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        treeTerrain.render()

        val grass = models[R.raw.grass_overlay] as AnimatedModel
        val tree = models[R.raw.trees_overlay] as AnimatedModel
        grass.animator = treesAnim
        tree.animator = treesAnim
        treesAnim.update(timeDelta)
        tree.render()
        grass.render()

        gl.glPopMatrix()
    }

    companion object {
        var pref_windSpeed: Float = 3.0f
        var todEngineColorFinal: EngineColor? = null
        var todSunPosition: Float = 0.0f
    }
}
