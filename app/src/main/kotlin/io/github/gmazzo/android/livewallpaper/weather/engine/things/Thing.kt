package io.github.gmazzo.android.livewallpaper.weather.engine.things

import androidx.annotation.CallSuper
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Model
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

sealed class Thing(
    protected val time: GlobalTime,
    protected val gl: GL11,
) {
    protected abstract val engineColor: EngineColor?
    var isDeleted: Boolean = false
        private set
    var origin = Vector()
    protected var timeElapsed = 0.0f
    var scale: Vector = Vector(1f)
    var velocity: Vector? = null
    private val visScratch = Vector()
    var visWidth = 3.0f

    protected abstract val model: Model

    protected abstract val texture: Texture

    fun delete() {
        isDeleted = true
    }

    open fun render() = gl.pushMatrix {
        gl.glMatrixMode(GL_MODELVIEW)
        gl.glTranslatef(origin.x, origin.y, origin.z)
        gl.glScalef(scale.x, scale.y, scale.z)
        gl.glBindTexture(GL_TEXTURE_2D, texture.glId)

        engineColor?.let { gl.glColor4f(it.r, it.g, it.b, it.a) }

        model.render()

        if (engineColor != null) {
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        }
    }

    @CallSuper
    open fun update() {
        val timeDelta = time.deltaSeconds

        timeElapsed += timeDelta
        velocity?.let { origin += it * timeDelta }
    }

    fun randomizeScale() {
        scale = Vector(
            3.5f + Random.nextFloat(0.0f, 2.0f),
            3.0f,
            3.5f + Random.nextFloat(0.0f, 2.0f)
        )
    }

}
