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
import gs.weather.engine.ThingManager;
import gs.weather.engine.Vector;
import gs.weather.sky_manager.TimeOfDay;

import static javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;
import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;

public class SceneStorm extends SceneBase {
    private static final String TAG = "Storm";
    static Color pref_boltColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    float lastLightningSpawn;
    float[] light1_ambientLight;
    float[] light1_position;
    float lightFlashTime;
    float lightFlashX;
    float[] light_ambientLight;
    float[] light_flashColor;
    float[] light_position;
    float[] light_specularLight;
    ParticleRain particleRain;
    Vector particleRainOrigin;
    float pref_boltFrequency;
    float[] pref_diffuseLight;
    boolean pref_flashLights;
    boolean pref_randomBoltColor;
    int rainDensity;
    Color v_light1_ambientLight;

    public SceneStorm(Context ctx) {
        this.rainDensity = 10;
        this.pref_flashLights = true;
        this.pref_randomBoltColor = false;
        this.pref_boltFrequency = 2.0f;
        this.mThingManager = new ThingManager();
        this.mMeshManager = new MeshManager(ctx);
        this.mContext = ctx;
        this.lastLightningSpawn = 0.0f;
        this.lightFlashTime = 0.0f;
        this.lightFlashX = 0.0f;
        this.light_ambientLight = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        this.pref_diffuseLight = new float[]{1.5f, 1.5f, 1.5f, 1.0f};
        this.light_specularLight = new float[]{0.1f, 0.1f, 0.1f, 1.0f};
        this.light_position = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        this.light_flashColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        this.v_light1_ambientLight = new Color(0.5f, 0.5f, 0.5f, 1.0f);
        this.light1_ambientLight = new float[4];
        this.light1_position = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        this.pref_numClouds = 20;
        todColorFinal = new Color();
        this.pref_todColors = new Color[4];
        this.pref_todColors[0] = new Color();
        this.pref_todColors[1] = new Color();
        this.pref_todColors[2] = new Color();
        this.pref_todColors[3] = new Color();
        this.particleRain = new ParticleRain(this.rainDensity);
        this.particleRainOrigin = new Vector(0.0f, 25.0f, 10.0f);
        this.reloadAssets = false;
    }

    public void updateSharedPrefs(SharedPreferences prefs, String key) {
        if (key == null || !key.equals("pref_usemipmaps")) {
            backgroundFromPrefs(prefs);
            windSpeedFromPrefs(prefs);
            numCloudsFromPrefs(prefs);
            rainDensityFromPrefs(prefs);
            todFromPrefs(prefs);
            if (key != null && (key.contains("numclouds") || key.contains("windspeed") || key.contains("numwisps"))) {
                spawnClouds(true);
            }
            this.pref_randomBoltColor = prefs.getBoolean("pref_randomboltcolor", false);
            boltColorFromPrefs(prefs);
            boltFrequencyFromPrefs(prefs);
            return;
        }
        this.reloadAssets = true;
    }

    public void backgroundFromPrefs(SharedPreferences prefs) {
    }

    private void rainDensityFromPrefs(SharedPreferences prefs) {
        this.rainDensity = prefs.getInt(WallpaperSettings.PREF_RAIN_DENSITY, 10);
    }

    private void todFromPrefs(SharedPreferences prefs) {
        pref_useTimeOfDay = prefs.getBoolean(WallpaperSettings.PREF_USE_TOD, false);
        this.pref_todColors[0].set("0.25 0.2 0.2 1", 0.0f, 1.0f);
        this.pref_todColors[1].set("0.6 0.6 0.6 1", 0.0f, 1.0f);
        this.pref_todColors[2].set("0.9 0.9 0.9 1", 0.0f, 1.0f);
        this.pref_todColors[3].set("0.65 0.6 0.6 1", 0.0f, 1.0f);
    }

    public void boltColorFromPrefs(SharedPreferences prefs) {
        pref_boltColor.set(prefs.getString("pref_boltcolor", "1 1 1 1"), 0.0f, 1.0f);
    }

    public void boltFrequencyFromPrefs(SharedPreferences prefs) {
        this.pref_boltFrequency = Float.parseFloat(prefs.getString("pref_boltfrequency", "5"));
    }

    public void precacheAssets(GL10 gl10) {
        super.precacheAssets(gl10);

        textures.loadBitmap("storm_bg", R.drawable.storm_bg);
        textures.loadBitmap("trees_overlay", R.drawable.trees_overlay);
        textures.loadBitmap("clouddark1", R.drawable.clouddark1);
        textures.loadBitmap("clouddark2", R.drawable.clouddark2);
        textures.loadBitmap("clouddark3", R.drawable.clouddark3);
        textures.loadBitmap("clouddark4", R.drawable.clouddark4);
        textures.loadBitmap("clouddark5", R.drawable.clouddark5);
        textures.loadBitmap("cloudflare1", R.drawable.cloudflare1);
        textures.loadBitmap("cloudflare2", R.drawable.cloudflare2);
        textures.loadBitmap("cloudflare3", R.drawable.cloudflare3);
        textures.loadBitmap("cloudflare4", R.drawable.cloudflare4);
        textures.loadBitmap("cloudflare5", R.drawable.cloudflare5);
        textures.loadBitmap("raindrop", R.drawable.raindrop);
        models.loadBMDL("plane_16x16", R.raw.plane_16x16);
        models.loadBMDL("cloud1m", R.raw.cloud1m);
        models.loadBMDL("cloud2m", R.raw.cloud2m);
        models.loadBMDL("cloud3m", R.raw.cloud3m);
        models.loadBMDL("cloud4m", R.raw.cloud4m);
        models.loadBMDL("cloud5m", R.raw.cloud5m);
        models.loadBMDL("grass_overlay", R.raw.grass_overlay);
        models.loadBMDL("trees_overlay", R.raw.trees_overlay);
        models.loadBMDL("trees_overlay_terrain", R.raw.trees_overlay_terrain);
    }

    public void load(GL10 gl) {
        spawnClouds(false);
    }

    public void unload(GL10 gl) {
        super.unload(gl);
        gl.glDisable(GL_COLOR_BUFFER_BIT);
        gl.glDisable(16385);
        gl.glDisable(GL_LIGHTING);
    }

    public void updateTimeOfDay(TimeOfDay tod) {
        if (pref_useTimeOfDay) {
            int iMain = tod.getMainIndex();
            int iBlend = tod.getBlendIndex();
            this.v_light1_ambientLight.blend(this.pref_todColors[iMain], this.pref_todColors[iBlend], tod.getBlendAmount());
        }
    }

    public void draw(GL10 gl, GlobalTime time) {
        checkAssetReload(gl);
        this.mThingManager.update(time.sTimeDelta);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glBlendFunc(1, 771);
        renderBackground(gl, time.sTimeElapsed);
        renderRain(gl, time.sTimeDelta);
        checkForLightning(time.sTimeDelta);
        updateLightValues(gl, time.sTimeDelta);
        gl.glTranslatef(0.0f, 0.0f, 40.0f);
        this.mThingManager.render(gl, textures, models);
        drawTree(gl, time.sTimeDelta);
    }

    private void renderBackground(GL10 gl, float timeDelta) {
        Mesh mesh = this.mMeshManager.getMeshByName(gl, "plane_16x16");
        gl.glBindTexture(GL_TEXTURE_2D, textures.get("storm_bg").getId());
        gl.glColor4f(todColorFinal.getR(), todColorFinal.getG(), todColorFinal.getB(), 1.0f);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 250.0f, 35.0f);
        gl.glScalef(this.BG_PADDING * 2.0f, this.BG_PADDING, this.BG_PADDING);
        gl.glMatrixMode(5890);
        gl.glPushMatrix();
        gl.glTranslatef(((pref_windSpeed * timeDelta) * -0.005f) % 1.0f, 0.0f, 0.0f);
        if (!this.pref_flashLights || this.lightFlashTime <= 0.0f) {
            gl.glEnable(GL_LIGHTING);
            gl.glEnable(16385);
            this.light1_ambientLight[0] = this.v_light1_ambientLight.getR();
            this.light1_ambientLight[1] = this.v_light1_ambientLight.getG();
            this.light1_ambientLight[2] = this.v_light1_ambientLight.getB();
            this.light1_ambientLight[3] = this.v_light1_ambientLight.getA();
            gl.glLightfv(16385, 4608, this.light1_ambientLight, 0);
        }
        mesh.render(gl);
        gl.glDisable(16385);
        gl.glPopMatrix();
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPopMatrix();
    }

    private void renderRain(GL10 gl, float timeDelta) {
        if (this.particleRain == null) {
            this.particleRain = new ParticleRain(this.rainDensity);
        }
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.particleRain.update(timeDelta);
        gl.glBlendFunc(1, 0);
        this.particleRain.render((GL11) gl, this.mMeshManager, this.particleRainOrigin);
        gl.glPopMatrix();
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
                ThingDarkCloud cloud = new ThingDarkCloud(true);
                cloud.randomizeScale();
                if (GlobalRand.intRange(0, 2) == 0) {
                    cloud.scale.setX(cloud.scale.getX() * -1.0f);
                }
                cloud.origin.setX((((float) i) * (90.0f / ((float) num_clouds))) - 0.099609375f);
                cloud.origin.setY(cloudDepthList[i]);
                cloud.origin.setZ(GlobalRand.floatRange(-20.0f, -10.0f));
                cloud.which = (i % 5) + 1;
                cloud.model = null;
                cloud.texture = null;
                cloud.texNameFlare = null;
                cloud.targetName = "dark_cloud";
                cloud.velocity = new Vector(pref_windSpeed * 1.5f, 0.0f, 0.0f);
                this.mThingManager.add(cloud);
            }
        }
    }

    private void spawnLightning() {
        if (this.pref_randomBoltColor) {
            GlobalRand.randomNormalizedVector(pref_boltColor);
        }
        ThingLightning lightning = new ThingLightning(pref_boltColor.getR(), pref_boltColor.getG(), pref_boltColor.getB());
        lightning.origin.set(GlobalRand.floatRange(-25.0f, 25.0f), GlobalRand.floatRange(95.0f, 168.0f), 20.0f);
        if (GlobalRand.intRange(0, 2) == 0) {
            lightning.scale.setZ(lightning.scale.getZ() * -1.0f);
        }
        this.mThingManager.add(lightning);
        this.mThingManager.sortByY();
        this.lightFlashTime = 0.25f;
        this.lightFlashX = lightning.origin.getX();
    }

    private void checkForLightning(float timeDelta) {
        if (GlobalRand.floatRange(0.0f, this.pref_boltFrequency * 0.75f) < timeDelta) {
            spawnLightning();
        }
    }

    private void updateLightValues(GL10 gl, float timeDelta) {
        float lightPosX = GlobalTime.waveCos(0.0f, 500.0f, 0.0f, 0.005f);
        if (!this.pref_flashLights || this.lightFlashTime <= 0.0f) {
            this.light_position[0] = lightPosX;
            gl.glLightfv(GL_COLOR_BUFFER_BIT, 4610, this.light_specularLight, 0);
        } else {
            float flashRemaining = this.lightFlashTime / 0.25f;
            this.light_position[0] = (this.lightFlashX * flashRemaining) + ((1.0f - flashRemaining) * lightPosX);
            this.light_flashColor[0] = pref_boltColor.getR();
            this.light_flashColor[1] = pref_boltColor.getG();
            this.light_flashColor[2] = pref_boltColor.getB();
            gl.glLightfv(GL_COLOR_BUFFER_BIT, 4610, this.light_flashColor, 0);
            this.lightFlashTime -= timeDelta;
        }
        this.light_position[1] = 50.0f;
        this.light_position[2] = GlobalTime.waveSin(0.0f, 500.0f, 0.0f, 0.005f);
        gl.glLightfv(GL_COLOR_BUFFER_BIT, 4608, this.light_ambientLight, 0);
        gl.glLightfv(GL_COLOR_BUFFER_BIT, 4609, this.pref_diffuseLight, 0);
        gl.glLightfv(GL_COLOR_BUFFER_BIT, 4611, this.light_position, 0);
    }
}
