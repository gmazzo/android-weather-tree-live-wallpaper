//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hm.weather.engine;

import com.hm.weather.engine.Utility.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

class TGALoader {
    private static final String TAG = "GL Engine";
    private static final ByteBuffer cTGAcompare;
    private static final ByteBuffer uTGAcompare;
    private static final ByteBuffer ugTGAcompare;

    static {
        byte[] var0 = new byte[]{0, 0, 3};
        byte[] var1 = new byte[]{0, 0, 2};
        byte[] var2 = new byte[]{0, 0, 10};
        uTGAcompare = ByteBuffer.allocate(var1.length);
        uTGAcompare.put(var1);
        uTGAcompare.flip();
        cTGAcompare = ByteBuffer.allocate(var2.length);
        cTGAcompare.put(var2);
        cTGAcompare.flip();
        ugTGAcompare = ByteBuffer.allocate(var0.length);
        ugTGAcompare.put(var0);
        ugTGAcompare.flip();
    }

    TGALoader() {
    }

    private TGALoader.TGA loadCompressedTGA(ReadableByteChannel var1) throws IOException {
        Logger.v("GL Engine", " - reading compressed tga");
        TGALoader.TGA var9 = new TGALoader.TGA();
        this.readBuffer(var1, var9.header);
        var9.width = (this.unsignedByteToInt(var9.header.get(10)) << 8) + this.unsignedByteToInt(var9.header.get(9));
        var9.height = (this.unsignedByteToInt(var9.header.get(12)) << 8) + this.unsignedByteToInt(var9.header.get(11));
        var9.bpp = this.unsignedByteToInt(var9.header.get(13));
        if (var9.width > 0 && var9.height > 0 && (var9.bpp == 24 || var9.bpp == 32)) {
            var9.bytesPerPixel = var9.bpp / 8;
            var9.imageSize = var9.bytesPerPixel * var9.width * var9.height;
            var9.imageData = ByteBuffer.allocate(var9.imageSize);
            var9.imageData.position(0);
            var9.imageData.limit(var9.imageData.capacity());
            int var7 = var9.height * var9.width;
            int var3 = 0;
            int var2 = 0;
            ByteBuffer var10 = ByteBuffer.allocate(var9.bytesPerPixel);

            int var4;
            do {
                int var8;
                try {
                    ByteBuffer var11 = ByteBuffer.allocate(1);
                    var11.clear();
                    var1.read(var11);
                    var11.flip();
                    var8 = this.unsignedByteToInt(var11.get());
                } catch (IOException var12) {
                    throw new IOException("Could not read RLE header");
                }

                int var5;
                short var6;
                if (var8 < 128) {
                    var6 = 0;
                    var5 = var2;

                    while (true) {
                        var2 = var5;
                        var4 = var3;
                        if (var6 >= var8 + 1) {
                            break;
                        }

                        this.readBuffer(var1, var10);
                        var9.imageData.put(var5, var10.get(2));
                        var9.imageData.put(var5 + 1, var10.get(1));
                        var9.imageData.put(var5 + 2, var10.get(0));
                        if (var9.bytesPerPixel == 4) {
                            var9.imageData.put(var5 + 3, var10.get(3));
                        }

                        var5 += var9.bytesPerPixel;
                        ++var3;
                        if (var3 > var7) {
                            throw new IOException("Too many pixels read");
                        }

                        ++var6;
                    }
                } else {
                    this.readBuffer(var1, var10);
                    var6 = 0;
                    var5 = var2;

                    while (true) {
                        var2 = var5;
                        var4 = var3;
                        if (var6 >= var8 - 127) {
                            break;
                        }

                        var9.imageData.put(var5, var10.get(2));
                        var9.imageData.put(var5 + 1, var10.get(1));
                        var9.imageData.put(var5 + 2, var10.get(0));
                        if (var9.bytesPerPixel == 4) {
                            var9.imageData.put(var5 + 3, var10.get(3));
                        }

                        var5 += var9.bytesPerPixel;
                        ++var3;
                        if (var3 > var7) {
                            throw new IOException("Too many pixels read");
                        }

                        ++var6;
                    }
                }

                var3 = var4;
            } while (var4 < var7);

            return var9;
        } else {
            throw new IOException("Invalid header data");
        }
    }

    private TGALoader.TGA loadUncompressedTGA(ReadableByteChannel var1) throws IOException {
        Logger.v("GL Engine", " - reading uncompressed tga");
        TGALoader.TGA var4 = new TGALoader.TGA();
        this.readBuffer(var1, var4.header);
        var4.width = (this.unsignedByteToInt(var4.header.get(10)) << 8) + this.unsignedByteToInt(var4.header.get(9));
        var4.height = (this.unsignedByteToInt(var4.header.get(12)) << 8) + this.unsignedByteToInt(var4.header.get(11));
        var4.bpp = this.unsignedByteToInt(var4.header.get(13));
        if (var4.width <= 0 || var4.height <= 0 || var4.bpp != 24 && var4.bpp != 32 && var4.bpp != 8) {
            throw new IOException("Invalid header data");
        } else {
            var4.bytesPerPixel = var4.bpp / 8;
            var4.imageSize = var4.bytesPerPixel * var4.width * var4.height;
            var4.imageData = ByteBuffer.allocate(var4.imageSize);
            Logger.v("GL Engine", " - " + var4.width + "x" + var4.height + "x" + var4.bpp + "(" + var4.imageSize + ")");
            this.readBuffer(var1, var4.imageData);

            for (int var3 = 0; var3 < var4.imageSize - 2; var3 += var4.bytesPerPixel) {
                byte var2 = var4.imageData.get(var3);
                var4.imageData.put(var3, var4.imageData.get(var3 + 2));
                var4.imageData.put(var3 + 2, var2);
            }

            return var4;
        }
    }

    private void readBuffer(ReadableByteChannel var1, ByteBuffer var2) throws IOException {
        while (var2.hasRemaining()) {
            var1.read(var2);
        }

        var2.flip();
    }

    private int unsignedByteToInt(byte var1) {
        return var1 & 255;
    }

    public TGALoader.TGA loadTGA(InputStream var1) throws IOException {
        ByteBuffer var2 = ByteBuffer.allocate(3);
        ReadableByteChannel var3 = Channels.newChannel(var1);
        this.readBuffer(var3, var2);
        if (!uTGAcompare.equals(var2) && !ugTGAcompare.equals(var2)) {
            if (cTGAcompare.equals(var2)) {
                return this.loadCompressedTGA(var3);
            } else {
                var3.close();
                throw new IOException("TGA file be type 2 or type 10 ");
            }
        } else {
            return this.loadUncompressedTGA(var3);
        }
    }

    class TGA {
        int bpp;
        int bytesPerPixel;
        int commentLength;
        ByteBuffer header = ByteBuffer.allocate(15);
        int height;
        ByteBuffer imageData;
        int imageSize;
        final TGALoader loader = TGALoader.this;
        int type;
        int width;

        public TGA() {
        }
    }
}
