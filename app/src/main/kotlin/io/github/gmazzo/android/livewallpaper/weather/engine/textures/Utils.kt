package io.github.gmazzo.android.livewallpaper.weather.engine.textures

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

private fun Int.asDirectBuffer() =
    ByteBuffer.allocateDirect(this).order(ByteOrder.nativeOrder())

fun Int.asDirectShortBuffer(): ShortBuffer =
    (this * 2).asDirectBuffer().asShortBuffer()

fun Int.asDirectFloatBuffer(): FloatBuffer =
    (this * 4).asDirectBuffer().asFloatBuffer()

val ShortBuffer.sizeInBytes get() = capacity() * 2

val FloatBuffer.sizeInBytes get() = capacity() * 4

operator fun ShortBuffer.set(index: Int, value: Short) = put(index, value)!!

operator fun FloatBuffer.set(index: Int, value: Float) = put(index, value)!!
