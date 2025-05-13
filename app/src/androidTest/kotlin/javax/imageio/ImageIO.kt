package javax.imageio

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.File
import java.io.IOException

object ImageIO {

    @JvmStatic
    fun read(file: File) =
        BitmapFactory.decodeFile(file.absolutePath)?.let(::BufferedImage)


    @JvmStatic
    @Throws(IOException::class)
    fun write(bitmap: RenderedImage, format: String, file: File): Boolean {
        check("png" == format) { "Expected PNG format, but got $format" }
        file.parentFile?.mkdirs()
        file.outputStream().use { bitmap.bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return true
    }

}
