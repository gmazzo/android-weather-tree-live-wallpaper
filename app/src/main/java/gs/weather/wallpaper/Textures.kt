package gs.weather.wallpaper

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import android.support.annotation.AnyRes
import android.support.annotation.RawRes
import gs.weather.engine.TextureManager
import java.io.Closeable
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.opengles.GL10.*
import javax.microedition.khronos.opengles.GL11
import kotlin.math.absoluteValue

class Textures(
        private val resources: Resources,
        private val gl: GL11,
        val manager: TextureManager /*TODO remove once done*/) : Closeable {
    private val textures = mutableMapOf<String, Texture>()

    operator fun get(name: String) = textures.getOrElse(name) { throw IllegalArgumentException("Unknown texture: $name") }

    private fun createTexture(name: String, loader: () -> Unit) = textures.getOrPut(name) {
        gl.glEnable(GL_TEXTURE_2D)
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, 9729.0f)
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, 9729.0f)
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, 10497.0f)
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, 10497.0f)

        val buffer = IntBuffer.allocate(1)
        gl.glGenTextures(1, buffer)

        val id = buffer.get(0)
        gl.glBindTexture(GL_TEXTURE_2D, id)
        loader.invoke()

        return Texture(gl, id, name).apply(manager::bind)
    }

    fun loadBitmap(name: String, @AnyRes resId: Int) = createTexture(name) {
        val bitmap = BitmapFactory.decodeResource(resources, resId,
                BitmapFactory.Options().apply { inDither = false })
        try {
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)

        } finally {
            bitmap.recycle()
        }
    }

    fun loadTGA(name: String, @RawRes resId: Int) = createTexture(name) {
        resources.openRawResource(resId).use { input ->
            val header = ByteArray(18)
            input.read(header)

            val width = (header[13].unsigned shl 8) + header[12].unsigned
            val height = (header[15].unsigned shl 8) + header[14].unsigned
            val bpp = header[16].unsigned

            if (width <= 0 || height <= 0 || bpp !in arrayOf(24, 32, 8)) {
                throw IOException("Invalid header data for name=$name")
            }

            val bytesPerPixel = bpp / 8
            val imageSize = bytesPerPixel * width * height
            val imageData = ByteArray(imageSize)
            input.read(imageData)

            var i = 0
            while (i < imageSize - 2) {
                val var2 = imageData[i]
                imageData[i] = imageData[i + 2]
                imageData[i + 2] = var2
                i += bytesPerPixel
            }

            val format = when (bpp) {
                8 -> GL_LUMINANCE
                24 -> GL_RGB
                32 -> GL_RGBA
                else -> throw IllegalArgumentException("Unknown format for name=$name; bpp=$bpp")
            }

            gl.glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, ByteBuffer.wrap(imageData))
        }
    }

    fun unload(texture: Texture) {
        if (textures.remove(texture.name) == null) {
            throw IllegalArgumentException("Texture is not from this container")
        }

        texture.unload()
    }

    override fun close() {
        textures.values.forEach(Texture::unload)
        textures.clear()
    }

    private val Byte.unsigned get() = toInt().absoluteValue

}