package io.github.gmazzo.android.livewallpaper.weather.engine.models

import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import javax.microedition.khronos.opengles.GL10.GL_FLOAT
import javax.microedition.khronos.opengles.GL10.GL_MODULATE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE0
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE1
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_COORD_ARRAY
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_ENV
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_ENV_MODE
import javax.microedition.khronos.opengles.GL10.GL_TRIANGLES
import javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_SHORT
import javax.microedition.khronos.opengles.GL11
import javax.microedition.khronos.opengles.GL11.GL_ARRAY_BUFFER
import javax.microedition.khronos.opengles.GL11.GL_ELEMENT_ARRAY_BUFFER

class StaticModel(
    name: String,
    resId: Int,
    gl: GL11,
    indicesCount: Int,
    bufTCHandle: Int,
    bufIndexHandle: Int,
    private val frame: Frame,
) : Model(name, resId, gl, indicesCount, bufTCHandle, bufIndexHandle) {

    override fun render() {
        gl.glBindBuffer(GL_ARRAY_BUFFER, frame.bufVertexHandle)
        gl.glVertexPointer(3, GL_FLOAT, 0, 0)
        render(frame)
    }

    fun render(
        texture1: Texture,
        texture2: Texture,
        combine: Int,
    ) {
        gl.glActiveTexture(GL_TEXTURE0)
        gl.glBindTexture(GL_TEXTURE_2D, texture1.glId)
        gl.glBindBuffer(GL_ARRAY_BUFFER, bufTCHandle)
        gl.glTexCoordPointer(2, GL_FLOAT, 0, 0)
        gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE)
        gl.glActiveTexture(GL_TEXTURE1)
        gl.glEnable(GL_TEXTURE_2D)
        gl.glClientActiveTexture(GL_TEXTURE1)
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY)
        gl.glBindTexture(GL_TEXTURE_2D, texture2.glId)
        gl.glBindBuffer(GL_ARRAY_BUFFER, bufTCHandle)
        gl.glTexCoordPointer(2, GL_FLOAT, 0, 0)
        gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, combine)
        gl.glBindBuffer(GL_ARRAY_BUFFER, frame.bufVertexHandle)
        gl.glVertexPointer(3, GL_FLOAT, 0, 0)
        gl.glBindBuffer(GL_ARRAY_BUFFER, frame.bufNormalHandle)
        gl.glNormalPointer(GL_FLOAT, 0, 0)
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bufIndexHandle)
        gl.glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_SHORT, 0)
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0)
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        gl.glDisable(GL_TEXTURE_2D)
        gl.glActiveTexture(GL_TEXTURE0)
        gl.glClientActiveTexture(GL_TEXTURE0)
    }

    override fun collectBufferIds() = sequenceOf(
        frame.bufNormalHandle,
        frame.bufVertexHandle,
    )

}