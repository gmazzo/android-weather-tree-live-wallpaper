package gs.weather;

import android.opengl.GLU;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import gs.weather.engine.TextureManager;

public class Cube {
    private static float cubeRotX;
    private static float cubeRotY;
    private static float cubeRotZ;
    private static FloatBuffer[] cubeTextureBfr;
    private static final float[][] cubeTextureCoords = new float[][]{new float[]{1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f}, new float[]{0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f}, new float[]{0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f}};
    private static FloatBuffer[] cubeVertexBfr;
    private static final float[][] cubeVertexCoords = new float[][]{new float[]{1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f}, new float[]{1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f}, new float[]{1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f}, new float[]{-1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f}, new float[]{1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f}};
    private static Cube mInstance;
    private int textureId;

    private Cube(GL10 gl, TextureManager tm) {
        cubeVertexBfr = new FloatBuffer[6];
        cubeTextureBfr = new FloatBuffer[6];
        for (int i = 0; i < 6; i++) {
            cubeVertexBfr[i] = makeDirectBuffer(cubeVertexCoords[i]);
            cubeTextureBfr[i] = makeDirectBuffer(cubeTextureCoords[i]);
        }
        this.textureId = tm.bindTextureID(gl, "moon");
    }

    public static Cube getCube(GL10 gl, TextureManager tm) {
        if (mInstance == null) {
            mInstance = new Cube(gl, tm);
        }
        return mInstance;
    }

    public void drawFrame(GL10 gl, float screenRatio, float timeDelta) {
        gl.glShadeModel(7425);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(2929);
        gl.glDepthFunc(515);
        gl.glEnable(2884);
        gl.glCullFace(1029);
        gl.glHint(3152, 4354);
        gl.glClear(16640);
        gl.glMatrixMode(5889);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 125.0f, screenRatio, 8.0f, 400.0f);
        GLU.gluLookAt(gl, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
        gl.glMatrixMode(5888);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, 10.0f);
        gl.glRotatef(cubeRotX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(cubeRotY, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(cubeRotZ, 0.0f, 0.0f, 1.0f);
        gl.glBindTexture(3553, this.textureId);
        gl.glEnableClientState(32884);
        gl.glEnableClientState(32888);
        for (int i = 0; i < 6; i++) {
            gl.glVertexPointer(3, 5126, 0, cubeVertexBfr[i]);
            gl.glTexCoordPointer(2, 5126, 0, cubeTextureBfr[i]);
            gl.glDrawArrays(6, 0, 4);
        }
        gl.glDisableClientState(32884);
        gl.glDisableClientState(32888);
        gl.glPopMatrix();
        gl.glMatrixMode(5889);
        gl.glPopMatrix();
        cubeRotX += 0.5f;
        cubeRotY += 0.5f;
        cubeRotZ += 0.5f;
    }

    public static FloatBuffer makeDirectBuffer(float[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }
}
