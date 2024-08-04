package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDay
import io.github.gmazzo.android.livewallpaper.weather.engine.timeofday.TimeOfDayTint
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_MODULATE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE
import javax.microedition.khronos.opengles.GL11
import kotlin.math.min

class ThingSun @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    private val time: GlobalTime,
    private val timeOfDay: TimeOfDay,
    private val timeOfDayTint: TimeOfDayTint,
) : Thing(gl, models[R.raw.plane_16x16], textures[R.raw.sun]) {

    override val engineColor = EngineColor(1f, 1f, .95f, 1f)

    private val sunBlend = textures[R.raw.sun_blend]

    override fun render() = gl.pushMatrix(GL_MODELVIEW) {
        val timeElapsed = time.elapsedSeconds

        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_COLOR)
        gl.glColor4f(engineColor.r, engineColor.g, engineColor.b, engineColor.a)
        gl.glLoadIdentity()
        gl.glTranslatef(origin.x, origin.y, origin.z)
        gl.glScalef(scale.x, scale.y, scale.z)
        gl.glRotatef((timeElapsed * 12f) % 360f, 0f, 1f, 0f)

        pushMatrix(GL_TEXTURE) {
            val angle = (timeElapsed * 18f) % 360f

            gl.glTranslatef(.5f, .5f, 0f)
            gl.glRotatef(angle, 0f, 0f, 1f)
            gl.glTranslatef(-.5f, -.5f, 0f)
            model.renderFrameMultiTexture(sunBlend, texture, GL_MODULATE, false)
        }
        //gl.glMatrixMode(GL_MODELVIEW)
    }

    override fun update() {
        super.update()

        var alpha = 0f

        if (timeOfDay.isDay) {
            scale = Vector(2f)
            val altitude = 175f * timeOfDay.sunPosition

            alpha = (altitude / 25f).coerceIn(0f, 1f)
            origin = origin.copy(z = min(altitude - 50f, 40f))

        } else {
            scale = Vector(0f)
        }

        engineColor.set(timeOfDayTint.color)
        engineColor.a = alpha
    }

}
