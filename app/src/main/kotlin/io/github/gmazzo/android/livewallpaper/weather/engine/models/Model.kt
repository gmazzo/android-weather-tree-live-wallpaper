package io.github.gmazzo.android.livewallpaper.weather.engine.models

import androidx.annotation.RawRes
import io.github.gmazzo.android.livewallpaper.weather.engine.AnimPlayer
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.asDirectFloatBuffer
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.set
import io.github.gmazzo.android.livewallpaper.weather.engine.withFlags
import java.io.Closeable
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE0
import javax.microedition.khronos.opengles.GL10.GL_TRIANGLES
import javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_SHORT
import javax.microedition.khronos.opengles.GL11
import javax.microedition.khronos.opengles.GL11.GL_ARRAY_BUFFER
import javax.microedition.khronos.opengles.GL11.GL_ELEMENT_ARRAY_BUFFER
import javax.microedition.khronos.opengles.GL11.GL_FLOAT
import javax.microedition.khronos.opengles.GL11.GL_MODULATE
import javax.microedition.khronos.opengles.GL11.GL_TEXTURE1
import javax.microedition.khronos.opengles.GL11.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL11.GL_TEXTURE_COORD_ARRAY
import javax.microedition.khronos.opengles.GL11.GL_TEXTURE_ENV
import javax.microedition.khronos.opengles.GL11.GL_TEXTURE_ENV_MODE

class Model(
    val name: String,
    @param:RawRes val resId: Int,
    internal val gl: GL11,
    private val indicesCount: Int,
    private val bufTCHandle: Int,
    private val bufIndexHandle: Int,
    private val elementsCount: Int,
    private val vertices: FloatArray,
    private val frames: List<Frame>,
) : Closeable {

    var animator: AnimPlayer? = null

    private val currentFrame get() = frames[animator?.currentFrame ?: 0]

    private val bufScratch by lazy { (elementsCount * 3).asDirectFloatBuffer() }

    fun render(frame: Frame = currentFrame) {
        animator?.renderAnimated(frame) ?: renderStatic(frame)

        // cleanup
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private fun renderStatic(frame: Frame) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, frame.bufVertexHandle)
        gl.glVertexPointer(3, GL_FLOAT, 0, 0)
        finishRender(frame)
    }

    private fun AnimPlayer.renderAnimated(frame: Frame) {
        val firstFrameOffset = elementsCount * currentFrame * 3
        val blendFrameOffset = elementsCount * blendFrame * 3

        when {
            blendFrameAmount < .01 -> renderStatic(frame)
            blendFrameAmount > .99 -> renderStatic(frames[blendFrame])
            else -> {
                for (i in 0 until bufScratch.capacity()) {
                    bufScratch[i] = vertices[firstFrameOffset + i] * (1 - blendFrameAmount) +
                            vertices[blendFrameOffset + i] * blendFrameAmount
                }
                bufScratch.position(0)

                gl.glVertexPointer(3, GL_FLOAT, 0, bufScratch)
                finishRender(frame)
            }
        }
    }

    private fun finishRender(frame: Frame) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, frame.bufNormalHandle)
        gl.glNormalPointer(GL_FLOAT, 0, 0)

        gl.glBindBuffer(GL_ARRAY_BUFFER, bufTCHandle)
        gl.glTexCoordPointer(2, GL_FLOAT, 0, 0)

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufIndexHandle)
        gl.glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_SHORT, 0)
    }

    fun render(
        texture1: Texture,
        texture2: Texture,
        combine: Int,
        frame: Frame = currentFrame,
    ) = with(gl) {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, texture1.glId)
        glBindBuffer(GL_ARRAY_BUFFER, bufTCHandle)
        glTexCoordPointer(2, GL_FLOAT, 0, 0)
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE)

        glActiveTexture(GL_TEXTURE1)
        withFlags(GL_TEXTURE_2D) {
            glClientActiveTexture(GL_TEXTURE1)
            glEnableClientState(GL_TEXTURE_COORD_ARRAY)
            glBindTexture(GL_TEXTURE_2D, texture2.glId)

            glBindBuffer(GL_ARRAY_BUFFER, bufTCHandle)
            glTexCoordPointer(2, GL_FLOAT, 0, 0)
            glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, combine)
            glBindBuffer(GL_ARRAY_BUFFER, frame.bufVertexHandle)
            glVertexPointer(3, GL_FLOAT, 0, 0)
            glBindBuffer(GL_ARRAY_BUFFER, frame.bufNormalHandle)
            glNormalPointer(GL_FLOAT, 0, 0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufIndexHandle)
            glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_SHORT, 0)

            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        }

        glActiveTexture(GL_TEXTURE0)
        glClientActiveTexture(GL_TEXTURE0)
    }

    override fun close() {
        val ids = buildList {
            frames.forEach {
                add(it.bufNormalHandle)
                add(it.bufVertexHandle)
            }
            add(bufIndexHandle)
            add(bufTCHandle)
        }.toIntArray()

        gl.glDeleteBuffers(ids.size, ids, 0)
    }

    data class Frame(
        val bufNormalHandle: Int,
        val bufVertexHandle: Int
    )

    override fun toString() = "$name (resId=$resId, frames=${frames.size})"

}
