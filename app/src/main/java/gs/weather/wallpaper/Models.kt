package gs.weather.wallpaper

import android.content.res.Resources
import android.support.annotation.RawRes
import java.io.DataInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import javax.microedition.khronos.opengles.GL11.*

class Models(private val resources: Resources,
             private val gl: GL10) {

    @JvmOverloads // TODO delete this once finished
    fun loadBMDL(@RawRes rawId: Int, interpolated: Boolean = false) {
        DataInputStream(resources.openRawResource(rawId)).use { input ->
            val buffer4 = ByteArray(4)

            input.assertChunk("BMDL", buffer4)
            val version = input.readInt()
            val elements = input.readInt()
            val frames = input.readInt()

            input.skip(16)
            input.assertChunk("WIND", buffer4)
            val indicesCount = input.readInt()
            input.skip(8)
            val indices = ShortArray(indicesCount * 3) { input.readShort() }

            input.skip(4)
            input.assertChunk("TEXT", buffer4)
            val textsCount = input.readInt()
            input.skip(8)
            val texts = FloatArray(textsCount * 2) { input.readFloat() }

            input.skip(4)
            input.assertChunk("VERT", buffer4)
            val verticesCount = input.readInt()
            val verticesScale = input.readInt().let { if (it == 0) 128 else it }.toFloat()
            val verticesMapper = if (version >= 4)
                { _: Int -> input.readShort() / verticesScale } else { _ -> input.readFloat() }
            input.skip(4)
            val vertices = FloatArray(verticesCount * 3 * frames, init = verticesMapper)

            input.skip(4)
            input.assertChunk("NORM", buffer4)
            val normalsCount = input.readInt()
            val normalsMapper = if (version >= 3)
                { _: Int -> input.readByte() / 127.0f } else { _ -> input.readFloat() }
            input.skip(8)
            val normals = FloatArray(normalsCount * 3 * frames, init = normalsMapper)

            return loadWithArrays(vertices, normals, texts, indices, elements, frames, interpolated)
        }
    }

    private fun loadWithArrays(vertexs: FloatArray, normals: FloatArray, tcs: FloatArray, indices: ShortArray, num_elements: Int, num_frames: Int, willBeInterpolated: Boolean) {
        val useVertexBufferObjects = gl is GL11
        val iCapacity = indices.size
        val numTriangles = indices.size / 3
        val frames = arrayOfNulls<Model.Frame>(num_frames)
        val numElements = num_elements
        val originalVertexArray = if (willBeInterpolated) vertexs else null

        val length = num_elements * 3
        val vertexBufferBytes = length * 4
        val normalBufferBytes = length * 4
        val tcBufferBytes = num_elements * 2 * 4
        val indexBufferBytes = iCapacity * 2
        for (i in 0 until num_frames) {
            val frame = Model.Frame().apply { frames[i] = this }

            frame.bufVertexDirect = ByteBuffer.allocateDirect(vertexBufferBytes).order(ByteOrder.nativeOrder())
            frame.bufVertex = frame.bufVertexDirect!!.asFloatBuffer().apply {
                clear()
                put(vertexs, i * length, length)
                position(0)
            }

            frame.bufNormalDirect = ByteBuffer.allocateDirect(normalBufferBytes).order(ByteOrder.nativeOrder())
            frame.bufNormal = frame.bufNormalDirect!!.asFloatBuffer().apply {
                clear()
                put(normals, i * length, length)
                position(0)
            }

            if (useVertexBufferObjects) {
                val gl11 = gl as GL11
                val handleTemp = IntArray(1)

                gl11.glGenBuffers(1, handleTemp, 0)
                frame.bufVertexHandle = handleTemp[0]
                gl11.glBindBuffer(GL_ARRAY_BUFFER, frame.bufVertexHandle)
                gl11.glBufferData(GL_ARRAY_BUFFER, vertexBufferBytes, frame.bufVertex, GL_STATIC_DRAW)
                gl11.glBindBuffer(GL_ARRAY_BUFFER, 0)

                gl11.glGenBuffers(1, handleTemp, 0)
                frame.bufNormalHandle = handleTemp[0]
                gl11.glBindBuffer(GL_ARRAY_BUFFER, frame.bufNormalHandle)
                gl11.glBufferData(GL_ARRAY_BUFFER, normalBufferBytes, frame.bufNormal, GL_STATIC_DRAW)
                gl11.glBindBuffer(GL_ARRAY_BUFFER, 0)
            }
        }

        val bufTCDirect = ByteBuffer.allocateDirect(tcBufferBytes).order(ByteOrder.nativeOrder())
        val bufTC = bufTCDirect.asFloatBuffer().apply {
            clear()
            put(tcs)
            position(0)
        }

        val bufIndexDirect = ByteBuffer.allocateDirect(indexBufferBytes).order(ByteOrder.nativeOrder())
        val bufIndex = bufIndexDirect.asShortBuffer().apply {
            clear()
            put(indices)
            position(0)
        }

        val numIndices = bufIndex.capacity()
        if (useVertexBufferObjects) {
            val gl11 = gl as GL11
            val handleTemp = IntArray(1)
            gl11.glGenBuffers(1, handleTemp, 0)

            val bufIndexHandle = handleTemp[0]
            gl11.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufIndexHandle)
            gl11.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBufferBytes, bufIndex, GL_STATIC_DRAW)
            gl11.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
            gl11.glGenBuffers(1, handleTemp, 0)

            val bufTCHandle = handleTemp[0]
            gl11.glBindBuffer(GL_ARRAY_BUFFER, bufTCHandle)
            gl11.glBufferData(GL_ARRAY_BUFFER, tcBufferBytes, bufTC, GL_STATIC_DRAW)
            gl11.glBindBuffer(GL_ARRAY_BUFFER, 0)
        }
    }

    private fun InputStream.assertChunk(header: String, buffer: ByteArray) {
        read(buffer)

        val value = buffer.toString(Charset.forName("UTF-8"))
        if (header != value) {
            throw  IllegalArgumentException("Unexpected chunk: $header!=$value")
        }
    }

}