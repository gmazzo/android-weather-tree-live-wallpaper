package io.github.gmazzo.android.livewallpaper.weather.wallpaper

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import androidx.annotation.AnyRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import java.io.Closeable
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.opengles.GL10.GL_LINEAR
import javax.microedition.khronos.opengles.GL10.GL_LUMINANCE
import javax.microedition.khronos.opengles.GL10.GL_REPEAT
import javax.microedition.khronos.opengles.GL10.GL_RGB
import javax.microedition.khronos.opengles.GL10.GL_RGBA
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_MAG_FILTER
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_MIN_FILTER
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_WRAP_S
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_WRAP_T
import javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_BYTE
import javax.microedition.khronos.opengles.GL11
import kotlin.math.absoluteValue

class Textures(
    private val resources: Resources,
    private val gl: GL11
) : Closeable {
    private val textures = mutableMapOf<Int, Texture>()

    operator fun get(@AnyRes resId: Int) = textures.getOrPut(resId) {
        gl.glEnable(GL_TEXTURE_2D)

        val buffer = IntBuffer.allocate(1)
        gl.glGenTextures(1, buffer)

        val glId = buffer.get(0)
        gl.glBindTexture(GL_TEXTURE_2D, glId)
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        when (resources.getResourceTypeName(resId)) {
            "drawable" -> loadBitmap(resId)
            else -> loadTGA(resId)
        }

        return@getOrPut Texture(resId, glId)
    }

    private fun loadBitmap(@DrawableRes resId: Int) {
        val bitmap = BitmapFactory.decodeResource(resources, resId,
            BitmapFactory.Options().apply { inDither = false })
        try {
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)

        } finally {
            bitmap.recycle()
        }
    }

    private fun loadTGA(@RawRes resId: Int) {
        resources.openRawResource(resId).use { input ->
            val header = ByteArray(18)
            input.read(header)

            val width = (header[13].unsigned shl 8) + header[12].unsigned
            val height = (header[15].unsigned shl 8) + header[14].unsigned
            val bpp = header[16].unsigned

            if (width <= 0 || height <= 0 || bpp !in arrayOf(24, 32, 8)) {
                throw IOException("Invalid header data for resId=$resId")
            }

            val bytesPerPixel = bpp / 8
            val imageSize = bytesPerPixel * width * height
            val imageData = ByteArray(imageSize)
            input.read(imageData)

            (0 until (imageSize - 2) step bytesPerPixel).forEach { i ->
                val tmp = imageData[i]
                imageData[i] = imageData[i + 2]
                imageData[i + 2] = tmp
            }

            val format = when (bpp) {
                8 -> GL_LUMINANCE
                24 -> GL_RGB
                32 -> GL_RGBA
                else -> throw IllegalArgumentException("Unknown format for resId=$resId; bpp=$bpp")
            }

            gl.glTexImage2D(
                GL_TEXTURE_2D,
                0,
                format,
                width,
                height,
                0,
                format,
                GL_UNSIGNED_BYTE,
                ByteBuffer.wrap(imageData)
            )
        }
    }

    override fun close() {
        val ids = textures.values.map(Texture::glId).toIntArray()
        textures.clear()

        gl.glDeleteTextures(ids.size, ids, 0)
    }

    private val Byte.unsigned get() = toInt().absoluteValue

}
