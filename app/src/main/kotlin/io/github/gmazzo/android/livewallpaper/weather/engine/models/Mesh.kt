package io.github.gmazzo.android.livewallpaper.weather.engine.models

import android.util.Log
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.math.max
import kotlin.math.min

class Mesh {
    private var bufIndex: ShortBuffer? = null
    private var bufIndexDirect: ByteBuffer? = null
    var bufIndexHandle: Int = 0
    var bufScratch: FloatBuffer? = null
    private var bufTC: FloatBuffer? = null
    private var bufTCDirect: ByteBuffer? = null
    var bufTCHandle: Int = 0
    lateinit var frames: Array<Frame?>
    var meshName: String? = null
    var numElements: Int = 0
    var numIndices: Int = 0
    protected var numTriangles: Int = 0
    var originalVertexArray: FloatArray? = null

    class Frame {
        lateinit var bufNormal: FloatBuffer
        lateinit var bufNormalDirect: ByteBuffer
        var bufNormalHandle: Int = 0
        lateinit var bufVertex: FloatBuffer
        lateinit var bufVertexDirect: ByteBuffer
        var bufVertexHandle: Int = 0
    }

    internal inner class Tag(i: Int) {
        private val normal = FloatArray((i * 3))
        private val position = FloatArray((i * 3))

        fun addNormal(f: Float, f1: Float, f2: Float, i: Int) {
            normal[i * 3] = f
            normal[i * 3 + 1] = f1
            normal[i * 3 + 2] = f2
        }

        fun addPosition(f: Float, f1: Float, f2: Float, i: Int) {
            position[i * 3] = f
            position[i * 3 + 1] = f1
            position[i * 3 + 2] = f2
        }
    }

    private fun allocateScratchBuffers() {
        this.bufScratch = ByteBuffer.allocateDirect(this.numElements * 3 * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    }

    private fun createFromArrays(
        gl: GL10,
        vertexs: FloatArray?,
        normals: FloatArray?,
        tcs: FloatArray?,
        indices: ShortArray,
        num_elements: Int,
        num_frames: Int,
        willBeInterpolated: Boolean
    ) {
        val useVertexBufferObjects = gl is GL11
        if (useVertexBufferObjects) {
            Log.v(TAG, " - using GL11 vertex buffer objects")
        }
        val iCapacity = indices.size
        this.numTriangles = indices.size / 3
        this.frames = arrayOfNulls(num_frames)
        this.numElements = num_elements
        if (willBeInterpolated) {
            this.originalVertexArray = vertexs
            Log.v(TAG, " - preparing for interpolated animation")
        } else {
            this.originalVertexArray = null
        }
        if (this.meshName == null) {
            this.meshName = "CreatedFromArrays"
        }
        val length = num_elements * 3
        val vertexBufferBytes = length * 4
        val normalBufferBytes = length * 4
        val tcBufferBytes = (num_elements * 2) * 4
        val indexBufferBytes = iCapacity * 2
        for (i in 0 until num_frames) {
            val frame = Frame()
            frames[i] = frame
            frame.bufVertexDirect = ByteBuffer.allocateDirect(vertexBufferBytes)
            frame.bufVertexDirect.order(ByteOrder.nativeOrder())
            frame.bufVertex = frame.bufVertexDirect.asFloatBuffer()
            frame.bufVertex.clear()
            frame.bufVertex.put(vertexs, i * length, length)
            frame.bufVertex.position(0)
            frame.bufNormalDirect = ByteBuffer.allocateDirect(normalBufferBytes)
            frame.bufNormalDirect.order(ByteOrder.nativeOrder())
            frame.bufNormal = frame.bufNormalDirect.asFloatBuffer()
            frame.bufNormal.clear()
            frame.bufNormal.put(normals, i * length, length)
            frame.bufNormal.position(0)
            if (useVertexBufferObjects) {
                val gl11 = gl as GL11
                val handleTemp = IntArray(1)
                gl11.glGenBuffers(1, handleTemp, 0)
                frame.bufVertexHandle = handleTemp[0]
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, frame.bufVertexHandle)
                gl11.glBufferData(GL11.GL_ARRAY_BUFFER, vertexBufferBytes, frame.bufVertex, 35044)
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0)
                gl11.glGenBuffers(1, handleTemp, 0)
                frame.bufNormalHandle = handleTemp[0]
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, frame.bufNormalHandle)
                gl11.glBufferData(GL11.GL_ARRAY_BUFFER, normalBufferBytes, frame.bufNormal, 35044)
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0)
            }
        }
        this.bufTCDirect = ByteBuffer.allocateDirect(tcBufferBytes).order(ByteOrder.nativeOrder())
        this.bufTC = bufTCDirect!!.asFloatBuffer().apply {
            clear()
            put(tcs)
            position(0)
        }
        this.bufIndexDirect = ByteBuffer.allocateDirect(indexBufferBytes).order(ByteOrder.nativeOrder())
        this.bufIndex = bufIndexDirect!!.asShortBuffer().apply {
            clear()
            put(indices)
            position(0)
        }
        this.numIndices = bufIndex!!.capacity()
        if (useVertexBufferObjects) {
            val gl11 = gl as GL11
            val handleTemp = IntArray(1)
            gl11.glGenBuffers(1, handleTemp, 0)
            this.bufIndexHandle = handleTemp[0]
            gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, this.bufIndexHandle)
            gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, indexBufferBytes, this.bufIndex, 35044)
            gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0)
            gl11.glGenBuffers(1, handleTemp, 0)
            this.bufTCHandle = handleTemp[0]
            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.bufTCHandle)
            gl11.glBufferData(GL11.GL_ARRAY_BUFFER, tcBufferBytes, this.bufTC, 35044)
            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0)
        }

        // this.numElements = 0;
        this.numTriangles = 0
        this.bufIndex = null
        this.bufIndexDirect = null
        this.bufTC = null
        this.bufTCDirect = null
    }

    fun createFromBinaryFile(
        gl: GL10,
        inputstream: InputStream?,
        name: String?,
        willBeInterpolated: Boolean
    ) {
        val tagList: HashMap<*, *> = HashMap<Any?, Any?>()
        Log.v(TAG, " - reading as binary")
        this.meshName = name
        val dataInputStream = DataInputStream(inputstream)
        val segchars = ByteArray(4)
        try {
            dataInputStream.read(segchars, 0, 4)
            if (segchars[0] == 66.toByte() && segchars[1] == 77.toByte() && segchars[2] == 68.toByte() && segchars[3] == 76.toByte()) {
                try {
                    val fileVersion = dataInputStream.readInt()
                    val fileElements = dataInputStream.readInt()
                    val fileFrames = dataInputStream.readInt()
                    Log.v(
                        TAG,
                        "version: $fileVersion elements: $fileElements frames: $fileFrames"
                    )
                    dataInputStream.skip(12)
                    dataInputStream.skip(4)
                    dataInputStream.read(segchars, 0, 4)
                    if (segchars[0] == 87.toByte() && segchars[1] == 73.toByte() && segchars[2] == 78.toByte() && segchars[3] == 68.toByte()) {
                        var i: Int
                        val numWindings = dataInputStream.readInt()
                        val indices = ShortArray((numWindings * 3))
                        dataInputStream.skip(8)
                        var curReadIndex = 0
                        i = 0
                        while (i < numWindings) {
                            indices[curReadIndex] = dataInputStream.readShort()
                            indices[curReadIndex + 1] = dataInputStream.readShort()
                            indices[curReadIndex + 2] = dataInputStream.readShort()
                            curReadIndex += 3
                            i++
                        }
                        try {
                            dataInputStream.skip(4)
                            dataInputStream.read(segchars, 0, 4)
                            if (segchars[0] == 84.toByte() && segchars[1] == 69.toByte() && segchars[2] == 88.toByte() && segchars[3] == 84.toByte()) {
                                val numTC = dataInputStream.readInt()
                                val tcList = FloatArray((numTC * 2))
                                dataInputStream.skip(8)
                                curReadIndex = 0
                                i = 0
                                while (i < numTC) {
                                    tcList[curReadIndex] = dataInputStream.readFloat()
                                    tcList[curReadIndex + 1] = dataInputStream.readFloat()
                                    curReadIndex += 2
                                    i++
                                }
                                try {
                                    dataInputStream.skip(4)
                                    dataInputStream.read(segchars, 0, 4)
                                    if (segchars[0] == 86.toByte() && segchars[1] == 69.toByte() && segchars[2] == 82.toByte() && segchars[3] == 84.toByte()) {
                                        val numVertices = dataInputStream.readInt()
                                        val vertexList =
                                            FloatArray(((numVertices * 3) * fileFrames))
                                        var vertScale = dataInputStream.readInt()
                                        if (vertScale == 0) {
                                            vertScale = 128
                                        }
                                        Log.i(TAG, "vertScale=$vertScale")
                                        dataInputStream.skip(4)
                                        val n = numVertices * fileFrames
                                        curReadIndex = 0
                                        i = 0
                                        while (i < n) {
                                            if (fileVersion >= 4) {
                                                vertexList[curReadIndex] =
                                                    (dataInputStream.readShort()
                                                        .toFloat()) / (vertScale.toFloat())
                                                vertexList[curReadIndex + 1] =
                                                    (dataInputStream.readShort()
                                                        .toFloat()) / (vertScale.toFloat())
                                                vertexList[curReadIndex + 2] =
                                                    (dataInputStream.readShort()
                                                        .toFloat()) / (vertScale.toFloat())
                                                curReadIndex += 3
                                            } else {
                                                vertexList[curReadIndex] =
                                                    dataInputStream.readFloat()
                                                vertexList[curReadIndex + 1] =
                                                    dataInputStream.readFloat()
                                                vertexList[curReadIndex + 2] =
                                                    dataInputStream.readFloat()
                                                curReadIndex += 3
                                            }
                                            i++
                                        }
                                        try {
                                            dataInputStream.skip(4)
                                            dataInputStream.read(segchars, 0, 4)
                                            if (segchars[0] == 78.toByte() && segchars[1] == 79.toByte() && segchars[2] == 82.toByte() && segchars[3] == 77.toByte()) {
                                                val numNormals = dataInputStream.readInt()
                                                val normalList =
                                                    FloatArray(((numNormals * 3) * fileFrames))
                                                dataInputStream.skip(8)
                                                curReadIndex = 0
                                                i = 0
                                                while (i < numNormals * fileFrames) {
                                                    if (fileVersion >= 3) {
                                                        normalList[curReadIndex] =
                                                            (dataInputStream.readByte()
                                                                .toFloat()) / 127.0f
                                                        normalList[curReadIndex + 1] =
                                                            (dataInputStream.readByte()
                                                                .toFloat()) / 127.0f
                                                        normalList[curReadIndex + 2] =
                                                            (dataInputStream.readByte()
                                                                .toFloat()) / 127.0f
                                                        curReadIndex += 3
                                                    } else {
                                                        normalList[curReadIndex] =
                                                            dataInputStream.readFloat()
                                                        normalList[curReadIndex + 1] =
                                                            dataInputStream.readFloat()
                                                        normalList[curReadIndex + 2] =
                                                            dataInputStream.readFloat()
                                                        curReadIndex += 3
                                                    }
                                                    i++
                                                }
                                                createFromArrays(
                                                    gl,
                                                    vertexList,
                                                    normalList,
                                                    tcList,
                                                    indices,
                                                    fileElements,
                                                    fileFrames,
                                                    willBeInterpolated
                                                )
                                                if (tagList.size > 0) {
                                                    return
                                                }
                                                return
                                            }
                                            Log.v(TAG, " - invalid chunk tag: NORM")
                                            return
                                        } catch (ex2: IOException) {
                                            ex2.printStackTrace()
                                            return
                                        }
                                    }
                                    Log.v(TAG, " - invalid chunk tag: BVRT")
                                    return
                                } catch (ex22: IOException) {
                                    ex22.printStackTrace()
                                    return
                                }
                            }
                            Log.v(TAG, " - invalid chunk tag: TEXT")
                            return
                        } catch (ex222: IOException) {
                            ex222.printStackTrace()
                            return
                        }
                    }
                    Log.v(TAG, " - invalid chunk tag: WIND")
                    return
                } catch (ex2222: IOException) {
                    Log.v(TAG, " - ERROR reading model WIND!")
                    ex2222.printStackTrace()
                    return
                }
            }
            Log.v(TAG, " - invalid chunk tag: BMDL")
        } catch (ex22222: IOException) {
            Log.v(TAG, " - ERROR reading model BMDL!")
            ex22222.printStackTrace()
        }
    }

    fun createFromTextFile(gl: GL10?, inputstream: InputStream?, s: String?, flag: Boolean) {
    }

    fun render(gl10: GL10) {
        renderFrame(gl10, 0)
    }

    fun renderFrame(gl10: GL10, frameNum: Int) {
        var frameNum = frameNum
        if (frameNum >= frames.size || frameNum < 0) {
            Log.v(
                TAG,
                "ERROR: Mesh.renderFrame (" + this.meshName + ") given a frame outside of frames.length: " + frameNum
            )
            frameNum = frames.size - 1
        }
        if (gl10 is GL11) {
            renderFrame_gl11(gl10, frameNum)
            return
        }
        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, frames[frameNum]!!.bufVertex)
        gl10.glNormalPointer(GL10.GL_FLOAT, 0, frames[frameNum]!!.bufNormal)
        gl10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, this.bufTC)
        gl10.glDrawElements(
            GL10.GL_TRIANGLES,
            this.numIndices,
            GL10.GL_UNSIGNED_SHORT,
            this.bufIndex
        )
    }

    fun renderFrameInterpolated(gl: GL10, frameNum: Int, frameBlendNum: Int, blendAmount: Float) {
        var frameNum = frameNum
        var frameBlendNum = frameBlendNum
        if (this.originalVertexArray == null) {
            renderFrame(gl, frameNum)
        } else if ((blendAmount.toDouble()) < 0.01) {
            renderFrame(gl, frameNum)
        } else if ((blendAmount.toDouble()) > 0.99) {
            renderFrame(gl, frameBlendNum)
        } else {
            var sb: StringBuilder
            if (frameNum >= frames.size || frameNum < 0) {
                sb = StringBuilder("ERROR: Mesh.renderFrameInterpolated (")
                sb.append(this.meshName)
                sb.append(") given a frame outside of frames.length: ")
                sb.append(frameNum)
                Log.v(TAG, sb.toString())
                frameNum = frames.size - 1
            }
            if (frameBlendNum >= frames.size || frameBlendNum < 0) {
                sb = StringBuilder("ERROR: Mesh.renderFrameInterpolated (")
                sb.append(this.meshName)
                sb.append(") given a blendframe outside of frames.length: ")
                sb.append(frameBlendNum)
                Log.v(TAG, sb.toString())
                frameBlendNum = frames.size - 1
            }
            if (this.bufScratch == null) {
                allocateScratchBuffers()
                Log.v(TAG, this.meshName + " allocated animation buffers")
            }
            val firstFrameOffset = (this.numElements * frameNum) * 3
            val blendFrameOffset = (this.numElements * frameBlendNum) * 3
            val oneminusblend = 1.0f - blendAmount
            var i = 0
            val c = bufScratch!!.capacity()
            while (i < c) {
                bufScratch!!.put(
                    i,
                    (originalVertexArray!![firstFrameOffset + i] * oneminusblend) +
                            (originalVertexArray!![blendFrameOffset + i] * blendAmount)
                )
                i++
            }
            bufScratch!!.position(0)

            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.bufScratch)
            if (gl is GL11) {
                gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, frames[frameNum]!!.bufNormalHandle)
                gl.glNormalPointer(GL10.GL_FLOAT, 0, 0)

                gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.bufTCHandle)
                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, 0)

                gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, this.bufIndexHandle)
                gl.glDrawElements(GL10.GL_TRIANGLES, this.numIndices, GL10.GL_UNSIGNED_SHORT, 0)

                gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0)
                gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0)
            } else {
                gl.glNormalPointer(GL10.GL_FLOAT, 0, frames[frameNum]!!.bufNormal)
                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, this.bufTC)
                gl.glDrawElements(
                    GL10.GL_TRIANGLES,
                    this.numIndices,
                    GL10.GL_UNSIGNED_SHORT,
                    this.bufIndex
                )
            }
        }
    }

    fun renderFrameMultiTexture(
        gl11: GL11,
        frameNum: Int,
        tex1: Int,
        tex2: Int,
        combine: Int,
        envMap: Boolean
    ) {
        gl11.glActiveTexture(GL10.GL_TEXTURE0)
        gl11.glBindTexture(GL10.GL_TEXTURE_2D, tex1)
        if (envMap) {
            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, frames[frameNum]!!.bufNormalHandle)
            gl11.glTexCoordPointer(3, GL10.GL_FLOAT, 0, 0)
        } else {
            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.bufTCHandle)
            gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 0, 0)
        }
        gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE)
        gl11.glActiveTexture(GL10.GL_TEXTURE1)
        gl11.glEnable(GL10.GL_TEXTURE_2D)
        gl11.glClientActiveTexture(GL10.GL_TEXTURE1)
        gl11.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl11.glBindTexture(GL10.GL_TEXTURE_2D, tex2)
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.bufTCHandle)
        gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 0, 0)
        gl11.glTexEnvi(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, combine)
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, frames[frameNum]!!.bufVertexHandle)
        gl11.glVertexPointer(3, GL10.GL_FLOAT, 0, 0)
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, frames[frameNum]!!.bufNormalHandle)
        gl11.glNormalPointer(GL10.GL_FLOAT, 0, 0)
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, this.bufIndexHandle)
        gl11.glDrawElements(GL10.GL_TRIANGLES, this.numIndices, GL10.GL_UNSIGNED_SHORT, 0)
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0)
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0)
        gl11.glDisable(GL10.GL_TEXTURE_2D)
        gl11.glActiveTexture(GL10.GL_TEXTURE0)
        gl11.glClientActiveTexture(GL10.GL_TEXTURE0)
    }

    fun renderFrame_gl11(gl11: GL11, frameNum: Int) {
        renderFrame_gl11_setup(gl11, frameNum)
        renderFrame_gl11_render(gl11)
        renderFrame_gl11_clear(gl11)
    }

    fun renderFrame_gl11_clear(gl11: GL11) {
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0)
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun renderFrame_gl11_render(gl11: GL11) {
        gl11.glDrawElements(GL10.GL_TRIANGLES, this.numIndices, GL10.GL_UNSIGNED_SHORT, 0)
    }

    fun renderFrame_gl11_setup(gl11: GL11, frameNum: Int) {
        val frame = max(min(frameNum, frames.size - 1), 0)
        if (frame != frameNum) {
            Log.v(
                TAG,
                "ERROR: Mesh.renderFrame (" + this.meshName + ") given a frame outside of frames.length: " + frameNum
            )
        }

        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, frames[frame]!!.bufVertexHandle)
        gl11.glVertexPointer(3, GL10.GL_FLOAT, 0, 0)
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, frames[frame]!!.bufNormalHandle)
        gl11.glNormalPointer(GL10.GL_FLOAT, 0, 0)
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, this.bufTCHandle)
        gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 0, 0)
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, this.bufIndexHandle)
    }

    companion object {
        private const val TAG = "GL Engine"
    }
}
