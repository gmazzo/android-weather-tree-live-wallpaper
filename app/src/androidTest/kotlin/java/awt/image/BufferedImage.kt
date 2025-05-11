package java.awt.image

import android.graphics.Bitmap

class BufferedImage(override val bitmap: Bitmap) : RenderedImage {

    constructor(width: Int, height: Int, type: Int) : this(
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    )

    val width = bitmap.width

    val height = bitmap.height

    fun getRGB(x: Int, y: Int) =
        bitmap.getPixel(x, y)

    fun setRGB(x: Int, y: Int, color: Int) {
        bitmap.setPixel(x, y, color)
    }

}
