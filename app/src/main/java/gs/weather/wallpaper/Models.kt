package gs.weather.wallpaper

import android.content.res.Resources
import android.support.annotation.RawRes
import gs.weather.engine.MeshManager
import java.io.Closeable
import java.io.DataInputStream
import java.io.InputStream
import java.nio.charset.Charset
import javax.microedition.khronos.opengles.GL11
import javax.microedition.khronos.opengles.GL11.*

class Models(private val resources: Resources,
             private val gl: GL11,
             val manager: MeshManager /*TODO remove once done*/) : Closeable {
    private val models = mutableMapOf<String, Model>()

    operator fun get(name: String) = models.getOrElse(name) { throw IllegalArgumentException("Unknown model: $name") }

    fun loadBMDL(name: String, @RawRes rawId: Int) = models.getOrPut(name) {
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

            return@getOrPut loadWithArrays(name, vertices, normals, texts, indices, elements, frames)
        }
    }.apply(manager::bind) // TODO remove once done

    private fun loadWithArrays(name: String,
                               vertices: FloatArray,
                               normals: FloatArray,
                               texts: FloatArray,
                               indices: ShortArray,
                               elementsCount: Int,
                               framesCount: Int) = models.getOrPut(name) {
        val animated = framesCount > 0

        val frames = Array(framesCount) { i ->
            val bufVertex = (elementsCount * 3).asDirectFloatBuffer().apply {
                val length = capacity()
                put(vertices, i * length, length)
                position(0)
            }

            val bufNormal = (elementsCount * 3).asDirectFloatBuffer().apply {
                val length = capacity()
                put(normals, i * length, length)
                position(0)
            }

            val handleTemp = IntArray(2)
            gl.glGenBuffers(2, handleTemp, 0)

            val bufVertexHandle = handleTemp[0]
            gl.glBindBuffer(GL_ARRAY_BUFFER, bufVertexHandle)
            gl.glBufferData(GL_ARRAY_BUFFER, bufVertex.sizeInBytes, bufVertex, GL_STATIC_DRAW)

            val bufNormalHandle = handleTemp[1]
            gl.glBindBuffer(GL_ARRAY_BUFFER, bufNormalHandle)
            gl.glBufferData(GL_ARRAY_BUFFER, bufNormal.sizeInBytes, bufNormal, GL_STATIC_DRAW)

            gl.glBindBuffer(GL_ARRAY_BUFFER, 0)
            return@Array Model.Frame(bufNormalHandle, bufVertexHandle)
        }

        val bufTC = (elementsCount * 2).asDirectFloatBuffer().apply {
            put(texts)
            position(0)
        }

        val bufIndex = indices.size.asDirectShortBuffer().apply {
            put(indices)
            position(0)
        }

        val indicesCount = bufIndex.capacity()
        val handleTemp = IntArray(2)
        gl.glGenBuffers(2, handleTemp, 0)

        val bufIndexHandle = handleTemp[0]
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufIndexHandle)
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, bufIndex.sizeInBytes, bufIndex, GL_STATIC_DRAW)
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

        val bufTCHandle = handleTemp[1]
        gl.glBindBuffer(GL_ARRAY_BUFFER, bufTCHandle)
        gl.glBufferData(GL_ARRAY_BUFFER, bufTC.sizeInBytes, bufTC, GL_STATIC_DRAW)
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0)

        if (animated) {
            val bufScratch = (elementsCount * 3).asDirectFloatBuffer()

            return@getOrPut AnimatedModel(gl, name, frames, indicesCount, bufTCHandle, bufIndexHandle,
                    null, elementsCount, vertices, bufScratch)
        } else {
            return@getOrPut Model(gl, name, frames, indicesCount, bufTCHandle, bufIndexHandle)
        }
    }

    override fun close() {
        models.values.forEach(Model::unload)
        models.clear()
    }

    private fun InputStream.assertChunk(header: String, buffer: ByteArray) {
        read(buffer)

        val value = buffer.toString(Charset.forName("UTF-8"))
        if (header != value) {
            throw  IllegalArgumentException("Unexpected chunk: $header!=$value")
        }
    }

}
