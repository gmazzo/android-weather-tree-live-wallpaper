package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Named
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class ThingSun @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    @Named("timeOfDay") private val timeOfDayColor: EngineColor,
    @Named("sunPosition") private val todSunPosition: MutableStateFlow<Float>,
) : SimpleThing(gl, models, textures, R.raw.plane_16x16, R.raw.sun) {

    override val engineColor = EngineColor(1.0f, 1.0f, 0.95f, 1.0f)

    private val sunBlend by lazy { textures[R.raw.sun_blend] }

    override fun render() = gl.pushMatrix {
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_COLOR)
        gl.glColor4f(
            engineColor.r,
            engineColor.g,
            engineColor.b,
            engineColor.a
        )
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glTranslatef(origin.x, origin.y, origin.z)
        gl.glScalef(scale.x, scale.y, scale.z)
        gl.glRotatef((timeElapsed * 12.0f) % 360.0f, 0.0f, 1.0f, 0.0f)
        gl.glMatrixMode(GL10.GL_TEXTURE)

        pushMatrix {
            val angle = (timeElapsed * 18.0f) % 360.0f

            gl.glTranslatef(0.5f, 0.5f, 0.0f)
            gl.glRotatef(angle, 0.0f, 0.0f, 1.0f)
            gl.glTranslatef(-0.5f, -0.5f, 0.0f)
            model.renderFrameMultiTexture(sunBlend, texture, GL10.GL_MODULATE, false)
        }
        gl.glMatrixMode(GL10.GL_MODELVIEW)
    }

    override fun update(timeDelta: Float) {
        super.update(timeDelta)
        val sunPos = todSunPosition.value
        var alpha = 0.0f

        if (sunPos > 0.0f) {
            scale.set(2.0f)
            val altitude = 175.0f * sunPos

            alpha = altitude / 25.0f
            if (alpha > 1.0f) {
                alpha = 1.0f
            }
            origin.z = altitude - 50.0f
            if (origin.z > 40.0f) {
                origin.z = 40.0f
            }
        } else {
            scale.set(0.0f)
        }

        engineColor.set(timeOfDayColor)
        engineColor.a = alpha
    }

}
