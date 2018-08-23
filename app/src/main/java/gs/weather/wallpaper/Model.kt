package gs.weather.wallpaper

import gs.weather.engine.Mesh
import javax.microedition.khronos.opengles.GL10.*
import javax.microedition.khronos.opengles.GL11
import javax.microedition.khronos.opengles.GL11.GL_ARRAY_BUFFER
import javax.microedition.khronos.opengles.GL11.GL_ELEMENT_ARRAY_BUFFER

open class Model internal constructor(
        val name: String,
        internal val gl: GL11,
        internal val frames: Array<Frame>,
        internal val indicesCount: Int,
        internal val bufTCHandle: Int,
        internal val bufIndexHandle: Int) {

    open fun render() {
        renderFrame(0)
    }

    internal fun renderFrame(frame: Int) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, frames[frame].bufVertexHandle)
        gl.glVertexPointer(3, GL_FLOAT, 0, 0)

        renderFrameShared(frame)
    }

    internal fun renderFrameShared(frame: Int) {
        gl.glBindBuffer(GL_ARRAY_BUFFER, frames[frame].bufNormalHandle)
        gl.glNormalPointer(GL_FLOAT, 0, 0)

        gl.glBindBuffer(GL_ARRAY_BUFFER, bufTCHandle)
        gl.glTexCoordPointer(2, GL_FLOAT, 0, 0)

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufIndexHandle)
        gl.glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_SHORT, 0)

        gl.glBindBuffer(GL_ARRAY_BUFFER, 0)
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    internal data class Frame(
            val bufNormalHandle: Int,
            val bufVertexHandle: Int)

    internal fun unload() {
        val ids = (frames.flatMap { listOf(it.bufNormalHandle, it.bufVertexHandle) }
                + bufIndexHandle + bufTCHandle).toIntArray()
        gl.glDeleteBuffers(ids.size, ids, 0)
    }

    // TODO delete this once finished
    open fun asMesh() = Mesh().also { mesh ->
        mesh.meshName = name
        mesh.numIndices = indicesCount
        mesh.bufIndexHandle = bufIndexHandle
        mesh.bufTCHandle = bufTCHandle
        mesh.frames = frames.map {
            Mesh.Frame().apply {
                bufNormalHandle = it.bufNormalHandle
                bufVertexHandle = it.bufVertexHandle
            }
        }.toTypedArray()
    }

}
