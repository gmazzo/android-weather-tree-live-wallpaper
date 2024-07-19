package io.github.gmazzo.android.livewallpaper.weather;

import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_MODULATE;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_COORD_ARRAY;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_ENV;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_ENV_MODE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.opengl.GLU;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand;
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime;
import io.github.gmazzo.android.livewallpaper.weather.engine.Scene;
import io.github.gmazzo.android.livewallpaper.weather.engine.Utility;
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector;
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay;
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.WeatherSettingsUtil;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures;

public class IsolatedRenderer implements OnSharedPreferenceChangeListener {
    static final float BACKGROUND_DISTANCE = 300.0f;
    static final float CALENDAR_UPDATE_INTERVAL = 10.0f;
    static final float CAMERA_X_POSITION = 0.0f;
    static final float CAMERA_X_RANGE = 14.0f;
    static final float CAMERA_Y_POSITION = 0.0f;
    static final float CAMERA_Z_POSITION = 0.0f;
    static final float CAMERA_Z_RANGE = 10.0f;
    static final float POSITION_UPDATE_INTERVAL = 300.0f;
    private static final String TAG = "IsolatedRenderer";
    public static SceneMode currentSceneId;
    public static float homeOffsetPercentage = 0.5f;
    public static float horizontalFOV = 45.0f;
    boolean IS_LANDSCAPE = false;
    private TimeOfDay _tod = new TimeOfDay();
    private Calendar calendarInstance;
    private Vector cameraDir = new Vector();
    private float cameraFOV = 65.0f;
    private Vector cameraPos;
    Context context;
    private Scene currentScene;
    private Vector desiredCameraPos;
    private GlobalTime globalTime;
    boolean isPaused;
    private float lastCalendarUpdate;
    private float lastPositionUpdate;
    private GL11 gl = null;
    float pref_cameraSpeed = 1.0f;
    boolean isDemoMode = false;
    SharedPreferences prefs;
    private float screenHeight;
    private float screenRatio = 1.0f;
    private float screenWidth;

    public IsolatedRenderer(Context ctx) {
        homeOffsetPercentage = 0.5f;
        this.isPaused = false;
        this.calendarInstance = null;
        this.lastCalendarUpdate = 10.0f;
        this.lastPositionUpdate = 300.0f;
        this.globalTime = new GlobalTime();
        this.cameraPos = new Vector(0.0f, 0.0f, 0.0f);
        this.desiredCameraPos = new Vector(0.0f, 0.0f, 0.0f);
        this.context = ctx;
    }

    private void setContext(Context ctx) {
        this.context = ctx;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        onSharedPreferenceChanged(this.prefs, null);
        this.prefs.registerOnSharedPreferenceChangeListener(this);
    }

    public Context getContext() {
        return this.context;
    }

    public synchronized void onPause() {
        this.isPaused = true;
    }

    public synchronized void onResume() {
        this.lastCalendarUpdate = 10.0f;
        this.lastPositionUpdate = 300.0f;
        this.isPaused = false;
    }

    public void precacheAssets(GL10 gl10) {
        this.currentScene.precacheAssets(gl10);
    }

    public void cameraSpeedFromPrefs(SharedPreferences prefs) {
        this.pref_cameraSpeed = (Float.valueOf(prefs.getString("pref_cameraspeed", "1")).floatValue() * 0.5f) + 0.5f;
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        cameraSpeedFromPrefs(prefs);
        this.currentScene.updateSharedPrefs(prefs, key);
    }

    public synchronized void onSceneChanged(SceneMode newSceneId) {
        if (newSceneId != currentSceneId) {
            this.currentScene.unload(this.gl);
            switch (newSceneId) {
                case CLEAR:
                    this.currentScene = new SceneClear(this.context, this.gl);
                    currentSceneId = SceneMode.CLEAR;
                    break;
                case CLOUDY:
                    this.currentScene = new SceneCloudy(this.context, this.gl);
                    currentSceneId = SceneMode.CLOUDY;
                    break;
                case STORM:
                    this.currentScene = new SceneStorm(this.context, this.gl);
                    currentSceneId = SceneMode.STORM;
                    break;
                case SNOW:
                    this.currentScene = new SceneSnow(this.context, this.gl);
                    currentSceneId = SceneMode.SNOW;
                    break;
                case FOG:
                    this.currentScene = new SceneFog(this.context, this.gl);
                    currentSceneId = SceneMode.FOG;
                    break;
                case RAIN:
                    this.currentScene = new SceneRain(this.context, this.gl);
                    currentSceneId = SceneMode.RAIN;
                    break;
            }
            this.currentScene.load(this.gl);
        }
        this.currentScene.setScreenMode(this.IS_LANDSCAPE);
        onSharedPreferenceChanged(this.prefs, null);

        if (isDemoMode) {
            Toast.makeText(context, newSceneId.name(), Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, "Scene changed to " + newSceneId.name() + ", isDemoMode=" + isDemoMode);
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        gl.glViewport(0, 0, w, h);
        this.screenWidth = (float) w;
        this.screenHeight = (float) h;
        this.screenRatio = this.screenWidth / this.screenHeight;
        if (this.screenRatio > 1.0f) {
            this.IS_LANDSCAPE = true;
        } else {
            this.IS_LANDSCAPE = false;
        }
        setRenderDefaults(gl);
        gl.glMatrixMode(5889);
        gl.glLoadIdentity();
        if (gl != this.gl) {
            this.gl = (GL11) gl;
            if (this.currentScene == null) {
                if (Scene.sTextures == null) {
                    Scene.sTextures = new Textures(context.getResources(), (GL11) gl);
                }
                if (Scene.sModels == null) {
                    Scene.sModels = new Models(context.getResources(), (GL11) gl);
                }

                currentSceneId = SceneMode.CLEAR;
                currentScene = new SceneClear(context, (GL11) gl);
                setContext(context);

            } else {
                this.currentScene.unload(gl);
                this.currentScene.precacheAssets(gl);
            }
        }
        this.currentScene.setScreenMode(this.IS_LANDSCAPE);
        this.currentScene.load(gl);
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eglconfig) {
    }

    public void setRenderDefaults(GL10 gl) {
        gl.glHint(3152, 4353);
        gl.glShadeModel(7425);
        gl.glEnable(GL_TEXTURE_2D);
        gl.glEnable(3042);
        gl.glAlphaFunc(518, 0.02f);
        gl.glDepthMask(false);
        gl.glDepthFunc(515);
        gl.glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        gl.glEnableClientState(32884);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glBlendFunc(1, 771);
        gl.glCullFace(1029);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glEnable(2903);
        gl.glMatrixMode(5890);
        gl.glPopMatrix();
        gl.glPopMatrix();
        gl.glLoadIdentity();
        gl.glMatrixMode(5889);
        gl.glPopMatrix();
        gl.glPopMatrix();
        gl.glLoadIdentity();
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPopMatrix();
        gl.glPopMatrix();
        gl.glLoadIdentity();
    }

    public synchronized void drawFrame(GL10 gl) {
        if (!this.isPaused) {
            this.globalTime.updateTime();
            updateCalendar(this.globalTime.sTimeDelta);
            updateCameraPosition(gl, this.globalTime.sTimeDelta);
            gl.glClear(256);
            gl.glMatrixMode(5889);
            gl.glLoadIdentity();
            if (this.IS_LANDSCAPE) {
                GLU.gluPerspective(gl, this.cameraFOV, this.screenRatio, 1.0f, 400.0f);
            } else {
                GLU.gluPerspective(gl, this.cameraFOV, this.screenRatio, 1.0f, 400.0f);
            }
            GLU.gluLookAt(gl, this.cameraPos.getX(), this.cameraPos.getY(), this.cameraPos.getZ(), this.cameraPos.getX(), 400.0f, this.cameraPos.getZ(), 0.0f, 0.0f, 1.0f);
            this.currentScene.draw(gl, this.globalTime);
        }
    }

    public void setTouchPos(float x, float y) {
        Vector vPos = new Vector();
        Utility.adjustScreenPosForDepth(vPos, this.cameraFOV, this.screenWidth, this.screenHeight, x, y, GlobalRand.floatRange(35.0f, 68.0f) - this.cameraPos.getY());
        vPos.setX(vPos.getX() + this.cameraPos.getX());
    }

    public void updateOffset(float offset) {
        homeOffsetPercentage = offset;
    }

    private void updateCalendar(float timeDelta) {
        this.lastCalendarUpdate += timeDelta;
        if (this.lastCalendarUpdate >= 10.0f || this.calendarInstance == null) {
            this.calendarInstance = Calendar.getInstance();
            this.lastCalendarUpdate = 0.0f;
        }
        if (this.lastPositionUpdate >= 300.0f) {
            float longitude = WeatherSettingsUtil.getLongitude(this.context);
            this._tod.calculateTimeTable(WeatherSettingsUtil.getLatitude(this.context), longitude);
            this.lastPositionUpdate = 0.0f;
        }
        calculateTimeOfDay(timeDelta);
    }

    private void calculateTimeOfDay(float timeDelta) {
        int minutes = (this.calendarInstance.get(Calendar.HOUR_OF_DAY) * 60) + this.calendarInstance.get(Calendar.MINUTE);
        if (this.isDemoMode) {
            minutes = (int) ((this.globalTime.msTimeCurrent / 10) % 1440);
        }
        this._tod.update(minutes, true);
        this.currentScene.updateTimeOfDay(this._tod);
    }

    private void updateCameraPosition(GL10 gl, float timeDelta) {
        this.desiredCameraPos.set((28.0f * homeOffsetPercentage) - CAMERA_X_RANGE, 0.0f, 0.0f);
        float rate = (3.5f * timeDelta) * this.pref_cameraSpeed;
        float dx = (this.desiredCameraPos.getX() - this.cameraPos.getX()) * rate;
        float dy = (this.desiredCameraPos.getY() - this.cameraPos.getY()) * rate;
        float dz = (this.desiredCameraPos.getZ() - this.cameraPos.getZ()) * rate;
        this.cameraPos.setX(this.cameraPos.getX() + dx);
        this.cameraPos.setY(this.cameraPos.getY() + dy);
        this.cameraPos.setZ(this.cameraPos.getZ() + dz);
        this.cameraDir.setX(this.cameraPos.getX() - this.cameraPos.getX());
        this.cameraDir.setY(100.0f - this.cameraPos.getY());
        if (this.IS_LANDSCAPE) {
            this.cameraFOV = 45.0f;
        } else {
            this.cameraFOV = 70.0f;
        }
        horizontalFOV = this.cameraFOV * this.screenRatio;
    }
}
