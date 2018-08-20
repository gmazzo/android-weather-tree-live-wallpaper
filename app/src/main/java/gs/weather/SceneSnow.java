package gs.weather;

import android.content.Context;
import android.content.SharedPreferences;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import gs.weather.engine.GlobalRand;
import gs.weather.engine.GlobalTime;
import gs.weather.engine.Mesh;
import gs.weather.engine.MeshManager;
import gs.weather.engine.TextureManager;
import gs.weather.engine.ThingManager;
import gs.weather.engine.Vector3;
import gs.weather.engine.Vector4;
import gs.weather.sky_manager.TimeOfDay;

public class SceneSnow extends SceneBase {
    static final float CLOUD_START_DISTANCE = 175.0f;
    static final float CLOUD_X_RANGE = 45.0f;
    static final float CLOUD_Z_RANGE = 20.0f;
    private static final String TAG = "Snow";
    static final float WISPY_X_RANGE = 60.0f;
    static final float WISPY_Z_RANGE = 30.0f;
    public static float pref_snowGravity;
    public static String pref_snowImage;
    public static float pref_snowNoise;
    ParticleSnow particleSnow;
    int pref_snowDensity;
    Vector3 snowPos1;
    Vector3 snowPos2;
    Vector3 snowPos3;

    public SceneSnow(Context ctx) {
        this.mThingManager = new ThingManager();
        this.mTextureManager = new TextureManager(ctx);
        this.mMeshManager = new MeshManager(ctx);
        this.mContext = ctx;
        this.pref_background = "bg2";
        todColorFinal = new Vector4();
        this.pref_todColors = new Vector4[4];
        this.pref_todColors[0] = new Vector4();
        this.pref_todColors[1] = new Vector4();
        this.pref_todColors[2] = new Vector4();
        this.pref_todColors[3] = new Vector4();
        this.reloadAssets = false;
        this.pref_numClouds = 20;
        this.pref_numWisps = 6;
        this.snowPos1 = new Vector3(0.0f, CLOUD_Z_RANGE, -20.0f);
        this.snowPos2 = new Vector3(8.0f, 15.0f, -20.0f);
        this.snowPos3 = new Vector3(-8.0f, 10.0f, -20.0f);
    }

    public void load(GL10 gl) {
        spawnClouds(false);
    }

    public void updateSharedPrefs(SharedPreferences prefs, String key) {
        if (key == null || !key.equals("pref_usemipmaps")) {
            backgroundFromPrefs(prefs);
            windSpeedFromPrefs(prefs);
            numCloudsFromPrefs(prefs);
            todFromPrefs(prefs);
            if (key != null && (key.contains("numclouds") || key.contains("windspeed") || key.contains("numwisps"))) {
                spawnClouds(true);
            }
            snowDensityFromPrefs(prefs);
            snowGravityFromPrefs(prefs);
            snowNoiseFromPrefs(prefs);
            snowTypeFromPrefs(prefs);
            return;
        }
        this.mTextureManager.updatePrefs();
        this.reloadAssets = true;
    }

    public void precacheAssets(GL10 gl10) {
        this.mTextureManager.loadTextureFromPath(gl10, this.pref_background);
        this.mTextureManager.loadTextureFromPath(gl10, "trees_overlay");
        this.mTextureManager.loadTextureFromPath(gl10, "cloud1");
        this.mTextureManager.loadTextureFromPath(gl10, "cloud2");
        this.mTextureManager.loadTextureFromPath(gl10, "cloud3");
        this.mTextureManager.loadTextureFromPath(gl10, "cloud4");
        this.mTextureManager.loadTextureFromPath(gl10, "cloud5");
        this.mTextureManager.loadTextureFromPath(gl10, "wispy1");
        this.mTextureManager.loadTextureFromPath(gl10, "wispy2");
        this.mTextureManager.loadTextureFromPath(gl10, "wispy3");
        this.mTextureManager.loadTextureFromPath(gl10, "p_snow1");
        this.mTextureManager.loadTextureFromPath(gl10, "p_snow2");
        this.mMeshManager.createMeshFromFile(gl10, "plane_16x16");
        this.mMeshManager.createMeshFromFile(gl10, "cloud1m");
        this.mMeshManager.createMeshFromFile(gl10, "cloud2m");
        this.mMeshManager.createMeshFromFile(gl10, "cloud3m");
        this.mMeshManager.createMeshFromFile(gl10, "cloud4m");
        this.mMeshManager.createMeshFromFile(gl10, "cloud5m");
        this.mMeshManager.createMeshFromFile(gl10, "grass_overlay", true);
        this.mMeshManager.createMeshFromFile(gl10, "trees_overlay", true);
        this.mMeshManager.createMeshFromFile(gl10, "trees_overlay_terrain");
        this.mMeshManager.createMeshFromFile(gl10, "flakes");
    }

    private void spawnClouds(boolean force) {
        spawnClouds(this.pref_numClouds, this.pref_numWisps, force);
    }

    public void backgroundFromPrefs(SharedPreferences prefs) {
        String bg = "bg2";
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
    }

    private void snowDensityFromPrefs(SharedPreferences prefs) {
        this.pref_snowDensity = Integer.parseInt(prefs.getString("pref_snowdensity", "2"));
    }

    private void snowGravityFromPrefs(SharedPreferences prefs) {
        pref_snowGravity = Float.parseFloat(prefs.getString("pref_snowgravity", "2")) * 0.5f;
    }

    private void snowNoiseFromPrefs(SharedPreferences prefs) {
        pref_snowNoise = Float.parseFloat(prefs.getString("pref_snownoise", "7")) * 0.1f;
    }

    private void snowTypeFromPrefs(SharedPreferences prefs) {
        pref_snowImage = prefs.getString("pref_snowtype", "p_snow1");
        this.reloadAssets = true;
    }

    private void spawnClouds(int num_clouds, int num_wisps, boolean force) {
        boolean cloudsExist = this.mThingManager.countByTargetname("cloud") != 0;
        if (force || !cloudsExist) {
            int i;
            this.mThingManager.clearByTargetname("cloud");
            this.mThingManager.clearByTargetname("wispy");
            float[] cloudDepthList = new float[num_clouds];
            float cloudDepthStep = 131.25f / ((float) num_clouds);
            for (i = 0; i < cloudDepthList.length; i++) {
                cloudDepthList[i] = (((float) i) * cloudDepthStep) + 43.75f;
            }
            for (i = 0; i < cloudDepthList.length; i++) {
                float f4 = cloudDepthList[i];
                int i2 = GlobalRand.intRange(0, cloudDepthList.length);
                cloudDepthList[i] = cloudDepthList[i2];
                cloudDepthList[i2] = f4;
            }
            for (i = 0; i < cloudDepthList.length; i++) {
                ThingCloud cloud = new ThingCloud();
                cloud.randomizeScale();
                if (GlobalRand.intRange(0, 2) == 0) {
                    cloud.scale.x *= -1.0f;
                }
                cloud.origin.x = (((float) i) * (90.0f / ((float) num_clouds))) - 0.099609375f;
                cloud.origin.y = cloudDepthList[i];
                cloud.origin.z = GlobalRand.floatRange(-20.0f, -10.0f);
                int which = (i % 5) + 1;
                cloud.meshName = "cloud" + which + "m";
                cloud.texName = "cloud" + which;
                cloud.targetName = "cloud";
                cloud.velocity = new Vector3(pref_windSpeed * 1.5f, 0.0f, 0.0f);
                this.mThingManager.add(cloud);
            }
            for (i = 0; i < cloudDepthList.length; i++) {
                ThingWispy wispy = new ThingWispy();
                wispy.meshName = "plane_16x16";
                wispy.texName = "wispy" + ((i % 3) + 1);
                wispy.targetName = "wispy";
                wispy.velocity = new Vector3(pref_windSpeed * 1.5f, 0.0f, 0.0f);
                wispy.scale.set(GlobalRand.floatRange(1.0f, 3.0f), 1.0f, GlobalRand.floatRange(1.0f, 1.5f));
                wispy.origin.x = (((float) i) * (120.0f / ((float) num_wisps))) - 0.0703125f;
                wispy.origin.y = GlobalRand.floatRange(87.5f, CLOUD_START_DISTANCE);
                wispy.origin.z = GlobalRand.floatRange(-40.0f, -20.0f);
                this.mThingManager.add(wispy);
            }
        }
    }

    public void updateTimeOfDay(TimeOfDay tod) {
        todSunPosition = tod.getSunPosition();
        super.updateTimeOfDay(tod);
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
        renderBackground(gl, time.sTimeElapsed);
        gl.glTranslatef(0.0f, 0.0f, 40.0f);
        this.mThingManager.render(gl, this.mTextureManager, this.mMeshManager);
        renderSnow(gl, time.sTimeDelta);
        drawTree(gl, time.sTimeDelta);
    }

    private void renderSnow(GL10 gl, float timeDelta) {
        if (this.particleSnow == null) {
            this.particleSnow = new ParticleSnow();
        }
        this.particleSnow.update(timeDelta);
        this.particleSnow.render((GL11) gl, this.mTextureManager, this.mMeshManager, this.snowPos1);
        if (this.pref_snowDensity > 1) {
            this.particleSnow.render((GL11) gl, this.mTextureManager, this.mMeshManager, this.snowPos2);
        }
        if (this.pref_snowDensity > 2) {
            this.particleSnow.render((GL11) gl, this.mTextureManager, this.mMeshManager, this.snowPos3);
        }
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
