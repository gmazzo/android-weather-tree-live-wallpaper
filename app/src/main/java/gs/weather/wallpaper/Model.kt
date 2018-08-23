package gs.weather.wallpaper

import gs.weather.engine.Mesh
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11

class Model internal constructor(
        val name: String,
        private val gl: GL11,
        private val frames: Array<Frame>,
        private val elementsCount: Int,
        private val indicesCount: Int,
        private val vertices: FloatArray?,
        private val bufTCHandle: Int,
        private val bufIndexHandle: Int) {

    fun render() {
        renderFrame(0)
    }

    private fun renderFrame(frame: Int) {
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, frames[frame].bufVertexHandle)
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, 0)

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, frames[frame].bufNormalHandle)
        gl.glNormalPointer(GL10.GL_FLOAT, 0, 0)

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, bufTCHandle)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, 0)

        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, bufIndexHandle)
        gl.glDrawElements(4, indicesCount, GL10.GL_UNSIGNED_SHORT, 0)

        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0)
        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0)
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
    fun asMesh() = Mesh(name, elementsCount, indicesCount, bufIndexHandle, bufTCHandle, vertices,
            frames.map {
                Mesh.Frame().apply {
                    bufNormalHandle = it.bufNormalHandle
                    bufVertexHandle = it.bufVertexHandle
                }
            }.toTypedArray())

}
