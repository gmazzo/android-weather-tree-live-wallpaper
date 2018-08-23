package gs.weather.wallpaper

import gs.weather.engine.AnimPlayer
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10.*
import javax.microedition.khronos.opengles.GL11
import javax.microedition.khronos.opengles.GL11.GL_ARRAY_BUFFER
import javax.microedition.khronos.opengles.GL11.GL_ELEMENT_ARRAY_BUFFER

class AnimatedModel internal constructor(
        gl: GL11,
        name: String,
        frames: Array<Frame>,
        indicesCount: Int,
        bufTCHandle: Int,
        bufIndexHandle: Int,
        var animator: AnimPlayer?, // TODO make it private non-null once done
        private val elementsCount: Int,
        private val vertices: FloatArray,
        private val bufScratch: FloatBuffer) :
        Model(gl, name, frames, indicesCount, bufTCHandle, bufIndexHandle) {

    override fun render() {
        val frameNum = animator?.currentFrame ?: 0
        val frameBlendNum = animator?.blendFrame ?: 0
        val blendAmount = animator?.blendFrameAmount ?: 0f

        when {
            blendAmount < 0.01 -> renderFrame(frameNum)
            blendAmount > 0.99 -> renderFrame(frameBlendNum)
            else -> renderFrame(frameNum, frameBlendNum, blendAmount)
        }
    }

    private fun renderFrame(frame: Int, frameBlend: Int, blendAmount: Float) {
        val firstFrameOffset = elementsCount * frame * 3
        val blendFrameOffset = elementsCount * frameBlend * 3
        val blendAmountInverse = 1.0f - blendAmount

        with(bufScratch) {
            for (i in 0 until capacity()) {
                this[i] =
                        vertices[firstFrameOffset + i] * blendAmountInverse +
                        vertices[blendFrameOffset + i] * blendAmount
            }
            position(0)

            gl.glVertexPointer(3, GL_FLOAT, 0, this)
        }

        renderFrameShared(frame)
    }

    // TODO delete this once finished
    fun renderFrameMultiTexture(frameNum: Int, tex1: Texture, tex2: Texture, combine: Int, envMap: Boolean) {
        gl.glActiveTexture(GL_TEXTURE0)
        gl.glBindTexture(GL_TEXTURE_2D, tex1.id)
        if (envMap) {
            gl.glBindBuffer(GL_ARRAY_BUFFER, this.frames[frameNum].bufNormalHandle)
            gl.glTexCoordPointer(3, GL_FLOAT, 0, 0)
        } else {
            gl.glBindBuffer(GL_ARRAY_BUFFER, this.bufTCHandle)
            gl.glTexCoordPointer(2, GL_FLOAT, 0, 0)
        }
        gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE)
        gl.glActiveTexture(GL_TEXTURE1)
        gl.glEnable(GL_TEXTURE_2D)
        gl.glClientActiveTexture(GL_TEXTURE1)
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY)
        gl.glBindTexture(GL_TEXTURE_2D, tex2.id)
        gl.glBindBuffer(GL_ARRAY_BUFFER, this.bufTCHandle)
        gl.glTexCoordPointer(2, GL_FLOAT, 0, 0)
        gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, combine)
        gl.glBindBuffer(GL_ARRAY_BUFFER, this.frames[frameNum].bufVertexHandle)
        gl.glVertexPointer(3, GL_FLOAT, 0, 0)
        gl.glBindBuffer(GL_ARRAY_BUFFER, this.frames[frameNum].bufNormalHandle)
        gl.glNormalPointer(GL_FLOAT, 0, 0)
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bufIndexHandle)
        gl.glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_SHORT, 0)
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0)
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        gl.glDisable(GL_TEXTURE_2D)
        gl.glActiveTexture(GL_TEXTURE0)
        gl.glClientActiveTexture(GL_TEXTURE0)
    }

    // TODO delete this once finished
    override fun asMesh() = super.asMesh().also {
        it.numElements = elementsCount
        it.originalVertexArray = vertices
        it.bufScratch = bufScratch
    }

}
