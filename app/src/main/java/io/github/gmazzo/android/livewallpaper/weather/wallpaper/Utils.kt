package io.github.gmazzo.android.livewallpaper.weather.wallpaper

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

fun Int.asDirectBuffer() = ByteBuffer.allocateDirect(this)
        .order(ByteOrder.nativeOrder())!!

fun Int.asDirectShortBuffer() = (this * 2).asDirectBuffer().asShortBuffer()!!

fun Int.asDirectFloatBuffer() = (this * 4).asDirectBuffer().asFloatBuffer()!!

val ByteBuffer.sizeInBytes get() = capacity()

val ShortBuffer.sizeInBytes get() = capacity() * 2

val FloatBuffer.sizeInBytes get() = capacity() * 4

operator fun ByteBuffer.set(index: Int, value: Byte) = put(index, value)!!

operator fun ShortBuffer.set(index: Int, value: Short) = put(index, value)!!

operator fun FloatBuffer.set(index: Int, value: Float) = put(index, value)!!
