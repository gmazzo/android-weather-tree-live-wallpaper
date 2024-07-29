package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things
import io.github.gmazzo.android.livewallpaper.weather.engine.things.Things.Companion.WIND_SPEED
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Named
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

open class SceneClear @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    things: Things,
    @Named("timeOfDay") timeOfDayColor: EngineColor,
    @Named("sunPosition") private val sunPosition: MutableStateFlow<Float>,
) : Scene(gl, models, textures, things, timeOfDayColor, sunMoon = true) {

    open val backgroundId: Int = R.drawable.bg3

    override fun draw(time: GlobalTime) {
        things.update(time.sTimeDelta)
        gl.glDisable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL10.GL_LIGHT1)
        gl.glDisable(GL10.GL_LIGHTING)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA)
        renderBackground(time.sTimeElapsed)
        gl.glTranslatef(0.0f, 0.0f, 40.0f)
        things.render()
        drawTree(time.sTimeDelta)
    }

    private fun renderBackground(timeDelta: Float) = gl.pushMatrix {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[backgroundId].glId)
        gl.glColor4f(
            timeOfDayColor.r,
            timeOfDayColor.g,
            timeOfDayColor.b,
            1.0f
        )
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glTranslatef(0.0f, 250.0f, 35.0f)
        gl.glScalef(bgPadding * 2.0f, bgPadding, bgPadding)
        gl.glMatrixMode(GL10.GL_TEXTURE)

        pushMatrix {
            gl.glTranslatef(
                ((WIND_SPEED * timeDelta) * -0.005f) % 1.0f,
                0.0f,
                0.0f
            )
            val mesh = models[R.raw.plane_16x16]
            mesh.render()
            renderStars(timeDelta)
        }

        gl.glMatrixMode(GL10.GL_MODELVIEW)
    }

    private fun renderStars(timeDelta: Float) {
        val position = sunPosition.value

        if (position <= 0.0f) {
            gl.glColor4f(1.0f, 1.0f, 1.0f, position * -2.0f)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
            val starMesh = models[R.raw.stars]
            val noise = textures[R.drawable.noise]
            val star = textures[R.drawable.stars]
            gl.glTranslatef((0.1f * timeDelta) % 1.0f, 300.0f, -100.0f)
            starMesh.renderFrameMultiTexture(noise, star, GL10.GL_MODULATE, false)
        }
    }

}
