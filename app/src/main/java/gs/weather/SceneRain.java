package gs.weather;

import android.content.Context;
import android.content.SharedPreferences;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import gs.weather.engine.Color;
import gs.weather.engine.GlobalRand;
import gs.weather.engine.GlobalTime;
import gs.weather.engine.Mesh;
import gs.weather.engine.MeshManager;
import gs.weather.engine.TextureManager;
import gs.weather.engine.ThingManager;
import gs.weather.engine.Vector;
import gs.weather.sky_manager.TimeOfDay;
import gs.weather.wallpaper.Models;
import gs.weather.wallpaper.Textures;

public class SceneRain extends SceneBase {
    private final String TAG;
    float[] light_diffuse;
    ParticleRain particleRain;
    Vector particleRainOrigin;
    int rainDensity;
    Color v_light_diffuse;

    public SceneRain(Context ctx) {
        this.TAG = "Rain";
        this.rainDensity = 10;
        this.mThingManager = new ThingManager();
        this.mTextureManager = new TextureManager(ctx);
        this.mMeshManager = new MeshManager(ctx);
        this.mContext = ctx;
        this.pref_background = "storm_bg";
        todColorFinal = new Color();
        this.pref_todColors = new Color[4];
        this.pref_todColors[0] = new Color();
        this.pref_todColors[1] = new Color();
        this.pref_todColors[2] = new Color();
        this.pref_todColors[3] = new Color();
        this.reloadAssets = false;
        this.pref_numClouds = 20;
        this.pref_numWisps = 6;
        this.v_light_diffuse = new Color(0.5f, 0.5f, 0.5f, 1.0f);
        this.light_diffuse = new float[]{0.1f, 0.1f, 0.1f, 1.0f};
        this.particleRain = new ParticleRain(this.rainDensity);
        this.particleRainOrigin = new Vector(0.0f, 25.0f, 10.0f);
    }

    public void load(GL10 gl) {
        spawnClouds(false);
    }

    private void spawnClouds(boolean force) {
        spawnClouds(this.pref_numClouds, force);
    }

    private void spawnClouds(int num_clouds, boolean force) {
        boolean cloudsExist = this.mThingManager.countByTargetname("dark_cloud") != 0;
        if (force || !cloudsExist) {
            int i;
            this.mThingManager.clearByTargetname("dark_cloud");
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
                ThingDarkCloud cloud = new ThingDarkCloud(false);
                cloud.randomizeScale();
                if (GlobalRand.intRange(0, 2) == 0) {
                    cloud.scale.setX(cloud.scale.getX() * -1.0f);
                }
                cloud.origin.setX((((float) i) * (90.0f / ((float) num_clouds))) - 0.099609375f);
                cloud.origin.setY(cloudDepthList[i]);
                cloud.origin.setZ(GlobalRand.floatRange(-20.0f, -10.0f));
                int which = (i % 5) + 1;
                cloud.meshName = "cloud" + which + "m";
                cloud.texName = "clouddark" + which;
                cloud.targetName = "dark_cloud";
                cloud.velocity = new Vector(pref_windSpeed * 1.5f, 0.0f, 0.0f);
                this.mThingManager.add(cloud);
            }
        }
    }

    public void updateSharedPrefs(SharedPreferences prefs, String key) {
        if (key == null || !key.equals("pref_usemipmaps")) {
            backgroundFromPrefs(prefs);
            windSpeedFromPrefs(prefs);
            numCloudsFromPrefs(prefs);
            rainDensityFromPrefs(prefs);
            todFromPrefs(prefs);
            if (key == null) {
                return;
            }
            if (key.contains("numclouds") || key.contains("windspeed") || key.contains("numwisps")) {
                spawnClouds(true);
                return;
            }
            return;
        }
        this.mTextureManager.updatePrefs();
        this.reloadAssets = true;
    }

    private void todFromPrefs(SharedPreferences prefs) {
        pref_useTimeOfDay = prefs.getBoolean(WallpaperSettings.PREF_USE_TOD, false);
        this.pref_todColors[0].set("0.25 0.2 0.2 1", 0.0f, 1.0f);
        this.pref_todColors[1].set("0.6 0.6 0.6 1", 0.0f, 1.0f);
        this.pref_todColors[2].set("0.9 0.9 0.9 1", 0.0f, 1.0f);
        this.pref_todColors[3].set("0.65 0.6 0.6 1", 0.0f, 1.0f);
    }

    public void backgroundFromPrefs(SharedPreferences prefs) {
        String bg = "storm_bg";
        if (!bg.equals(this.pref_background)) {
            this.pref_background = bg;
            this.reloadAssets = true;
        }
    }

    private void rainDensityFromPrefs(SharedPreferences prefs) {
        this.rainDensity = prefs.getInt(WallpaperSettings.PREF_RAIN_DENSITY, 10);
    }

    public void precacheAssets(GL10 gl10) {
        Textures txs = new Textures(mContext.getResources(), (GL11) gl10);
        this.mTextureManager.loadTextureFromPath(gl10, this.pref_background);
        this.mTextureManager.bind(txs.loadBitmap("trees_overlay", R.drawable.trees_overlay));
        this.mTextureManager.bind(txs.loadBitmap("clouddark1", R.drawable.clouddark1));
        this.mTextureManager.bind(txs.loadBitmap("clouddark2", R.drawable.clouddark2));
        this.mTextureManager.bind(txs.loadBitmap("clouddark3", R.drawable.clouddark3));
        this.mTextureManager.bind(txs.loadBitmap("clouddark4", R.drawable.clouddark4));
        this.mTextureManager.bind(txs.loadBitmap("clouddark5", R.drawable.clouddark5));
        this.mTextureManager.bind(txs.loadBitmap("raindrop", R.drawable.raindrop));
        Models mlds = new Models(mContext.getResources(), (GL11) gl10);
        this.mMeshManager.bind(mlds.loadBMDL("plane_16x16", R.raw.plane_16x16));
        this.mMeshManager.bind(mlds.loadBMDL("cloud1m", R.raw.cloud1m));
        this.mMeshManager.bind(mlds.loadBMDL("cloud2m", R.raw.cloud2m));
        this.mMeshManager.bind(mlds.loadBMDL("cloud3m", R.raw.cloud3m));
        this.mMeshManager.bind(mlds.loadBMDL("cloud4m", R.raw.cloud4m));
        this.mMeshManager.bind(mlds.loadBMDL("cloud5m", R.raw.cloud5m));
        this.mMeshManager.bind(mlds.loadBMDL("grass_overlay", R.raw.grass_overlay));
        this.mMeshManager.bind(mlds.loadBMDL("trees_overlay", R.raw.trees_overlay));
        this.mMeshManager.bind(mlds.loadBMDL("trees_overlay_terrain", R.raw.trees_overlay_terrain));
    }

    public void updateTimeOfDay(TimeOfDay tod) {
        if (pref_useTimeOfDay) {
            int iMain = tod.getMainIndex();
            int iBlend = tod.getBlendIndex();
            this.v_light_diffuse.blend(this.pref_todColors[iMain], this.pref_todColors[iBlend], tod.getBlendAmount());
        }
    }

    private void renderBackground(GL10 gl, float timeDelta) {
        Mesh mesh = this.mMeshManager.getMeshByName(gl, "plane_16x16");
        this.mTextureManager.bindTextureID(gl, this.pref_background);
        gl.glColor4f(todColorFinal.getR(), todColorFinal.getG(), todColorFinal.getB(), 1.0f);
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

    private void renderRain(GL10 gl, float timeDelta) {
        if (this.particleRain == null) {
            this.particleRain = new ParticleRain(this.rainDensity);
        }
        gl.glMatrixMode(5888);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.particleRain.update(timeDelta);
        gl.glBlendFunc(1, 0);
        this.particleRain.render((GL11) gl, this.mTextureManager, this.mMeshManager, this.particleRainOrigin);
        gl.glPopMatrix();
    }

    public void draw(GL10 gl, GlobalTime time) {
        checkAssetReload(gl);
        this.mThingManager.update(time.sTimeDelta);
        gl.glEnable(2896);
        gl.glEnable(16384);
        gl.glMatrixMode(5888);
        gl.glLoadIdentity();
        gl.glBlendFunc(1, 771);
        this.light_diffuse[0] = this.v_light_diffuse.getR();
        this.light_diffuse[1] = this.v_light_diffuse.getG();
        this.light_diffuse[2] = this.v_light_diffuse.getB();
        this.light_diffuse[3] = this.v_light_diffuse.getA();
        gl.glLightfv(16384, 4609, this.light_diffuse, 0);
        gl.glLightfv(16384, 4608, this.light_diffuse, 0);
        renderBackground(gl, time.sTimeElapsed);
        renderRain(gl, time.sTimeDelta);
        gl.glTranslatef(0.0f, 0.0f, 40.0f);
        this.mThingManager.render(gl, this.mTextureManager, this.mMeshManager);
        gl.glDisable(16384);
        gl.glDisable(2896);
        drawTree(gl, time.sTimeDelta);
    }
}
