package io.github.gmazzo.android.livewallpaper.weather.engine.scenes;

import static javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;
import static javax.microedition.khronos.opengles.GL10.GL_LINEAR;
import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;

import android.content.Context;
import android.content.SharedPreferences;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import io.github.gmazzo.android.livewallpaper.weather.R;
import io.github.gmazzo.android.livewallpaper.weather.WallpaperSettings;
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor;
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime;
import io.github.gmazzo.android.livewallpaper.weather.engine.ThingManager;
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Model;

public class SceneFog extends SceneBase {
    private static final String TAG = "Fog";
    static float[] fogColor = new float[]{0.8f, 0.8f, 0.8f, 1.0f};
    static float pref_fog_density = 0.2f;
    EngineColor fogEngineColorFinal;
    EngineColor[] fog_todEngineColors;

    public SceneFog(Context context, GL11 gl) {
        super(context, gl);
        this.mThingManager = new ThingManager();
        todEngineColorFinal = new EngineColor();
        this.pref_todEngineColors = new EngineColor[4];
        this.pref_todEngineColors[0] = new EngineColor();
        this.pref_todEngineColors[1] = new EngineColor();
        this.pref_todEngineColors[2] = new EngineColor();
        this.pref_todEngineColors[3] = new EngineColor();
        this.fogEngineColorFinal = new EngineColor();
        this.fog_todEngineColors = new EngineColor[4];
        this.reloadAssets = false;
    }

    public void load(GL10 gl) {
    }

    public void updateSharedPrefs(SharedPreferences prefs, String key) {
        if (key == null || !key.equals("pref_usemipmaps")) {
            backgroundFromPrefs(prefs);
            windSpeedFromPrefs(prefs);
            todFromPrefs(prefs);
            pref_fog_density = prefs.getFloat("pref_fog_desity", 0.2f);
            return;
        }
        this.reloadAssets = true;
    }

    public void precacheAssets(GL10 gl10) {
        textures.get(R.drawable.bg1);
        textures.get(R.drawable.trees_overlay);
        textures.get(R.raw.sun);
        textures.get(R.raw.sun_blend);
        models.get(R.raw.plane_16x16);
        models.get(R.raw.grass_overlay);
        models.get(R.raw.trees_overlay);
        models.get(R.raw.trees_overlay_terrain);
    }

    public void backgroundFromPrefs(SharedPreferences prefs) {
    }

    private void todFromPrefs(SharedPreferences prefs) {
        pref_useTimeOfDay = prefs.getBoolean(WallpaperSettings.PREF_USE_TOD, false);
        this.pref_todEngineColors[0].set(prefs.getString(WallpaperSettings.PREF_LIGHT_COLOR1, "0.5 0.5 0.75 1"), 0.0f, 1.0f);
        this.pref_todEngineColors[1].set(prefs.getString(WallpaperSettings.PREF_LIGHT_COLOR2, "1 0.73 0.58 1"), 0.0f, 1.0f);
        this.pref_todEngineColors[2].set(prefs.getString(WallpaperSettings.PREF_LIGHT_COLOR3, "1 1 1 1"), 0.0f, 1.0f);
        this.pref_todEngineColors[3].set(prefs.getString(WallpaperSettings.PREF_LIGHT_COLOR4, "1 0.85 0.75 1"), 0.0f, 1.0f);
        this.fog_todEngineColors[0] = new EngineColor(0.2f, 0.2f, 0.2f, 1.0f);
        this.fog_todEngineColors[1] = new EngineColor(0.5f, 0.5f, 0.5f, 1.0f);
        this.fog_todEngineColors[2] = new EngineColor(0.8f, 0.8f, 0.8f, 1.0f);
        this.fog_todEngineColors[3] = new EngineColor(0.5f, 0.5f, 0.5f, 1.0f);
    }

    public void updateTimeOfDay(TimeOfDay tod) {
        if (pref_useTimeOfDay) {
            int iMain = tod.getMainIndex();
            int iBlend = tod.getBlendIndex();
            float blendAmount = tod.getBlendAmount();
            this.todEngineColorFinal.blend(this.pref_todEngineColors[iMain], this.pref_todEngineColors[iBlend], blendAmount);
            this.fogEngineColorFinal.blend(this.fog_todEngineColors[iMain], this.fog_todEngineColors[iBlend], blendAmount);
        } else {
            todEngineColorFinal.set(1.0f, 1.0f, 1.0f, 1.0f);
            this.fogEngineColorFinal.set(0.8f, 0.8f, 0.8f, 1.0f);
        }
        this.fogEngineColorFinal.setToArray(fogColor);
    }

    public void draw(GL10 gl, GlobalTime time) {
        checkAssetReload(gl);
        this.mThingManager.update(time.sTimeDelta);
        gl.glDisable(GL_COLOR_BUFFER_BIT);
        gl.glDisable(16385);
        gl.glDisable(GL_LIGHTING);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glBlendFunc(1, 771);
        gl.glEnable(2912);
        gl.glFogf(2917, GL_LINEAR);
        gl.glFogfv(2918, fogColor, 0);
        gl.glFogf(2914, pref_fog_density);
        gl.glFogf(2915, -10.0f);
        gl.glFogf(2916, 190.0f);
        gl.glFogf(3156, 4352.0f);
        renderBackground(gl, time.sTimeElapsed);
        gl.glTranslatef(0.0f, 0.0f, 40.0f);
        this.mThingManager.render(gl, textures, models);
        drawTree(gl, time.sTimeDelta);
        gl.glDisable(2912);
    }

    private void renderBackground(GL10 gl, float timeDelta) {
        gl.glBindTexture(GL_TEXTURE_2D, textures.get(R.drawable.bg1).getGlId());
        gl.glColor4f(todEngineColorFinal.getR(), todEngineColorFinal.getG(), todEngineColorFinal.getB(), 1.0f);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 250.0f, 35.0f);
        gl.glScalef(this.BG_PADDING * 2.0f, this.BG_PADDING, this.BG_PADDING);
        gl.glMatrixMode(5890);
        gl.glPushMatrix();
        gl.glTranslatef(((pref_windSpeed * timeDelta) * -0.005f) % 1.0f, 0.0f, 0.0f);
        Model model = models.get(R.raw.plane_16x16);
        model.render();
        gl.glPopMatrix();
        gl.glMatrixMode(GL_MODELVIEW);

        gl.glPopMatrix();
    }
}
