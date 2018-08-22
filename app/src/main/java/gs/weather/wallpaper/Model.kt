package gs.weather.wallpaper

import java.nio.ByteBuffer
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL

abstract class Model<T : GL>(
        internal val gl: T,
        internal val frames: Array<Frame>,
        internal val numIndices: Int) {

    fun render() {
        renderFrame(0)
    }

    internal abstract fun renderFrame(frame: Int)

    class Frame {
        var bufNormal: FloatBuffer? = null
        var bufNormalDirect: ByteBuffer? = null
        var bufNormalHandle = 0
        var bufVertex: FloatBuffer? = null
        var bufVertexDirect: ByteBuffer? = null
        var bufVertexHandle = 0
    }

}
