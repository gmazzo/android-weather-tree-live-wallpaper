package io.github.gmazzo.android.livewallpaper.weather.engine

import androidx.annotation.IntDef
import java.nio.IntBuffer
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_PROJECTION
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL11
import javax.microedition.khronos.opengles.GL11.GL_MATRIX_MODE

@Retention(AnnotationRetention.SOURCE)
@IntDef(GL_MODELVIEW, GL_PROJECTION, GL_TEXTURE)
annotation class GLMatrixMode

@Retention(AnnotationRetention.SOURCE)
@IntDef(GL_TEXTURE_2D)
annotation class GLFlags

fun <R> GL11.withFlags(@GLFlags vararg flags: Int, block: GL11.() -> R): R {
    val currentFlags = flags.filterNot(::glIsEnabled)

    currentFlags.forEach(::glEnable)
    try {
        return block()

    } finally {
        currentFlags.forEach(::glDisable)
    }
}

fun <R> GL11.pushMatrix(@GLMatrixMode mode: Int, block: GL11.() -> R): R {
    val currentMode = currentMatrixMode

    glMatrixMode(mode)
    glPushMatrix()
    try {
        return block()

    } finally {
        check(currentMatrixMode == mode) {
            "glPushMatrix/glPopMatrix was called inside the block, expected $mode but was $currentMatrixMode"
        }
        glPopMatrix()
        glMatrixMode(currentMode)
    }
}


private val intBuffer1 = IntBuffer.allocate(1)

@GLMatrixMode private val GL11.currentMatrixMode: Int
    get() = glGetIntegerv(GL_MATRIX_MODE, intBuffer1).let { intBuffer1[0] }
