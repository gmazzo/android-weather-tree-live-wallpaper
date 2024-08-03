package io.github.gmazzo.android.livewallpaper.weather.engine.models

import android.util.Log
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10.GL_FLOAT
import javax.microedition.khronos.opengles.GL10.GL_TRIANGLES
import javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_SHORT
import javax.microedition.khronos.opengles.GL11

@Deprecated("it should use Model instead")
class Mesh {
    private var bufIndex: ShortBuffer? = null
    var bufIndexHandle: Int = 0
    var bufScratch: FloatBuffer? = null
    private var bufTC: FloatBuffer? = null
    var bufTCHandle: Int = 0
    lateinit var frames: Array<Frame?>
    var meshName: String? = null
    var numElements: Int = 0
    var numIndices: Int = 0
    var originalVertexArray: FloatArray? = null

    class Frame {
        var bufNormalHandle: Int = 0
        var bufVertexHandle: Int = 0
    }

    fun renderFrame_gl11_clear(gl11: GL11) {
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0)
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun renderFrame_gl11_render(gl11: GL11) {
        gl11.glDrawElements(GL_TRIANGLES, this.numIndices, GL_UNSIGNED_SHORT, 0)
    }

    fun renderFrame_gl11_setup(gl11: GL11, frameNum: Int) {
        val frame = frameNum.coerceIn(0, frames.size - 1)
        if (frame != frameNum) {
            Log.v(
                TAG,
                "ERROR: Mesh.renderFrame (" + this.meshName + ") given a frame outside of frames.length: " + frameNum
            )
        }

        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, frames[frame]!!.bufVertexHandle)
        gl11.glVertexPointer(3, GL_FLOAT, 0, 0)
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, frames[frame]!!.bufNormalHandle)
        gl11.glNormalPointer(GL_FLOAT, 0, 0)
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.bufTCHandle)
        gl11.glTexCoordPointer(2, GL_FLOAT, 0, 0)
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, this.bufIndexHandle)
    }

    companion object {
        private const val TAG = "GL Engine"
    }
}
