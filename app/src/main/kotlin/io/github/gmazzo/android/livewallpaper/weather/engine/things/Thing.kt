package io.github.gmazzo.android.livewallpaper.weather.engine.things

import androidx.annotation.CallSuper
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GLBlendFactor
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.StaticModel
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import io.github.gmazzo.android.livewallpaper.weather.engine.withColor
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_ONE
import javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL11

sealed class Thing(
    protected val gl: GL11,
    protected val model: StaticModel,
    protected open val texture: Texture,
) {

    var foreground: Boolean = false

    open val color: EngineColor = EngineColor()

    var deleted: Boolean = false
        private set

    var origin = Vector()

    var scale: Vector = Vector(1f)

    fun delete() {
        deleted = true
    }

    open fun render() =
        render(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)

    protected fun render(
        @GLBlendFactor sourceBlendFactor: Int,
        @GLBlendFactor destBlendFactor: Int,
    ) = gl.pushMatrix(GL_MODELVIEW) {
        gl.glTranslatef(origin.x, origin.y, origin.z)
        gl.glScalef(scale.x, scale.y, scale.z)

        pushMatrix(GL_TEXTURE) {
            gl.glBlendFunc(sourceBlendFactor, destBlendFactor)
            gl.glBindTexture(GL_TEXTURE_2D, texture.glId)

            withColor(color) {
                model.render()
            }
        }
    }

    @CallSuper
    open fun update() {
    }

}
