package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Textures
import io.github.gmazzo.android.livewallpaper.weather.engine.time.Clock
import io.github.gmazzo.android.livewallpaper.weather.engine.withColor
import io.github.gmazzo.android.livewallpaper.weather.engine.withFlags
import kotlinx.coroutines.flow.MutableStateFlow
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
    random: Random,
    gl: GL11,
    private val resources: Resources,
    private val clock: MutableStateFlow<Clock>,
) : Thing(
    gl,
    model = resources.models[random.nextInt(resources.models.size)],
    texture = resources.lightningPiecesCore,
) {

    override fun render() = gl.pushMatrix(GL_MODELVIEW) {
        withFlags(GL_LIGHTING, GL_COLOR_BUFFER_BIT) {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE)
            glTranslatef(origin.x, origin.y, origin.z)
            glScalef(scale.x, scale.x, scale.x)

            withColor(color) {
                model.render(resources.lightningPiecesGlow, texture, GL_ADD)
            }
        }
    }

    override fun update() {
        super.update()

        color.a -= 2f * clock.value.deltaSeconds
        if (color.a <= 0f) {
            delete()
        }
    }

    class Resources @Inject constructor(
        models: Models,
        textures: Textures,
    ) {
        val lightning1 = models[R.raw.lightning1]
        val lightning2 = models[R.raw.lightning2]
        val lightning3 = models[R.raw.lightning3]
        val lightningPiecesCore = textures[R.raw.lightning_pieces_core]
        val lightningPiecesGlow = textures[R.raw.lightning_pieces_glow]

        val models = arrayOf(lightning1, lightning2, lightning3)
    }

}
