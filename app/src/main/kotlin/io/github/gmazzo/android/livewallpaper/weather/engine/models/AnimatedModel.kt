package io.github.gmazzo.android.livewallpaper.weather.engine.models

import androidx.annotation.RawRes
import io.github.gmazzo.android.livewallpaper.weather.engine.AnimPlayer
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.set
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10.GL_FLOAT
import javax.microedition.khronos.opengles.GL11

class AnimatedModel internal constructor(
    name: String,
    @RawRes resId: Int,
    gl: GL11,
    indicesCount: Int,
    bufTCHandle: Int,
    bufIndexHandle: Int,
    private val elementsCount: Int,
    private val vertices: FloatArray,
    private val bufScratch: FloatBuffer,
    private val frames: List<Frame>,
) : Model(name, resId, gl, indicesCount, bufTCHandle, bufIndexHandle) {

    var animator: AnimPlayer? = null

    override fun render() {
        val frameNum = animator?.currentFrame ?: 0
        val frameBlendNum = animator?.blendFrame ?: 0
        val blendAmount = animator?.blendFrameAmount ?: 0f

        val firstFrameOffset = elementsCount * frameNum * 3
        val blendFrameOffset = elementsCount * frameBlendNum * 3
        val blendAmountInverse = 1f - blendAmount

        with(bufScratch) {
            for (i in 0 until capacity()) {
                this[i] =
                    vertices[firstFrameOffset + i] * blendAmountInverse +
                            vertices[blendFrameOffset + i] * blendAmount
            }
            position(0)

            gl.glVertexPointer(3, GL_FLOAT, 0, this)
        }

        render(frames[frameNum])
    }

    override fun collectBufferIds() =
        frames.asSequence().flatMap { sequenceOf(it.bufNormalHandle, it.bufVertexHandle) }

}
