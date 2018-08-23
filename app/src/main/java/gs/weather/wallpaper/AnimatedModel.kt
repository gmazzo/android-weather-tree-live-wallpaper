package gs.weather.wallpaper

import gs.weather.engine.AnimPlayer
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10.GL_FLOAT
import javax.microedition.khronos.opengles.GL11

class AnimatedModel internal constructor(
        name: String,
        gl: GL11,
        frames: Array<Frame>,
        indicesCount: Int,
        bufTCHandle: Int,
        bufIndexHandle: Int,
        private val animator: AnimPlayer,
        private val elementsCount: Int,
        private val vertices: FloatArray,
        private val bufScratch: FloatBuffer) :
        Model(name, gl, frames, indicesCount, bufTCHandle, bufIndexHandle) {

    override fun render() {
        val frameNum = animator.currentFrame
        val frameBlendNum = animator.blendFrame
        val blendAmount = animator.blendFrameAmount

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
    override fun asMesh() = super.asMesh().also {
        it.numElements = elementsCount
        it.originalVertexArray = vertices
        it.bufScratch = bufScratch
    }

}
