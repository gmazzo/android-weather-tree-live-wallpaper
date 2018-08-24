package gs.weather;

import android.content.Context;
import android.content.SharedPreferences;

import javax.microedition.khronos.opengles.GL10;

import gs.weather.engine.Color;
import gs.weather.engine.GlobalTime;
import gs.weather.engine.Mesh;
import gs.weather.engine.MeshManager;
import gs.weather.engine.TextureManager;
import gs.weather.engine.ThingManager;
import gs.weather.sky_manager.TimeOfDay;

import static javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;
import static javax.microedition.khronos.opengles.GL10.GL_LINEAR;
import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;

public class SceneFog extends SceneBase {
    private static final String TAG = "Fog";
    static float[] fogColor = new float[]{0.8f, 0.8f, 0.8f, 1.0f};
    static float pref_fog_density = 0.2f;
    Color fogColorFinal;
    Color[] fog_todColors;

    public SceneFog(Context ctx) {
        this.mThingManager = new ThingManager();
        this.mTextureManager = new TextureManager(ctx);
        this.mMeshManager = new MeshManager(ctx);
        this.mContext = ctx;
        this.pref_background = "bg1";
        todColorFinal = new Color();
        this.pref_todColors = new Color[4];
        this.pref_todColors[0] = new Color();
        this.pref_todColors[1] = new Color();
        this.pref_todColors[2] = new Color();
        this.pref_todColors[3] = new Color();
        this.fogColorFinal = new Color();
        this.fog_todColors = new Color[4];
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
        super.precacheAssets(gl10);

        textures.loadBitmap("bg1", R.drawable.bg1);
        textures.loadBitmap("trees_overlay", R.drawable.trees_overlay);
        textures.loadTGA("sun", R.raw.sun);
        textures.loadTGA("sun_blend", R.raw.sun_blend);
        models.loadBMDL("plane_16x16", R.raw.plane_16x16);
        models.loadBMDL("grass_overlay", R.raw.grass_overlay);
        models.loadBMDL("trees_overlay", R.raw.trees_overlay);
        models.loadBMDL("trees_overlay_terrain", R.raw.trees_overlay_terrain);
    }

    public void backgroundFromPrefs(SharedPreferences prefs) {
        String bg = "bg1";
        if (!bg.equals(this.pref_background)) {
            this.pref_background = bg;
            this.reloadAssets = true;
        }
    }

    private void todFromPrefs(SharedPreferences prefs) {
        pref_useTimeOfDay = prefs.getBoolean(WallpaperSettings.PREF_USE_TOD, false);
        this.pref_todColors[0].set(prefs.getString(WallpaperSettings.PREF_LIGHT_COLOR1, "0.5 0.5 0.75 1"), 0.0f, 1.0f);
        this.pref_todColors[1].set(prefs.getString(WallpaperSettings.PREF_LIGHT_COLOR2, "1 0.73 0.58 1"), 0.0f, 1.0f);
        this.pref_todColors[2].set(prefs.getString(WallpaperSettings.PREF_LIGHT_COLOR3, "1 1 1 1"), 0.0f, 1.0f);
        this.pref_todColors[3].set(prefs.getString(WallpaperSettings.PREF_LIGHT_COLOR4, "1 0.85 0.75 1"), 0.0f, 1.0f);
        this.fog_todColors[0] = new Color(0.2f, 0.2f, 0.2f, 1.0f);
        this.fog_todColors[1] = new Color(0.5f, 0.5f, 0.5f, 1.0f);
        this.fog_todColors[2] = new Color(0.8f, 0.8f, 0.8f, 1.0f);
        this.fog_todColors[3] = new Color(0.5f, 0.5f, 0.5f, 1.0f);
    }

    public void updateTimeOfDay(TimeOfDay tod) {
        if (pref_useTimeOfDay) {
            int iMain = tod.getMainIndex();
            int iBlend = tod.getBlendIndex();
            float blendAmount = tod.getBlendAmount();
            this.todColorFinal.blend(this.pref_todColors[iMain], this.pref_todColors[iBlend], blendAmount);
            this.fogColorFinal.blend(this.fog_todColors[iMain], this.fog_todColors[iBlend], blendAmount);
        } else {
            todColorFinal.set(1.0f, 1.0f, 1.0f, 1.0f);
            this.fogColorFinal.set(0.8f, 0.8f, 0.8f, 1.0f);
        }
        this.fogColorFinal.setToArray(fogColor);
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
        Mesh mesh = this.mMeshManager.getMeshByName(gl, "plane_16x16");
        this.mTextureManager.bindTextureID(gl, this.pref_background);
        gl.glColor4f(todColorFinal.getR(), todColorFinal.getG(), todColorFinal.getB(), 1.0f);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 250.0f, 35.0f);
        gl.glScalef(this.BG_PADDING * 2.0f, this.BG_PADDING, this.BG_PADDING);
        gl.glMatrixMode(5890);
        gl.glPushMatrix();
        gl.glTranslatef(((pref_windSpeed * timeDelta) * -0.005f) % 1.0f, 0.0f, 0.0f);
        mesh.render(gl);
        gl.glPopMatrix();
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPopMatrix();
    }
}
