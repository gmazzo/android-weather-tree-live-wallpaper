package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.R
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneBase
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Texture
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class ThingSun : Thing() {
    private var sunBlend: Texture? = null

    init {
        this.engineColor = EngineColor(1.0f, 1.0f, 0.95f, 1.0f)
    }

    override fun render(gl: GL10, textures: Textures?, models: Models?) {
        if (texture == null) {
            texture = textures!![R.raw.sun]
            sunBlend = textures[R.raw.sun_blend]
            model = models!![R.raw.plane_16x16]
        }

        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_COLOR)
        gl.glColor4f(
            engineColor!!.r,
            engineColor!!.g,
            engineColor!!.b,
            engineColor!!.a
        )
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPushMatrix()
        gl.glLoadIdentity()
        gl.glTranslatef(origin.x, origin.y, origin.z)
        gl.glScalef(scale.x, scale.y, scale.z)
        gl.glRotatef((this.sTimeElapsed * 12.0f) % 360.0f, 0.0f, 1.0f, 0.0f)
        gl.glMatrixMode(GL10.GL_TEXTURE)
        gl.glPushMatrix()
        val f11 = (this.sTimeElapsed * 18.0f) % 360.0f
        gl.glTranslatef(0.5f, 0.5f, 0.0f)
        gl.glRotatef(f11, 0.0f, 0.0f, 1.0f)
        gl.glTranslatef(-0.5f, -0.5f, 0.0f)
        if (gl is GL11) {
            model!!.renderFrameMultiTexture(sunBlend!!, texture!!, GL10.GL_MODULATE, false)
        } else {
            gl.glBindTexture(GL10.GL_TEXTURE0, texture!!.glId)
            model!!.render()
        }
        gl.glPopMatrix()
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPopMatrix()
    }

    override fun update(timeDelta: Float) {
        super.update(timeDelta)
        val sunPos: Float = SceneBase.Companion.todSunPosition
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
        engineColor!!.set(SceneBase.Companion.todEngineColorFinal!!)
        engineColor!!.times(alpha)
    }

    companion object {
        private const val TAG = "Sun"
    }
}
