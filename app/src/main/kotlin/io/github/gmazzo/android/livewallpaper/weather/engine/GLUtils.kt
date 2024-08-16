package io.github.gmazzo.android.livewallpaper.weather.engine

import androidx.annotation.FloatRange
import androidx.annotation.IntDef
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT
import javax.microedition.khronos.opengles.GL10.GL_FOG
import javax.microedition.khronos.opengles.GL10.GL_LIGHT1
import javax.microedition.khronos.opengles.GL10.GL_LIGHTING
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_ONE
import javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_PROJECTION
import javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL10.GL_ZERO
import javax.microedition.khronos.opengles.GL11
import javax.microedition.khronos.opengles.GL11.GL_CURRENT_COLOR

@Retention(AnnotationRetention.SOURCE)
@IntDef(GL_MODELVIEW, GL_PROJECTION, GL_TEXTURE)
annotation class GLMatrixMode

@Retention(AnnotationRetention.SOURCE)
@IntDef(GL_TEXTURE_2D, GL_FOG, GL_LIGHTING, GL_LIGHT1, GL_COLOR_BUFFER_BIT)
annotation class GLFlags

@Retention(AnnotationRetention.SOURCE)
@IntDef(GL_ZERO, GL_ONE, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
annotation class GLBlendFactor

fun <R> GL11.withFlags(@GLFlags vararg flags: Int, block: GL11.() -> R): R {
    val currentFlags = flags.filterNot(::glIsEnabled)

    currentFlags.forEach(::glEnable)
    try {
        return block()

    } finally {
        currentFlags.forEach(::glDisable)
    }
}

fun <R> GL11.withoutFlags(@GLFlags vararg flags: Int, block: GL11.() -> R): R {
    val currentFlags = flags.filter(::glIsEnabled)

    currentFlags.forEach(::glDisable)
    try {
        return block()

    } finally {
        currentFlags.forEach(::glEnable)
    }
}

fun <R> GL11.withColor(color: EngineColor, alpha: Float = color.a, block: GL11.() -> R) =
    withColor(color.r, color.g, color.b, alpha, block)

fun <R> GL11.withColor(
    @FloatRange(from = 0.0, to = 1.0) r: Float,
    @FloatRange(from = 0.0, to = 1.0) g: Float,
    @FloatRange(from = 0.0, to = 1.0) b: Float,
    @FloatRange(from = 0.0, to = 1.0) a: Float,
    block: GL11.() -> R
): R {
    val current = FloatBuffer.allocate(4)

    glGetFloatv(GL_CURRENT_COLOR, current)
    glColor4f(r, g, b, a)
    try {
        return block()

    } finally {
        glColor4f(current[0], current[1], current[2], current[3])
    }
}

fun <R> GL11.pushMatrix(@GLMatrixMode mode: Int, block: GL11.() -> R): R {
    glMatrixMode(mode)
    glPushMatrix()
    try {
        return block()

    } finally {
        glMatrixMode(mode)
        glPopMatrix()
    }
}
