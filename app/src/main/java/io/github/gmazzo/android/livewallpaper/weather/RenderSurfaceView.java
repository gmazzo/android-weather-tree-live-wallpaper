package io.github.gmazzo.android.livewallpaper.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.opengl.GLSurfaceView;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RenderSurfaceView extends GLSurfaceView {
    protected boolean isPaused;
    protected BaseRenderer mBaseRenderer;
    protected SurfaceHolder mServiceSurfaceHolder;

    class BaseRenderer implements Renderer {
        private SharedPreferences prefs;
        private IsolatedRenderer renderer;
        private boolean wasCreated = false;

        public BaseRenderer() {
            this.renderer = new IsolatedRenderer(RenderSurfaceView.this.getContext());
        }

        public void onPause() {
            this.renderer.onPause();
        }

        public void onResume() {
            this.renderer.onResume();
        }

        public void onDrawFrame(GL10 gl) {
            if (this.wasCreated) {
                this.renderer.drawFrame(gl);
            }
        }

        public void onSurfaceChanged(GL10 gl, int w, int h) {
            this.renderer.onSurfaceChanged(gl, w, h);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig eglconfig) {
            this.renderer.onSurfaceCreated(gl, eglconfig);
            this.wasCreated = true;
        }
    }

    public RenderSurfaceView(Context context) {
        this(context, null);
    }

    public RenderSurfaceView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        this.isPaused = false;
        this.mBaseRenderer = new BaseRenderer();
        setRenderer(this.mBaseRenderer);
    }

    public SurfaceHolder getHolder() {
        if (this.mServiceSurfaceHolder != null) {
            return this.mServiceSurfaceHolder;
        }
        return super.getHolder();
    }

    public void setServiceSurfaceHolder(SurfaceHolder holder) {
        this.mServiceSurfaceHolder = holder;
    }

    public SurfaceHolder getSurfaceHolder() {
        return this.mServiceSurfaceHolder;
    }

    public void onPause() {
        this.mBaseRenderer.onPause();
        setRenderMode(0);
    }

    public void onResume() {
        this.mBaseRenderer.onResume();
        setRenderMode(1);
    }

    public void onDestroy() {
        super.onDetachedFromWindow();
    }

    public void changeScene(int sceneId) {
        this.mBaseRenderer.renderer.onSceneChanged(sceneId);
    }

    public void scrollOffset(float offset) {
        this.mBaseRenderer.renderer.updateOffset(offset);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mBaseRenderer.renderer.setTouchPos(motionEvent.getX(), motionEvent.getY());
        return super.onTouchEvent(motionEvent);
    }

    public void updateWeatherType(int type) {
        Editor e = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        int ret = 2000;
        switch (type) {
            case 1:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 2);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 2);
                ret = 2000;
                break;
            case 2:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 3);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 3);
                ret = 2000;
                break;
            case 3:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 4);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 4);
                ret = 2000;
                break;
            case 4:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 10);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2000;
                break;
            case 5:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 5);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 10);
                ret = 2000;
                break;
            case 6:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2001;
                break;
            case 7:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2001;
                break;
            case 8:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 25);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2001;
                break;
            case 9:
            case 10:
            case 27:
            case 28:
            case 30:
            case 31:
            case 32:
                break;
            case 11:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 5);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 10);
                ret = 2004;
                break;
            case 12:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 10);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2005;
                break;
            case 13:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 6);
                ret = 2005;
                break;
            case 14:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 5);
                ret = 2005;
                break;
            case 15:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 10);
                ret = 2002;
                break;
            case 16:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 25);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 7);
                ret = 2002;
                break;
            case 17:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 6);
                ret = 2002;
                break;
            case 18:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 10);
                ret = 2005;
                break;
            case 19:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2003;
                break;
            case 20:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 25);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2003;
                break;
            case 21:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2003;
                break;
            case 22:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2003;
                break;
            case 23:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 25);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2003;
                break;
            case 24:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2003;
                break;
            case 25:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2003;
                break;
            case 26:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2005;
                break;
            case 29:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2005;
                break;
            case 33:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 2);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 2);
                ret = 2000;
                break;
            case 34:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 4);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 4);
                ret = 2000;
                break;
            case 35:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 8);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2001;
                break;
            case 36:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2001;
                break;
            case 37:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 2);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 20);
                ret = 2001;
                break;
            case 38:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2001;
                break;
            case 39:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 6);
                ret = 2005;
                break;
            case 40:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 25);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 7);
                ret = 2005;
                break;
            case 41:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 6);
                ret = 2002;
                break;
            case 42:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 25);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 7);
                ret = 2002;
                break;
            case 43:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2003;
                break;
            case 44:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = 2003;
                break;
            default:
                Log.w("Renderer", "drawWeather unknown type came here!! type = " + type);
                return;
        }
        e.commit();
        changeScene(ret);
    }
}
