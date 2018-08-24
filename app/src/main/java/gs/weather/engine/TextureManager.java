package gs.weather.engine;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.opengl.GLUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import gs.weather.engine.Utility.Logger;

import static javax.microedition.khronos.opengles.GL10.GL_LINEAR;
import static javax.microedition.khronos.opengles.GL10.GL_REPEAT;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL11.GL_GENERATE_MIPMAP;

public class TextureManager {
    private static final String SIZE_FILE_SUFFIX = "_size";
    private static final String TAG = "GL Engine";
    private Context context;
    private int lastId = 0;
    private String lastPath = null;
    private boolean pref_useMipMaps = false;
    private HashMap<String, Integer> texIDs = new HashMap();

    private TextureManager(Context context) {
        this.context = context;
    }

    private static boolean bitmapSanityCheck(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int k = 2;
        for (int l = 0; l < 12; l++) {
            if (w == k || h == k) {
                return true;
            }
            k *= 2;
        }
        return false;
    }

    private static int closestPowerOfTwo(int input) {
        int i = 0;
        int j = 1;
        for (int k = 0; k <= 10; k++) {
            if (Math.abs(input - j) < Math.abs(input - i)) {
                i = j;
            }
            j *= 2;
        }
        return i;
    }

    private static boolean copyImageToCache(Context context, String from, String to) {
        Logger.v(TAG, "copyImageToCache: " + from + " --> " + to);
        Bitmap bitmap = getSizedAbitraryBitmap(context, from);
        if (bitmap == null) {
            Logger.v(TAG, "  - bitmap was null!");
            return false;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float r = (float) (w / h);
        if (!bitmapSanityCheck(bitmap)) {
            bitmap = forceToSanity(bitmap);
        }
        try {
            FileOutputStream outFileStream = context.openFileOutput(to, 0);
            bitmap.compress(CompressFormat.JPEG, 95, outFileStream);
            outFileStream.close();
            Logger.v(TAG, "  - bitmap copied");
            DataOutputStream sizeDataStream = new DataOutputStream(context.openFileOutput(to + SIZE_FILE_SUFFIX, 0));
            sizeDataStream.writeInt(w);
            sizeDataStream.writeInt(h);
            sizeDataStream.writeFloat(r);
            sizeDataStream.close();
            Logger.v(TAG, "  - bitmap size cached");
            Logger.v(TAG, "  - success!");
            bitmap.recycle();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.v(TAG, "  - Failed to copy image!");
            bitmap.recycle();
            return false;
        }
    }

    private static Bitmap forceToSanity(Bitmap bitmap) {
        int new_w;
        int new_h;
        int org_w = bitmap.getWidth();
        int org_h = bitmap.getHeight();
        if (org_w > org_h) {
            new_w = closestPowerOfTwo(org_w);
            new_h = closestPowerOfTwo((org_h * new_w) / org_w);
        } else {
            new_h = closestPowerOfTwo(org_h);
            new_w = closestPowerOfTwo((int) ((((float) org_w) * ((float) new_h)) / ((float) org_h)));
        }
        Logger.v(TAG, "  - scaling image to " + new_w + " r " + new_h);
        Bitmap result = Bitmap.createScaledBitmap(bitmap, new_w, new_h, true);
        bitmap.recycle();
        return result;
    }

    private static Bitmap getSizedAbitraryBitmap(Context context, String pathName) {
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, opts);
        int i = 1;
        int width = opts.outWidth;
        int height = opts.outHeight;
        if (width > height) {
            do {
                i *= 2;
            } while (width / i > 1536);
        } else {
            do {
                i *= 2;
            } while (height / i > 1536);
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = i;
        return BitmapFactory.decodeFile(pathName, opts);
    }

    private boolean loadCachedPath(GL10 gl10, String pathName, boolean useMipMap) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.context.openFileInput(pathName), null, null);
            if (!bitmapSanityCheck(bitmap)) {
                bitmap = forceToSanity(bitmap);
            }
            if (useMipMap && (gl10 instanceof GL11)) {
                Logger.v(TAG, " - Using mipmap generation");
                gl10.glTexParameterf(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, 1.0f);
                GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
            } else {
                GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
            }
            bitmap.recycle();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean loadDrawable(GL10 gl10, int resId, boolean useMipMap) {
        InputStream resInput = this.context.getResources().openRawResource(resId);
        Options opts = new Options();
        opts.inDither = false;
        Bitmap bitmap = BitmapFactory.decodeStream(resInput, null, opts);
        try {
            resInput.close();
        } catch (IOException e) {
            Logger.v(TAG, " - TextureManager failed at BitmapFactory.decodeStream!");
        }
        if (bitmap == null) {
            Logger.v(TAG, " - bitmap was null!");
            return false;
        } else if (bitmapSanityCheck(bitmap)) {
            Logger.v(TAG, " - loadBitmap");
            if (useMipMap) {
                Logger.v(TAG, " - Using mipmap generation");
                gl10.glTexParameterf(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, 1.0f);
                GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
            } else {
                GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
            }
            bitmap.recycle();
            return true;
        } else {
            Logger.v(TAG, " - bitmap failed sanity check!");
            bitmap.recycle();
            return false;
        }
    }

    private boolean loadRawPath(GL10 gl10, String pathName, boolean useMipMap) {
        Bitmap bitmap = getSizedAbitraryBitmap(this.context, pathName);
        if (bitmap != null) {
            if (!bitmapSanityCheck(bitmap)) {
                bitmap = forceToSanity(bitmap);
            }
            if (useMipMap && (gl10 instanceof GL11)) {
                Logger.v(TAG, " - Using mipmap generation");
                gl10.glTexParameterf(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, 1.0f);
                GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
            } else {
                GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
            }
            bitmap.recycle();
            return true;
        }
        Logger.v(TAG, " - bitmap was null!");
        return false;
    }

    private boolean loadTGA(GL10 gl, int resId, boolean useMipMap) {
        try {
            InputStream is = this.context.getResources().openRawResource(resId);
            TGALoader.TGA tga = new TGALoader().loadTGA(is);
            if (tga.bpp == 8) {
                Logger.v(TAG, " - tga read as 8-bit grey");
                gl.glTexImage2D(GL_TEXTURE_2D, 0, 6409, tga.width, tga.height, 0, 6409, 5121, tga.imageData);
            } else if (tga.bpp == 24) {
                Logger.v(TAG, " - tga read as 24-bit");
                gl.glTexImage2D(GL_TEXTURE_2D, 0, 6407, tga.width, tga.height, 0, 6407, 5121, tga.imageData);
            } else if (tga.bpp == 32) {
                Logger.v(TAG, " - tga read as 32-bit");
                gl.glTexImage2D(GL_TEXTURE_2D, 0, 6408, tga.width, tga.height, 0, 6408, 5121, tga.imageData);
            }
            is.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int bindTextureID(GL10 gl10, String name) {
        int tId = getTextureID(gl10, name);
        gl10.glBindTexture(GL_TEXTURE_2D, tId);
        return tId;
    }

    private boolean fileExistsOrIsLoaded(String filename) {
        if (((Integer) this.texIDs.get(filename)).intValue() != 0) {
            return true;
        }
        Resources res = this.context.getResources();
        if (res.getIdentifier(filename, "drawable", context.getPackageName()) != 0) {
            return true;
        }
        if (res.getIdentifier(filename, "raw", context.getPackageName()) != 0) {
            return true;
        }
        if (new File(filename).exists()) {
            return true;
        }
        try {
            this.context.openFileInput(filename).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private float getImageRatio(String filename) {
        try {
            DataInputStream localDataInputStream = new DataInputStream(this.context.openFileInput(filename + SIZE_FILE_SUFFIX));
            int w = localDataInputStream.readInt();
            int h = localDataInputStream.readInt();
            float r = localDataInputStream.readFloat();
            localDataInputStream.close();
            Logger.v(TAG, "Reading cached size data: " + w + " r " + h + " = " + r);
            return r;
        } catch (Exception e) {
            Options opt = new Options();
            opt.inJustDecodeBounds = true;
            try {
                FileInputStream fis = this.context.openFileInput(filename);
                BitmapFactory.decodeStream(fis, null, opt);
                fis.close();
            } catch (Exception e2) {
                BitmapFactory.decodeFile(filename, opt);
            }
            return ((float) opt.outWidth) / ((float) opt.outHeight);
        }
    }

    private int getTextureID(GL10 gl10, String path) {
        if (path.equals(this.lastPath)) {
            return this.lastId;
        }
        try {
            this.lastId = ((Integer) this.texIDs.get(path)).intValue();
        } catch (Exception e) {
            Logger.w(TAG, "ERROR: couldn't find texture: " + path + ", attempting to load...");
            this.lastId = loadTextureFromPath(gl10, path);
        }
        this.lastPath = path;
        return this.lastId;
    }

    private boolean isLoaded(String path) {
        return this.texIDs.containsKey(path);
    }

    private int loadTextureFromPath(GL10 gl10, String path) {
        return loadTextureFromPath(gl10, path, true, false);
    }

    private int loadTextureFromPath(GL10 gl10, String path, boolean useMipMap) {
        return loadTextureFromPath(gl10, path, useMipMap, false);
    }

    private int loadTextureFromPath(GL10 gl, String name, boolean useMipMap, boolean useClamp) {
        if (isLoaded(name)) {
            Logger.v(TAG, "TextureManager already loaded " + name);
            return getTextureID(gl, name);
        }
        Logger.v(TAG, "TextureManager reading " + name);
        if (this.pref_useMipMaps && (gl instanceof GL11) && useMipMap) {
            gl.glTexParameterf(GL_TEXTURE_2D, 10241, 9985.0f);
            gl.glTexParameterf(GL_TEXTURE_2D, 10240, GL_LINEAR);
        } else {
            gl.glTexParameterf(GL_TEXTURE_2D, 10241, GL_LINEAR);
            gl.glTexParameterf(GL_TEXTURE_2D, 10240, GL_LINEAR);
        }
        if (useClamp) {
            gl.glTexParameterf(GL_TEXTURE_2D, 10242, 33071.0f);
            gl.glTexParameterf(GL_TEXTURE_2D, 10243, 33071.0f);
        } else {
            gl.glTexParameterf(GL_TEXTURE_2D, 10242, GL_REPEAT);
            gl.glTexParameterf(GL_TEXTURE_2D, 10243, GL_REPEAT);
        }
        boolean loadSuccess = false;
        Resources resources = this.context.getResources();
        IntBuffer textureBuffer = IntBuffer.allocate(1);
        gl.glEnable(GL_TEXTURE_2D);
        gl.glGenTextures(1, textureBuffer);
        gl.glBindTexture(GL_TEXTURE_2D, textureBuffer.get(0));
        int drawable_id = resources.getIdentifier(name, "drawable", context.getPackageName());
        int raw_id = resources.getIdentifier(name, "raw", context.getPackageName());
        if (drawable_id != 0) {
            if (loadDrawable(gl, drawable_id, useMipMap)) {
                loadSuccess = true;
            }
        } else if (raw_id == 0) {
            loadSuccess = loadCachedPath(gl, name, useMipMap) ? true : loadRawPath(gl, name, useMipMap);
        } else if (loadTGA(gl, raw_id, useMipMap)) {
            loadSuccess = true;
        }
        if (loadSuccess) {
            this.texIDs.put(name, Integer.valueOf(textureBuffer.get(0)));
            Logger.d(TAG, "load " + name + " successed, glId=" + textureBuffer.get(0));
        } else {
            this.texIDs.put(name, Integer.valueOf(0));
            Logger.d(TAG, "load " + name + " failed");
        }
        return textureBuffer.get(0);
    }

    private void unload(GL10 gl10) {
        Iterator iterator = this.texIDs.keySet().iterator();
        while (iterator.hasNext()) {
            String filename = (String) iterator.next();
            iterator.remove();
            unload(gl10, filename);
        }
    }

    private boolean unload(GL10 gl10, String path) {
        if (this.texIDs.containsKey(path)) {
            Logger.v(TAG, "TextureManager unloading " + path);
            // TODO managed by Textures
            // gl10.glDeleteTextures(1, new int[]{((Integer) this.texIDs.get(path)).intValue()}, 0);
            this.texIDs.remove(path);
            return true;
        }
        Logger.v(TAG, "TextureManager doesn't need to unload " + path);
        return false;
    }

}
