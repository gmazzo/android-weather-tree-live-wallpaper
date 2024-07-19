package io.github.gmazzo.android.livewallpaper.weather;

import android.content.Context;
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
    protected boolean isDemoMode;
    protected BaseRenderer mBaseRenderer;
    protected SurfaceHolder mServiceSurfaceHolder;

    class BaseRenderer implements Renderer {
        private IsolatedRenderer renderer;
        private boolean wasCreated = false;

        public BaseRenderer() {
            this.renderer = new IsolatedRenderer(RenderSurfaceView.this.getContext());
        }

        public void onPause() {
            this.renderer.onPause();
        }

        public void onResume() {
            this.renderer.isDemoMode = isDemoMode;
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

    public void changeScene(SceneMode sceneId) {
        this.mBaseRenderer.renderer.onSceneChanged(sceneId);
    }

    public void scrollOffset(float offset) {
        this.mBaseRenderer.renderer.updateOffset(offset);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mBaseRenderer.renderer.setTouchPos(motionEvent.getX(), motionEvent.getY());
        return super.onTouchEvent(motionEvent);
    }

    public void updateWeatherType(@WeatherType int type) {
        Editor e = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        SceneMode ret = SceneMode.CLEAR;
        switch (type) {
            case WeatherType.SUNNY_DAY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 2);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 2);
                ret = SceneMode.CLEAR;
                break;
            case WeatherType.MOSTLY_SUNNY_DAY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 3);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 3);
                ret = SceneMode.CLEAR;
                break;
            case WeatherType.PARTLY_SUNNY_DAY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 4);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 4);
                ret = SceneMode.CLEAR;
                break;
            case WeatherType.INTERMITTENT_CLOUDS_DAY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 10);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.CLEAR;
                break;
            case WeatherType.HAZY_SUNSHINE_DAY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 5);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 10);
                ret = SceneMode.CLEAR;
                break;
            case WeatherType.MOSTLY_CLOUDY_DAY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.CLOUDY;
                break;
            case WeatherType.CLOUDY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.CLOUDY;
                break;
            case WeatherType.DREARY_OVERCAST:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 25);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.CLOUDY;
                break;
            case WeatherType.HOT:
            case WeatherType.COLD:
            case WeatherType.WINDY:
                break;
            case WeatherType.FOG:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 5);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 10);
                ret = SceneMode.FOG;
                break;
            case WeatherType.SHOWERS:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 10);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.RAIN;
                break;
            case WeatherType.MOSTLY_CLOUDY_WITH_SHOWERS_DAY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 6);
                ret = SceneMode.RAIN;
                break;
            case WeatherType.PARTLY_SUNNY_WITH_SHOWERS_DAY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 5);
                ret = SceneMode.RAIN;
                break;
            case WeatherType.THUNDER_STORMS:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 10);
                ret = SceneMode.STORM;
                break;
            case WeatherType.MOSTLY_CLOUDY_WITH_THUNDER_STORMS_DAY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 25);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 7);
                ret = SceneMode.STORM;
                break;
            case WeatherType.PARTLY_SUNNY_WITH_THUNDER_STORMS_DAY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 6);
                ret = SceneMode.STORM;
                break;
            case WeatherType.RAIN:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 10);
                ret = SceneMode.RAIN;
                break;
            case WeatherType.FLURRIES:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.SNOW;
                break;
            case WeatherType.MOSTLY_CLOUDY_WITH_FLURRIES_DAY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 25);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.SNOW;
                break;
            case WeatherType.PARTLY_SUNNY_WITH_FLURRIES_DAY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.SNOW;
                break;
            case WeatherType.SNOW:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.SNOW;
                break;
            case WeatherType.MOSTLY_CLOUDY_WITH_SNOW_DAY:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 25);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.SNOW;
                break;
            case WeatherType.ICE:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.SNOW;
                break;
            case WeatherType.SLEET:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.SNOW;
                break;
            case WeatherType.FREEZING_RAIN:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.RAIN;
                break;
            case WeatherType.RAIN_AND_SNOW:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.RAIN;
                break;
            case WeatherType.CLEAR_NIGHT:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 2);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 2);
                ret = SceneMode.CLEAR;
                break;
            case WeatherType.MOSTLY_CLEAR_NIGHT:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 4);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 4);
                ret = SceneMode.CLEAR;
                break;
            case WeatherType.PARTLY_CLOUDY_NIGHT:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 8);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.CLOUDY;
                break;
            case WeatherType.INTERMITTENT_CLOUDS_NIGHT:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.CLOUDY;
                break;
            case WeatherType.HAZY_MOONLIGHT_NIGHT:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 2);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 20);
                ret = SceneMode.CLOUDY;
                break;
            case WeatherType.MOSTLY_CLOUDY_NIGHT:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.CLOUDY;
                break;
            case WeatherType.PARTLY_CLOUDY_WITH_SHOWERS_NIGHT:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 6);
                ret = SceneMode.RAIN;
                break;
            case WeatherType.MOSTLY_CLOUDY_WITH_SHOWERS_NIGHT:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 25);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 7);
                ret = SceneMode.RAIN;
                break;
            case WeatherType.PARTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 15);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 6);
                ret = SceneMode.STORM;
                break;
            case WeatherType.MOSTLY_CLOUDY_WITH_THUNDER_STORMS_NIGHT:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 25);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                e.putInt(WallpaperSettings.PREF_RAIN_DENSITY, 7);
                ret = SceneMode.STORM;
                break;
            case WeatherType.MOSTLY_CLOUDY_WITH_FLURRIES_NIGHT:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.SNOW;
                break;
            case WeatherType.MOSTLY_CLOUDY_WITH_SNOW_NIGHT:
                e.putInt(WallpaperSettings.PREF_NUM_CLOUDS, 20);
                e.putInt(WallpaperSettings.PREF_NUM_WISPS, 5);
                ret = SceneMode.SNOW;
                break;
            default:
                Log.w("Renderer", "drawWeather unknown type came here!! type = " + type);
                return;
        }
        e.apply();
        changeScene(ret);
    }
}
