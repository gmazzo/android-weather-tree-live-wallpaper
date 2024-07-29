package io.github.gmazzo.android.livewallpaper.weather.engine

import javax.microedition.khronos.opengles.GL11
import kotlin.random.Random

fun Random.nextFloat(min: Float, max: Float) = (nextFloat() * (max - min)) + min

fun <R> GL11.pushMatrix(block: GL11.() -> R): R {
    glPushMatrix()
    try {
        return block()

    } finally {
        glPopMatrix()
    }
}
