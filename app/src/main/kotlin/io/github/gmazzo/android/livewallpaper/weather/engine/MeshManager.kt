package io.github.gmazzo.android.livewallpaper.weather.engine

import android.content.Context
import android.util.Log
import java.io.IOException
import javax.microedition.khronos.opengles.GL10

class MeshManager private constructor(private var context: Context) {
    private val MMPACKAGENAME: String = context.packageName
    private var lastMesh: Mesh? = null
    private var lastName: String? = null
    private val meshList: MutableMap<String, Mesh> = HashMap()

    private fun createFakeMesh(gl10: GL10, s: String) {
        val vertexList = floatArrayOf(
            -1.08213043E9f,
            0.0f,
            -1.08213043E9f,
            0.0f,
            0.0f,
            1.06535322E9f,
            1.06535322E9f,
            0.0f,
            -1.08213043E9f
        )
        val normalList = floatArrayOf(
            1.06535322E9f,
            0.0f,
            0.0f,
            1.06535322E9f,
            0.0f,
            0.0f,
            1.06535322E9f,
            0.0f,
            0.0f
        )
        val tcList = floatArrayOf(
            -1.08213043E9f,
            -1.08213043E9f,
            0.0f,
            1.06535322E9f,
            1.06535322E9f,
            -1.08213043E9f
        )
        val indexList = shortArrayOf(
            0.toShort(),
            1.toShort(),
            2.toShort(),
            1.toShort(),
            0.toShort(),
            2.toShort()
        )
        val mesh = Mesh()
        mesh.createFromArrays(gl10, vertexList, normalList, tcList, indexList, 3, 1, false)
        meshList[s] = mesh
    }

    private fun createMeshFromFile(gl10: GL10, s: String) {
        createMeshFromFile(gl10, s, false, null)
    }

    private fun createMeshFromFile(
        gl10: GL10,
        filename: String,
        willBeInterpolated: Boolean,
        container: Mesh?
    ) {
        var container = container
        if (isLoaded(filename)) {
            Log.v(TAG, "MeshManager: Already loaded $filename")
            return
        }
        val fileIsBinary: Boolean
        val resources = context.resources
        val resId = resources.getIdentifier(filename, "raw", this.MMPACKAGENAME)
        val firstFour = ByteArray(4)
        val inputstream = resources.openRawResource(resId)
        try {
            inputstream.read(firstFour, 0, 4)
            inputstream.close()
        } catch (e: IOException) {
        }
        fileIsBinary =
            if (firstFour[0] == 66.toByte() && firstFour[1] == 77.toByte() && firstFour[2] == 68.toByte() && firstFour[3] == 76.toByte()) {
                true
            } else if (firstFour[0] == 84.toByte() && firstFour[1] == 77.toByte() && firstFour[2] == 68.toByte() && firstFour[3] == 76.toByte()) {
                false
            } else {
                createFakeMesh(gl10, filename)
                try {
                    inputstream.close()
                    return
                } catch (e2: IOException) {
                    return
                }
            }
        if (container == null) {
            container = Mesh()
            val `is` = resources.openRawResource(resId)
            if (fileIsBinary) {
                container.createFromBinaryFile(gl10, `is`, filename, willBeInterpolated)
            } else {
                container.createFromTextFile(gl10, `is`, filename, willBeInterpolated)
            }
        }
        try {
            inputstream.close()
        } catch (e3: IOException) {
        }
        meshList[filename] = container
    }

    private fun getMeshByName(gl10: GL10, name: String): Mesh? {
        if (name == this.lastName) {
            return this.lastMesh
        }
        var mesh = meshList[name]
        if (mesh == null) {
            createMeshFromFile(gl10, name)
            mesh = meshList[name]
        }
        this.lastName = name
        this.lastMesh = mesh
        return mesh
    }

    private fun isLoaded(s: String): Boolean {
        return meshList.containsKey(s)
    }

    private fun setContext(context1: Context) {
        this.context = context1
    }

    private fun unload(gl10: GL10) {
        for (name in meshList.keys) {
            meshList[name]!!.unload(gl10)
        }
        meshList.clear()
        this.lastName = null
        this.lastMesh = null
    }

    companion object {
        private const val TAG = "GL Engine"
    }
}
