package io.github.gmazzo.android.livewallpaper.weather.engine.models

import androidx.annotation.RawRes
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import javax.microedition.khronos.opengles.GL10.GL_FLOAT
import javax.microedition.khronos.opengles.GL10.GL_MODULATE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE0
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE1
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_COORD_ARRAY
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_ENV
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_ENV_MODE
import javax.microedition.khronos.opengles.GL10.GL_TRIANGLES
import javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_SHORT
import javax.microedition.khronos.opengles.GL11
import javax.microedition.khronos.opengles.GL11.GL_ARRAY_BUFFER
import javax.microedition.khronos.opengles.GL11.GL_ELEMENT_ARRAY_BUFFER

open class Model internal constructor(
    internal val gl: GL11,
    @RawRes val resId: Int,
    private val frames: Array<Frame>,
    private val indicesCount: Int,
    private val bufTCHandle: Int,
    private val bufIndexHandle: Int
) {

    open fun render() {
        renderFrame(0)
    }

    fun renderFrame(frame: Int) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, frames[frame].bufVertexHandle)
        gl.glVertexPointer(3, GL_FLOAT, 0, 0)

        renderFrameShared(frame)
    }

    protected fun renderFrameShared(frame: Int) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, frames[frame].bufNormalHandle)
        gl.glNormalPointer(GL_FLOAT, 0, 0)

        gl.glBindBuffer(GL_ARRAY_BUFFER, bufTCHandle)
        gl.glTexCoordPointer(2, GL_FLOAT, 0, 0)

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufIndexHandle)
        gl.glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_SHORT, 0)

        gl.glBindBuffer(GL_ARRAY_BUFFER, 0)
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun renderFrameMultiTexture(tex1: Texture, tex2: Texture, combine: Int, envMap: Boolean) {
        val frameNum = 0
        gl.glActiveTexture(GL_TEXTURE0)
        gl.glBindTexture(GL_TEXTURE_2D, tex1.glId)
        if (envMap) {
            gl.glBindBuffer(GL_ARRAY_BUFFER, this.frames[frameNum].bufNormalHandle)
            gl.glTexCoordPointer(3, GL_FLOAT, 0, 0)
        } else {
            gl.glBindBuffer(GL_ARRAY_BUFFER, this.bufTCHandle)
            gl.glTexCoordPointer(2, GL_FLOAT, 0, 0)
        }
        gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE)
        gl.glActiveTexture(GL_TEXTURE1)
        gl.glEnable(GL_TEXTURE_2D)
        gl.glClientActiveTexture(GL_TEXTURE1)
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY)
        gl.glBindTexture(GL_TEXTURE_2D, tex2.glId)
        gl.glBindBuffer(GL_ARRAY_BUFFER, this.bufTCHandle)
        gl.glTexCoordPointer(2, GL_FLOAT, 0, 0)
        gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, combine)
        gl.glBindBuffer(GL_ARRAY_BUFFER, this.frames[frameNum].bufVertexHandle)
        gl.glVertexPointer(3, GL_FLOAT, 0, 0)
        gl.glBindBuffer(GL_ARRAY_BUFFER, this.frames[frameNum].bufNormalHandle)
        gl.glNormalPointer(GL_FLOAT, 0, 0)
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bufIndexHandle)
        gl.glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_SHORT, 0)
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0)
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        gl.glDisable(GL_TEXTURE_2D)
        gl.glActiveTexture(GL_TEXTURE0)
        gl.glClientActiveTexture(GL_TEXTURE0)
    }

    internal data class Frame(
        val bufNormalHandle: Int,
        val bufVertexHandle: Int
    )

    internal fun unload() {
        val ids = (frames.flatMap { listOf(it.bufNormalHandle, it.bufVertexHandle) }
                + bufIndexHandle + bufTCHandle).toIntArray()
        gl.glDeleteBuffers(ids.size, ids, 0)
    }

}
