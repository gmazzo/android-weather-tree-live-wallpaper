package io.github.gmazzo.android.livewallpaper.weather.engine.models

import androidx.annotation.RawRes
import java.io.Closeable
import javax.microedition.khronos.opengles.GL10.GL_FLOAT
import javax.microedition.khronos.opengles.GL10.GL_TRIANGLES
import javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_SHORT
import javax.microedition.khronos.opengles.GL11
import javax.microedition.khronos.opengles.GL11.GL_ARRAY_BUFFER
import javax.microedition.khronos.opengles.GL11.GL_ELEMENT_ARRAY_BUFFER

sealed class Model(
    val name: String,
    @RawRes val resId: Int,
    internal val gl: GL11,
    protected val indicesCount: Int,
    protected val bufTCHandle: Int,
    protected val bufIndexHandle: Int,
) : Closeable {

    abstract fun render()

    internal abstract fun collectBufferIds(): Sequence<Int>

    internal fun render(frame: Frame) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, frame.bufNormalHandle)
        gl.glNormalPointer(GL_FLOAT, 0, 0)

        gl.glBindBuffer(GL_ARRAY_BUFFER, bufTCHandle)
        gl.glTexCoordPointer(2, GL_FLOAT, 0, 0)

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufIndexHandle)
        gl.glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_SHORT, 0)

        gl.glBindBuffer(GL_ARRAY_BUFFER, 0)
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    final override fun close() {
        val ids = (sequenceOf(bufIndexHandle, bufTCHandle) + collectBufferIds())
            .toList()
            .toIntArray()

        gl.glDeleteBuffers(ids.size, ids, 0)
    }

    data class Frame(
        val bufNormalHandle: Int,
        val bufVertexHandle: Int
    )

    final override fun toString() = "$name (resId=$resId)"

}
