package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

class ThingLightning @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
) : ThingSimple(gl, models, textures, MODELS[Random.nextInt(MODELS.size)], R.raw.lightning_pieces_core) {

    override val engineColor = EngineColor(1f, 1f, 1f, 1.0f)

    private val glowTexture by lazy { textures[R.raw.lightning_pieces_glow] }

    override fun render() = gl.pushMatrix {
        gl.glEnable(GL10.GL_LIGHTING)
        gl.glEnable(GL10.GL_COLOR_BUFFER_BIT)

        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
        gl.glTranslatef(origin.x, origin.y, origin.z)
        gl.glScalef(scale.x, scale.x, scale.x)
        gl.glColor4f(engineColor.r, engineColor.g, engineColor.b, engineColor.a)
        model.renderFrameMultiTexture(glowTexture, texture, 260, false)

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
