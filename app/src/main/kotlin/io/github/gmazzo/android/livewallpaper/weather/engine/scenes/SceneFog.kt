package io.github.gmazzo.android.livewallpaper.weather.engine.scenes

import android.graphics.Color
import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.withFlags
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10.GL_DONT_CARE
import javax.microedition.khronos.opengles.GL10.GL_FOG
import javax.microedition.khronos.opengles.GL10.GL_FOG_COLOR
import javax.microedition.khronos.opengles.GL10.GL_FOG_DENSITY
import javax.microedition.khronos.opengles.GL10.GL_FOG_END
import javax.microedition.khronos.opengles.GL10.GL_FOG_HINT
import javax.microedition.khronos.opengles.GL10.GL_FOG_MODE
import javax.microedition.khronos.opengles.GL10.GL_FOG_START
import javax.microedition.khronos.opengles.GL10.GL_LINEAR

class SceneFog @Inject constructor(
    dependencies: SceneDependencies,
) : Scene(
    dependencies,
    background = R.drawable.bg1,
    backgroundTint = EngineColor(Color.WHITE),
) {

    private val fogColors =
        floatArrayOf(.8f, .8f, .8f, 1f)

    override fun draw() = gl.withFlags(GL_FOG) {
        super.draw()

        timeOfDayTint.color.toArray(fogColors)
        gl.glFogf(GL_FOG_MODE, GL_LINEAR.toFloat())
        gl.glFogfv(GL_FOG_COLOR, fogColors, 0)
        gl.glFogf(GL_FOG_DENSITY, FOG_DENSITY)
        gl.glFogf(GL_FOG_START, -10f)
        gl.glFogf(GL_FOG_END, 190f)
        gl.glHint(GL_FOG_HINT, GL_DONT_CARE)
    }

    companion object {
        private const val FOG_DENSITY = .2f
    }

}
