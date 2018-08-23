package gs.weather.wallpaper

import android.content.res.Resources
import android.support.annotation.RawRes
import java.io.Closeable
import java.io.DataInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import javax.microedition.khronos.opengles.GL11
import javax.microedition.khronos.opengles.GL11.*

class Models(private val resources: Resources,
             private val gl: GL11) : Closeable {
    private val models = mutableMapOf<String, Model>()

    @JvmOverloads // TODO delete this once finished
    fun loadBMDL(name: String, @RawRes rawId: Int, interpolated: Boolean = false) = models.getOrPut(name) {
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

            return loadWithArrays(name, vertices, normals, texts, indices, elements, frames, interpolated)
        }
    }

    private fun loadWithArrays(name: String,
                               vertices: FloatArray,
                               normals: FloatArray,
                               texts: FloatArray,
                               indices: ShortArray,
                               elementsCount: Int,
                               framesCount: Int,
                               interpolated: Boolean) = models.getOrPut(name) {
        val iCapacity = indices.size
        val originalVertexArray = if (interpolated) vertices else null

        val length = elementsCount * 3
        val vertexBufferBytes = length * 4
        val normalBufferBytes = length * 4
        val tcBufferBytes = elementsCount * 2 * 4
        val indexBufferBytes = iCapacity * 2

        val frames = Array(framesCount) { i ->
            val bufVertexDirect = ByteBuffer.allocateDirect(vertexBufferBytes).order(ByteOrder.nativeOrder())
            val bufVertex = bufVertexDirect.asFloatBuffer().apply {
                clear()
                put(vertices, i * length, length)
                position(0)
            }

            val bufNormalDirect = ByteBuffer.allocateDirect(normalBufferBytes).order(ByteOrder.nativeOrder())
            val bufNormal = bufNormalDirect.asFloatBuffer().apply {
                clear()
                put(normals, i * length, length)
                position(0)
            }

            val handleTemp = IntArray(1)

            gl.glGenBuffers(1, handleTemp, 0)
            val bufVertexHandle = handleTemp[0]
            gl.glBindBuffer(GL_ARRAY_BUFFER, bufVertexHandle)
            gl.glBufferData(GL_ARRAY_BUFFER, vertexBufferBytes, bufVertex, GL_STATIC_DRAW)
            gl.glBindBuffer(GL_ARRAY_BUFFER, 0)

            gl.glGenBuffers(1, handleTemp, 0)
            val bufNormalHandle = handleTemp[0]
            gl.glBindBuffer(GL_ARRAY_BUFFER, bufNormalHandle)
            gl.glBufferData(GL_ARRAY_BUFFER, normalBufferBytes, bufNormal, GL_STATIC_DRAW)
            gl.glBindBuffer(GL_ARRAY_BUFFER, 0)

            return@Array Model.Frame(bufNormalHandle, bufVertexHandle)
        }

        val bufTCDirect = ByteBuffer.allocateDirect(tcBufferBytes).order(ByteOrder.nativeOrder())
        val bufTC = bufTCDirect.asFloatBuffer().apply {
            clear()
            put(texts)
            position(0)
        }

        val bufIndexDirect = ByteBuffer.allocateDirect(indexBufferBytes).order(ByteOrder.nativeOrder())
        val bufIndex = bufIndexDirect.asShortBuffer().apply {
            clear()
            put(indices)
            position(0)
        }

        val indicesCount = bufIndex.capacity()
        val handleTemp = IntArray(2)
        gl.glGenBuffers(2, handleTemp, 0)

        val bufIndexHandle = handleTemp[0]
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufIndexHandle)
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBufferBytes, bufIndex, GL_STATIC_DRAW)
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

        val bufTCHandle = handleTemp[1]
        gl.glBindBuffer(GL_ARRAY_BUFFER, bufTCHandle)
        gl.glBufferData(GL_ARRAY_BUFFER, tcBufferBytes, bufTC, GL_STATIC_DRAW)
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0)

        return Model(name, gl, frames, elementsCount, indicesCount, originalVertexArray, bufTCHandle, bufIndexHandle)
    }

    private fun InputStream.assertChunk(header: String, buffer: ByteArray) {
        read(buffer)

        val value = buffer.toString(Charset.forName("UTF-8"))
        if (header != value) {
            throw  IllegalArgumentException("Unexpected chunk: $header!=$value")
        }
    }

    override fun close() {
        models.values.forEach(Model::unload)
        models.clear()
    }

}
