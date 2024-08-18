package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.withColor
import io.github.gmazzo.android.livewallpaper.weather.engine.withFlags
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10.GL_ADD
import javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT
import javax.microedition.khronos.opengles.GL10.GL_LIGHTING
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_ONE
import javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

class ThingLightning @Inject constructor(
    gl: GL11,
    models: Models,
    textures: Textures,
    private val time: GlobalTime,
) : Thing(
    gl,
    model = models[MODELS[Random.nextInt(MODELS.size)]],
    texture = textures[R.raw.lightning_pieces_core],
) {

    private val glow = textures[R.raw.lightning_pieces_glow]

    override fun render() = gl.pushMatrix(GL_MODELVIEW) {
        withFlags(GL_LIGHTING, GL_COLOR_BUFFER_BIT) {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE)
            glTranslatef(origin.x, origin.y, origin.z)
            glScalef(scale.x, scale.x, scale.x)

            withColor(color) {
                model.render(glow, texture, GL_ADD)
            }
        }
    }

    override fun update() {
        super.update()

        color.a -= 2f * time.deltaSeconds
        if (color.a <= 0f) {
            delete()
        }
    }

    companion object {
        private val MODELS = intArrayOf(
            R.raw.lightning1, R.raw.lightning2, R.raw.lightning3
        )
    }

}
