package io.github.gmazzo.android.livewallpaper.weather.engine

import androidx.annotation.IntDef
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_PROJECTION
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL11

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
    glMatrixMode(mode)
    glPushMatrix()
    try {
        return block()

    } finally {
        glMatrixMode(mode)
        glPopMatrix()
    }
}
