package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures
import javax.microedition.khronos.opengles.GL10

class ThingLightning(
    models: Models,
    textures: Textures,
    color: EngineColor,
) : SimpleThing(
    models,
    textures,
    MODELS[GlobalRand.intRange(0, MODELS.size)],
    R.raw.lightning_pieces_core
) {

    override val engineColor = EngineColor(color.r, color.g, color.b, 1.0f)

    private val glowTexture by lazy { textures[R.raw.lightning_pieces_glow] }

    override fun render(gl: GL10) {
        gl.glEnable(GL10.GL_LIGHTING)
        gl.glEnable(GL10.GL_COLOR_BUFFER_BIT)

        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
        gl.glPushMatrix()
        gl.glTranslatef(origin.x, origin.y, origin.z)
        gl.glScalef(scale.x, scale.x, scale.x)
        gl.glRotatef(angles.a, angles.r, angles.g, angles.b)
        gl.glColor4f(engineColor.r, engineColor.g, engineColor.b, engineColor.a)
        model.renderFrameMultiTexture(glowTexture, texture, 260, false)
        gl.glPopMatrix()
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        gl.glDisable(GL10.GL_COLOR_BUFFER_BIT)
        gl.glDisable(GL10.GL_LIGHTING)
    }

    override fun update(timeDelta: Float) {
        super.update(timeDelta)
        engineColor.a -= 2.0f * timeDelta
        if (engineColor.a <= 0.0f) {
            delete()
        }
    }

    companion object {
        private val MODELS = intArrayOf(
            R.raw.lightning1, R.raw.lightning2, R.raw.lightning3
        )
    }

}