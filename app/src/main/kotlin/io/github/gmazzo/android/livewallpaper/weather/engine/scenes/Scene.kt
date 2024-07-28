package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import androidx.annotation.CallSuper
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.AnimPlayer
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.AnimatedModel
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

abstract class Scene(
    protected val gl: GL11,
    protected val models: Models,
    protected val textures: Textures,
    protected val things: Things,
    protected val timeOfDayColor: EngineColor,
    private val sunMoon: Boolean = false,
    raining: Boolean = false,
    private val darkClouds: Boolean? = false,
) {
    var landscape: Boolean = false
    protected var bgPadding: Float = 20f
    private var treeAnimateDelayMin: Float = 3f
    private var treeAnimateDelayRange: Float = 7f
    private var globalTime: GlobalTime? = null
    protected var numClouds: Int = 0
    protected var numWisps: Int = 0
    private var treeAnim: Boolean = true
    private var treesAnim = AnimPlayer(0, 19, 5f, false)
    private var treesAnimateDelay = 5f

    protected val timeOfDayColors = if (raining) arrayOf(
        EngineColor(.25f, .2f, .2f, 1f),
        EngineColor(.6f, .6f, .6f, 1f),
        EngineColor(.9f, .9f, .9f, 1f),
        EngineColor(.65f, .6f, .6f, 1f),

    ) else arrayOf(
        EngineColor(.5f, .5f, .75f, 1f),
        EngineColor(1f, .73f, .58f, 1f),
        EngineColor(1f, 1f, 1f, 1f),
        EngineColor(1f, .85f, .75f, 1f),
    )

    abstract fun draw(time: GlobalTime)

    fun load(weather: WeatherType) {
        timeOfDayColor.set(1f, 1f, 1f, 1f)

        if (sunMoon) {
            things.spawnSun()
            things.spawnMoon()
        }
        updateWeather(weather)
    }

    @CallSuper
    open fun unload() {
        things.clear()
    }

    @CallSuper
    open fun updateWeather(weather: WeatherType) {
        numClouds = weather.clouds
        numWisps = weather.wisps

        if (darkClouds != null) {
            things.spawnClouds(numClouds, numWisps, dark = darkClouds)
        }
    }

    fun update(time: GlobalTime) {
        this.globalTime = time
    }

    open fun updateTimeOfDay(tod: TimeOfDay) {
        timeOfDayColor.blend(
            timeOfDayColors[tod.mainIndex],
            timeOfDayColors[tod.blendIndex],
            tod.blendAmount
        )
    }

    protected fun drawTree(timeDelta: Float) {
        if (this.treeAnim && treesAnim.count > 0) {
            this.treesAnimateDelay -= timeDelta
            if (this.treesAnimateDelay <= 0f) {
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
            gl.glTranslatef(2f, 70f, -65f)
        } else {
            gl.glTranslatef(-8f, 70f, -70f)
        }
        gl.glScalef(5f, 5f, 5f)
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

}
