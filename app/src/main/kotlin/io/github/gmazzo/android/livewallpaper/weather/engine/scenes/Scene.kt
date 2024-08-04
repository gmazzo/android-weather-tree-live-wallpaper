package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import androidx.annotation.CallSuper
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherState
import io.github.gmazzo.android.livewallpaper.weather.engine.AnimPlayer
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.AnimatedModel
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTint
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

sealed class Scene(
    protected val time: GlobalTime,
    protected val gl: GL11,
    protected val models: Models,
    protected val textures: Textures,
    protected val things: Things,
    protected val timeOfDayTint: TimeOfDayTint,
    private val darkClouds: Boolean = false,
) {

    var landscape = false
    protected val bgPadding = 20f
    private val treeAnimateDelayMin = 3f
    private val treeAnimateDelayRange = 7f
    private val treeAnim = true
    private val treesAnim = AnimPlayer(0, 19, 5f, false)
    private var treesAnimateDelay = 5f

    @CallSuper
    open fun draw() {
        timeOfDayTint.update()
        things.update()
    }

    @CallSuper
    open fun load() {
        things.spawnSun()
        things.spawnMoon()
    }

    @CallSuper
    open fun unload() {
        things.clear()
    }

    @CallSuper
    open fun update(state: WeatherState) {
        things.spawnClouds(state.weatherType.clouds, dark = darkClouds)
        things.spawnWisps(state.weatherType.wisps)
    }

    protected fun drawTree() = gl.pushMatrix(GL_MODELVIEW) {
        if (treeAnim && treesAnim.count > 0) {
            treesAnimateDelay -= time.deltaSeconds

            if (treesAnimateDelay <= 0f) {
                treesAnimateDelay =
                    treeAnimateDelayMin + (treeAnimateDelayRange * Random.nextFloat())
                treesAnim.reset()
            }
        }

        val treeTerrain = models[R.raw.trees_overlay_terrain]
        val treesOverlay = textures[R.drawable.trees_overlay]
        gl.glBindTexture(GL_TEXTURE_2D, treesOverlay.glId)

        if (landscape) gl.glTranslatef(2f, 70f, -65f)
        else gl.glTranslatef(-8f, 70f, -70f)

        gl.glScalef(5f, 5f, 5f)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        treeTerrain.render()

        val grass = models[R.raw.grass_overlay] as AnimatedModel
        val tree = models[R.raw.trees_overlay] as AnimatedModel
        grass.animator = treesAnim
        tree.animator = treesAnim
        treesAnim.update(time.deltaSeconds)
        tree.render()
        grass.render()
    }

}
