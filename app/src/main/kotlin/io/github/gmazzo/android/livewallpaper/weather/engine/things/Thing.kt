package io.github.gmazzo.android.livewallpaper.weather.engine.things

import androidx.annotation.RawRes
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Models
import io.github.gmazzo.android.livewallpaper.weather.engine.particles.ParticleSystem
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.math.abs

abstract class Thing(
    models: Models,
    @RawRes modelId: Int,
) {
    protected val angles: EngineColor = EngineColor(0.0f, 0.0f, 1.0f, 0.0f)
    protected abstract val engineColor: EngineColor?
    var isDeleted: Boolean = false
        private set
    var origin: Vector = Vector(0.0f, 0.0f, 0.0f)
    var particleSystem: ParticleSystem? = null
    var sTimeElapsed: Float = 0.0f
    var scale: Vector = Vector(1.0f, 1.0f, 1.0f)
    var targetName: String? = null
    var velocity: Vector? = null
    private val visScratch = Vector(0.0f, 0.0f, 0.0f)
    private var vis_isVisible = true
    var vis_width: Float = 3.0f

    protected val model by lazy { models[modelId] }

    protected abstract val texture: Texture

    fun checkVisibility(cameraPos: Vector, cameraAngleZ: Float, fov: Float) {
        if (this.vis_width == 0.0f) {
            this.vis_isVisible = true
            return
        }
        visScratch.set(
            origin.x - cameraPos.x,
            origin.y - cameraPos.y,
            origin.z - cameraPos.z
        )
        visScratch.rotateAroundZ(cameraAngleZ)
        this.vis_isVisible =
            abs(visScratch.x.toDouble()) < this.vis_width + ((visScratch.y * 0.01111111f) * fov)
    }

    fun delete() {
        this.isDeleted = true
    }

    open fun render(gl: GL10) {
        if (this.particleSystem != null && (gl is GL11)) {
            particleSystem!!.render(gl, this.origin)
        }
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPushMatrix()
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

        gl.glPopMatrix()
        if (this.engineColor != null) {
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        }
    }

    fun renderIfVisible(gl10: GL10) {
        if (this.vis_isVisible) {
            render(gl10)
        }
    }

    open fun update(timeDelta: Float) {
        this.sTimeElapsed += timeDelta
        if (this.velocity != null) {
            origin.plus(
                velocity!!.x * timeDelta,
                velocity!!.y * timeDelta,
                velocity!!.z * timeDelta
            )
        }
        if (this.particleSystem != null) {
            particleSystem!!.update(timeDelta)
        }
    }

    fun updateIfVisible(timeDelta: Float) {
        if (this.vis_isVisible) {
            update(timeDelta)
        }
    }
}
