package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.engine.withColor
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_MODULATE
import javax.microedition.khronos.opengles.GL10.GL_ONE
import javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_COLOR
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE
import javax.microedition.khronos.opengles.GL11
import kotlin.math.min

class ThingSun @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    private val time: GlobalTime,
    private val timeOfDay: TimeOfDay,
) : Thing(gl, models[R.raw.plane_16x16], textures[R.raw.sun]) {

    private val sunBlend = textures[R.raw.sun_blend]

    init {
        scale = Vector(2f)
        color.set(1f, 1f, .95f, 1f)
    }

    override fun render() = gl.pushMatrix(GL_MODELVIEW) {
        val timeElapsed = time.elapsedSeconds

        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR)
        gl.glTranslatef(origin.x, origin.y, origin.z)
        gl.glScalef(scale.x, scale.y, scale.z)
        gl.glRotatef((timeElapsed * 12f) % 360f, 0f, 1f, 0f)

        pushMatrix(GL_TEXTURE) {
            val angle = (timeElapsed * 18f) % 360f

            gl.glTranslatef(.5f, .5f, 0f)
            gl.glRotatef(angle, 0f, 0f, 1f)
            gl.glTranslatef(-.5f, -.5f, 0f)

            withColor(color) {
                model.renderFrameMultiTexture(sunBlend, texture, GL_MODULATE, false)
            }
        }
    }

    override fun update() {
        val altitude = 215f * timeOfDay.sunPosition

        origin = origin.copy(z = min(altitude - 90f, 0f))
        super.update()
    }

}
