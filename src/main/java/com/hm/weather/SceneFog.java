package com.hm.weather;

import android.content.Context;
import android.content.SharedPreferences;
import com.hm.weather.engine.GlobalTime;
import com.hm.weather.engine.Mesh;
import com.hm.weather.engine.MeshManager;
import com.hm.weather.engine.TextureManager;
import com.hm.weather.engine.ThingManager;
import com.hm.weather.engine.Vector4;
import com.hm.weather.sky_manager.TimeOfDay;
import javax.microedition.khronos.opengles.GL10;

public class SceneFog extends SceneBase {
    private static final String TAG = "Fog";
    static float[] fogColor = new float[]{0.8f, 0.8f, 0.8f, 1.0f};
    static float pref_fog_density = 0.2f;
    Vector4 fogColorFinal;
    Vector4[] fog_todColors;

    public SceneFog(Context ctx) {
        this.mThingManager = new ThingManager();
        this.mTextureManager = new TextureManager(ctx);
        this.mMeshManager = new MeshManager(ctx);
        this.mContext = ctx;
        this.pref_background = "bg1";
        todColorFinal = new Vector4();
        this.pref_todColors = new Vector4[4];
        this.pref_todColors[0] = new Vector4();
        this.pref_todColors[1] = new Vector4();
        this.pref_todColors[2] = new Vector4();
        this.pref_todColors[3] = new Vector4();
        this.fogColorFinal = new Vector4();
        this.fog_todColors = new Vector4[4];
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
        this.mTextureManager.updatePrefs();
        this.reloadAssets = true;
    }

    public void precacheAssets(GL10 gl10) {
        this.mTextureManager.loadTextureFromPath(gl10, this.pref_background);
        this.mTextureManager.loadTextureFromPath(gl10, "trees_overlay");
        this.mTextureManager.loadTextureFromPath(gl10, "sun", false);
        this.mTextureManager.loadTextureFromPath(gl10, "sun_blend", false);
        this.mMeshManager.createMeshFromFile(gl10, "plane_16x16");
        this.mMeshManager.createMeshFromFile(gl10, "grass_overlay", true);
        this.mMeshManager.createMeshFromFile(gl10, "trees_overlay", true);
        this.mMeshManager.createMeshFromFile(gl10, "trees_overlay_terrain");
    }

    public void backgroundFromPrefs(SharedPreferences prefs) {
        String bg = "bg1";
        if (!bg.equals(this.pref_background)) {
            this.pref_background = bg;
            this.reloadAssets = true;
        }
    }

    private void todFromPrefs(SharedPreferences prefs) {
        pref_useTimeOfDay = prefs.getBoolean(BaseWallpaperSettings.PREF_USE_TOD, false);
        this.pref_todColors[0].set(prefs.getString(BaseWallpaperSettings.PREF_LIGHT_COLOR1, "0.5 0.5 0.75 1"), 0.0f, 1.0f);
        this.pref_todColors[1].set(prefs.getString(BaseWallpaperSettings.PREF_LIGHT_COLOR2, "1 0.73 0.58 1"), 0.0f, 1.0f);
        this.pref_todColors[2].set(prefs.getString(BaseWallpaperSettings.PREF_LIGHT_COLOR3, "1 1 1 1"), 0.0f, 1.0f);
        this.pref_todColors[3].set(prefs.getString(BaseWallpaperSettings.PREF_LIGHT_COLOR4, "1 0.85 0.75 1"), 0.0f, 1.0f);
        this.fog_todColors[0] = new Vector4(0.2f, 0.2f, 0.2f, 1.0f);
        this.fog_todColors[1] = new Vector4(0.5f, 0.5f, 0.5f, 1.0f);
        this.fog_todColors[2] = new Vector4(0.8f, 0.8f, 0.8f, 1.0f);
        this.fog_todColors[3] = new Vector4(0.5f, 0.5f, 0.5f, 1.0f);
    }

    public void updateTimeOfDay(TimeOfDay tod) {
        if (pref_useTimeOfDay) {
            int iMain = tod.getMainIndex();
            int iBlend = tod.getBlendIndex();
            float blendAmount = tod.getBlendAmount();
            Vector4.mix(todColorFinal, this.pref_todColors[iMain], this.pref_todColors[iBlend], blendAmount);
            Vector4.mix(this.fogColorFinal, this.fog_todColors[iMain], this.fog_todColors[iBlend], blendAmount);
        } else {
            todColorFinal.set(1.0f, 1.0f, 1.0f, 1.0f);
            this.fogColorFinal.set(0.8f, 0.8f, 0.8f, 1.0f);
        }
        this.fogColorFinal.setToArray(fogColor);
    }

    public void draw(GL10 gl, GlobalTime time) {
        checkAssetReload(gl);
        this.mThingManager.update(time.sTimeDelta);
        gl.glDisable(16384);
        gl.glDisable(16385);
        gl.glDisable(2896);
        gl.glMatrixMode(5888);
        gl.glLoadIdentity();
        gl.glBlendFunc(1, 771);
        gl.glEnable(2912);
        gl.glFogf(2917, 9729.0f);
        gl.glFogfv(2918, fogColor, 0);
        gl.glFogf(2914, pref_fog_density);
        gl.glFogf(2915, -10.0f);
        gl.glFogf(2916, 190.0f);
        gl.glFogf(3156, 4352.0f);
        renderBackground(gl, time.sTimeElapsed);
        gl.glTranslatef(0.0f, 0.0f, 40.0f);
        this.mThingManager.render(gl, this.mTextureManager, this.mMeshManager);
        drawTree(gl, time.sTimeDelta);
        gl.glDisable(2912);
    }

    private void renderBackground(GL10 gl, float timeDelta) {
        Mesh mesh = this.mMeshManager.getMeshByName(gl, "plane_16x16");
        this.mTextureManager.bindTextureID(gl, this.pref_background);
        gl.glColor4f(todColorFinal.x, todColorFinal.y, todColorFinal.z, 1.0f);
        gl.glMatrixMode(5888);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 250.0f, 35.0f);
        gl.glScalef(this.BG_PADDING * 2.0f, this.BG_PADDING, this.BG_PADDING);
        gl.glMatrixMode(5890);
        gl.glPushMatrix();
        gl.glTranslatef(((pref_windSpeed * timeDelta) * -0.005f) % 1.0f, 0.0f, 0.0f);
        mesh.render(gl);
        gl.glPopMatrix();
        gl.glMatrixMode(5888);
        gl.glPopMatrix();
    }
}
