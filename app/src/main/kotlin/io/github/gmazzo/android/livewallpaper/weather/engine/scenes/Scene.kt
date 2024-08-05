package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.WeatherType
import io.github.gmazzo.android.livewallpaper.weather.engine.AnimPlayer
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.AnimatedModel
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDay.Companion.GOLDER_HOUR_FACTOR
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTint
import io.github.gmazzo.android.livewallpaper.weather.engine.withColor
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_MODULATE
import javax.microedition.khronos.opengles.GL10.GL_ONE
import javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

sealed class Scene(
    protected val time: GlobalTime,
    protected val gl: GL11,
    protected val models: Models,
    protected val textures: Textures,
    protected val things: Things,
    protected val timeOfDay: TimeOfDay,
    protected val timeOfDayTint: TimeOfDayTint,
    @DrawableRes backgroundId: Int,
    private val backgroundTint: EngineColor = timeOfDayTint.color,
    private val withSunAndMoon: Boolean = false,
    private val withStars: Boolean = false,
) {

    var landscape = false

    private val treesAnim = AnimPlayer(0, 19, 5f, false)
    private var treesAnimateDelay = 5f

    private val backgroundModel = models[R.raw.plane_16x16]
    private val backgroundTexture = textures[backgroundId]

    private val starModel = models[R.raw.stars]
    private val starTexture = textures[R.drawable.stars]
    private val starNoiseTexture = textures[R.drawable.noise]

    private val treesTerrainModel = models[R.raw.trees_overlay_terrain]
    private val treesGrassModel = models[R.raw.grass_overlay] as AnimatedModel
    private val treesTreeModel = models[R.raw.trees_overlay] as AnimatedModel
    private val treesTexture = textures[R.drawable.trees_overlay]

    @CallSuper
    open fun draw() = gl.pushMatrix(GL_MODELVIEW) {
        timeOfDayTint.update()
        things.update()

        drawBackground()

        // TODO get rid of this and use a straight forward coordinates system
        glTranslatef(0f, 0f, 40f)
        drawForeground()
    }

    @CallSuper
    open fun drawForeground() {
        things.render(foreground = false)
        drawTrees()
        things.render(foreground = true)
    }

    @CallSuper
    open fun load() {
        if (withSunAndMoon) {
            things.spawnSun()
            things.spawnMoon()
        }
    }

    @CallSuper
    open fun unload() {
        things.clear()
    }

    @CallSuper
    open fun update(weather: WeatherType) {
        things.spawnClouds(weather.clouds)
        things.spawnWisps(weather.wisps)
    }

    @CallSuper
    protected open fun drawBackground() = gl.pushMatrix(GL_MODELVIEW) {
        gl.glTranslatef(0f, 250f, 35f)
        gl.glScalef(40f, 20f, 20f)

        pushMatrix(GL_TEXTURE) {
            gl.glBindTexture(GL_TEXTURE_2D, backgroundTexture.glId)
            gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
            gl.glTranslatef(((WIND_SPEED * time.elapsedSeconds) * -.005f) % 1f, 0f, 0f)

            withColor(backgroundTint, alpha = 1f) {
                backgroundModel.render()
            }

            if (withStars) {
                renderStars()
            }
        }
    }

    private fun renderStars() {
        val factor = GOLDER_HOUR_FACTOR / 2
        val alpha = ((-timeOfDay.sunPosition + factor) / factor).coerceIn(0f, 1f)

        if (alpha > 0) {
            gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE)
            gl.glTranslatef((.1f * time.elapsedSeconds) % 1f, 300f, -100f)

            gl.withColor(1f, 1f, 1f, alpha) {
                starModel.renderFrameMultiTexture(starNoiseTexture, starTexture, GL_MODULATE, false)
            }
        }
    }

    private fun drawTrees() = gl.pushMatrix(GL_MODELVIEW) {
        if (treesAnim.count > 0) {
            treesAnimateDelay -= time.deltaSeconds

            if (treesAnimateDelay <= 0f) {
                treesAnimateDelay = 3f + (7f * Random.nextFloat())
                treesAnim.reset()
            }
        }

        gl.glBindTexture(GL_TEXTURE_2D, treesTexture.glId)

        if (landscape) gl.glTranslatef(2f, 70f, -65f)
        else gl.glTranslatef(-8f, 70f, -70f)

        gl.glScalef(5f, 5f, 5f)
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        treesTerrainModel.render()

        treesGrassModel.animator = treesAnim
        treesTreeModel.animator = treesAnim
        treesAnim.update(time.deltaSeconds)

        treesTreeModel.render()
        treesGrassModel.render()
    }

}
