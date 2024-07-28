//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package io.github.gmazzo.android.livewallpaper.weather.engine.models

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel

internal class TGALoader {
    @Throws(IOException::class)
    private fun loadCompressedTGA(var1: ReadableByteChannel): TGA {
        Log.v("GL Engine", " - reading compressed tga")
        val var9 = TGA()
        this.readBuffer(var1, var9.header)
        var9.width = (this.unsignedByteToInt(var9.header[10]) shl 8) + this.unsignedByteToInt(
            var9.header[9]
        )
        var9.height = (this.unsignedByteToInt(var9.header[12]) shl 8) + this.unsignedByteToInt(
            var9.header[11]
        )
        var9.bpp = this.unsignedByteToInt(var9.header[13])
        if (var9.width > 0 && var9.height > 0 && (var9.bpp == 24 || var9.bpp == 32)) {
            var9.bytesPerPixel = var9.bpp / 8
            var9.imageSize = var9.bytesPerPixel * var9.width * var9.height
            var9.imageData = ByteBuffer.allocate(var9.imageSize).apply {
                position(0)
                limit(capacity())
            }
            val var7 = var9.height * var9.width
            var var3 = 0
            var var2 = 0
            val var10 = ByteBuffer.allocate(var9.bytesPerPixel)

            var var4: Int
            do {
                var var8: Int
                try {
                    val var11 = ByteBuffer.allocate(1)
                    var11.clear()
                    var1.read(var11)
                    var11.flip()
                    var8 = this.unsignedByteToInt(var11.get())
                } catch (var12: IOException) {
                    throw IOException("Could not read RLE header")
                }

                var var5: Int
                var var6: Short
                if (var8 < 128) {
                    var6 = 0
                    var5 = var2

                    while (true) {
                        var2 = var5
                        var4 = var3
                        if (var6 >= var8 + 1) {
                            break
                        }

                        this.readBuffer(var1, var10)
                        var9.imageData!!
                            .put(var5, var10[2])
                            .put(var5 + 1, var10[1])
                            .put(var5 + 2, var10[0])
                        if (var9.bytesPerPixel == 4) {
                            var9.imageData!!.put(var5 + 3, var10[3])
                        }

                        var5 += var9.bytesPerPixel
                        ++var3
                        if (var3 > var7) {
                            throw IOException("Too many pixels read")
                        }

                        ++var6
                    }
                } else {
                    this.readBuffer(var1, var10)
                    var6 = 0
                    var5 = var2

                    while (true) {
                        var2 = var5
                        var4 = var3
                        if (var6 >= var8 - 127) {
                            break
                        }

                        var9.imageData!!
                            .put(var5, var10[2])
                            .put(var5 + 1, var10[1])
                            .put(var5 + 2, var10[0])
                        if (var9.bytesPerPixel == 4) {
                            var9.imageData!!.put(var5 + 3, var10[3])
                        }

                        var5 += var9.bytesPerPixel
                        ++var3
                        if (var3 > var7) {
                            throw IOException("Too many pixels read")
                        }

                        ++var6
                    }
                }

                var3 = var4
            } while (var4 < var7)

            return var9
        } else {
            throw IOException("Invalid header data")
        }
    }

    @Throws(IOException::class)
    private fun loadUncompressedTGA(var1: ReadableByteChannel): TGA {
        Log.v("GL Engine", " - reading uncompressed tga")
        val var4 = TGA()
        this.readBuffer(var1, var4.header)
        var4.width = (this.unsignedByteToInt(var4.header[10]) shl 8) + this.unsignedByteToInt(
            var4.header[9]
        )
        var4.height = (this.unsignedByteToInt(var4.header[12]) shl 8) + this.unsignedByteToInt(
            var4.header[11]
        )
        var4.bpp = this.unsignedByteToInt(var4.header[13])
        if (var4.width <= 0 || var4.height <= 0 || var4.bpp != 24 && var4.bpp != 32 && var4.bpp != 8) {
            throw IOException("Invalid header data")
        } else {
            var4.bytesPerPixel = var4.bpp / 8
            var4.imageSize = var4.bytesPerPixel * var4.width * var4.height
            var4.imageData = ByteBuffer.allocate(var4.imageSize)
            Log.v(
                "GL Engine",
                " - " + var4.width + "r" + var4.height + "r" + var4.bpp + "(" + var4.imageSize + ")"
            )
            this.readBuffer(var1, var4.imageData)

            var var3 = 0
            while (var3 < var4.imageSize - 2) {
                val var2 = var4.imageData!!.get(var3)
                var4.imageData!!
                    .put(var3, var4.imageData!!.get(var3 + 2))
                    .put(var3 + 2, var2)
                var3 += var4.bytesPerPixel
            }

            return var4
        }
    }

    @Throws(IOException::class)
    private fun readBuffer(var1: ReadableByteChannel, var2: ByteBuffer?) {
        while (var2!!.hasRemaining()) {
            var1.read(var2)
        }

        var2.flip()
    }

    private fun unsignedByteToInt(var1: Byte): Int {
        return var1.toInt() and 255
    }

    @Throws(IOException::class)
    fun loadTGA(var1: InputStream?): TGA {
        val var2 = ByteBuffer.allocate(3)
        val var3 = Channels.newChannel(var1)
        this.readBuffer(var3, var2)
        return if (uTGAcompare != var2 && ugTGAcompare != var2) {
            if (cTGAcompare == var2) {
                loadCompressedTGA(var3)
            } else {
                var3.close()
                throw IOException("TGA file be type 2 or type 10 ")
            }
        } else {
            loadUncompressedTGA(var3)
        }
    }

    internal inner class TGA {
        var bpp: Int = 0
        var bytesPerPixel: Int = 0
        var commentLength: Int = 0
        var header: ByteBuffer = ByteBuffer.allocate(15)
        var height: Int = 0
        var imageData: ByteBuffer? = null
        var imageSize: Int = 0
        val loader: TGALoader = this@TGALoader
        var type: Int = 0
        var width: Int = 0
    }

    companion object {
        private const val TAG = "GL Engine"
        private val cTGAcompare: ByteBuffer
        private val uTGAcompare: ByteBuffer
        private val ugTGAcompare: ByteBuffer

        init {
            val var0 = byteArrayOf(0, 0, 3)
            val var1 = byteArrayOf(0, 0, 2)
            val var2 = byteArrayOf(0, 0, 10)
            uTGAcompare = ByteBuffer.allocate(var1.size)
            uTGAcompare.put(var1)
            uTGAcompare.flip()
            cTGAcompare = ByteBuffer.allocate(var2.size)
            cTGAcompare.put(var2)
            cTGAcompare.flip()
            ugTGAcompare = ByteBuffer.allocate(var0.size)
            ugTGAcompare.put(var0)
            ugTGAcompare.flip()
        }
    }
}
