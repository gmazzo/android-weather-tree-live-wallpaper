package io.github.gmazzo.android.livewallpaper.weather.engine.things

import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Model
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.math.abs
import kotlin.random.Random

sealed class Thing(
    protected val gl: GL11,
) {
    protected val angles: EngineColor = EngineColor(0.0f, 0.0f, 1.0f, 0.0f)
    protected abstract val engineColor: EngineColor?
    var isDeleted: Boolean = false
        private set
    val origin = Vector(0.0f, 0.0f, 0.0f)
    protected var timeElapsed = 0.0f
    var scale: Vector = Vector(1.0f, 1.0f, 1.0f)
    var velocity: Vector? = null
    private val visScratch = Vector(0.0f, 0.0f, 0.0f)
    private var visible = true
    var visWidth: Float = 3.0f

    protected abstract val model: Model

    protected abstract val texture: Texture

    fun checkVisibility(cameraPos: Vector, cameraAngleZ: Float, fov: Float) {
        if (this.visWidth == 0.0f) {
            this.visible = true
            return
        }
        visScratch.set(
            origin.x - cameraPos.x,
            origin.y - cameraPos.y,
            origin.z - cameraPos.z
        )
        visScratch.rotateAroundZ(cameraAngleZ)
        this.visible =
            abs(visScratch.x.toDouble()) < this.visWidth + ((visScratch.y * 0.01111111f) * fov)
    }

    fun delete() {
        this.isDeleted = true
    }

    open fun render() = gl.pushMatrix {
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glTranslatef(origin.x, origin.y, origin.z)
        gl.glScalef(scale.x, scale.y, scale.z)
        if (angles.a != 0.0f) {
            gl.glRotatef(
                angles.a,
                angles.r,
                angles.g,
                angles.b
            )
        }
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.glId)
        engineColor?.let { gl.glColor4f(it.r, it.g, it.b, it.a) }

        model.render()

        if (engineColor != null) {
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        }
    }

    fun renderIfVisible() {
        if (this.visible) {
            render()
        }
    }

    open fun update(timeDelta: Float) {
        this.timeElapsed += timeDelta

        velocity?.let { velocity ->
            origin.plus(
                velocity.x * timeDelta,
                velocity.y * timeDelta,
                velocity.z * timeDelta
            )
        }
    }

    fun updateIfVisible(timeDelta: Float) {
        if (this.visible) {
            update(timeDelta)
        }
    }

    fun randomizeScale() = scale.set(
        3.5f + Random.nextFloat(0.0f, 2.0f),
        3.0f,
        3.5f + Random.nextFloat(0.0f, 2.0f)
    )

}
