package com.hm.weather;

import android.content.Context;
import android.content.SharedPreferences;
import com.hm.weather.engine.GlobalRand;
import com.hm.weather.engine.GlobalTime;
import com.hm.weather.engine.Mesh;
import com.hm.weather.engine.MeshManager;
import com.hm.weather.engine.TextureManager;
import com.hm.weather.engine.ThingManager;
import com.hm.weather.engine.Vector3;
import com.hm.weather.engine.Vector4;
import com.hm.weather.sky_manager.TimeOfDay;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class SceneStorm extends SceneBase {
    private static final String TAG = "Storm";
    static Vector4 pref_boltColor = new Vector4(1.0f, 1.0f, 1.0f, 1.0f);
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
    Vector3 particleRainOrigin;
    float pref_boltFrequency;
    float[] pref_diffuseLight;
    boolean pref_flashLights;
    boolean pref_randomBoltColor;
    int rainDensity;
    Vector4 v_light1_ambientLight;

    public SceneStorm(Context ctx) {
        this.rainDensity = 10;
        this.pref_flashLights = true;
        this.pref_randomBoltColor = false;
        this.pref_boltFrequency = 2.0f;
        this.mThingManager = new ThingManager();
        this.mTextureManager = new TextureManager(ctx);
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
        this.v_light1_ambientLight = new Vector4(0.5f, 0.5f, 0.5f, 1.0f);
        this.light1_ambientLight = new float[4];
        this.light1_position = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        this.pref_background = "storm_bg";
        this.pref_numClouds = 20;
        todColorFinal = new Vector4();
        this.pref_todColors = new Vector4[4];
        this.pref_todColors[0] = new Vector4();
        this.pref_todColors[1] = new Vector4();
        this.pref_todColors[2] = new Vector4();
        this.pref_todColors[3] = new Vector4();
        this.particleRain = new ParticleRain(this.rainDensity);
        this.particleRainOrigin = new Vector3(0.0f, 25.0f, 10.0f);
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
        this.mTextureManager.updatePrefs();
        this.reloadAssets = true;
    }

    public void backgroundFromPrefs(SharedPreferences prefs) {
        String bg = "storm_bg";
        if (!bg.equals(this.pref_background)) {
            this.pref_background = bg;
            this.reloadAssets = true;
        }
    }

    private void rainDensityFromPrefs(SharedPreferences prefs) {
        this.rainDensity = prefs.getInt(BaseWallpaperSettings.PREF_RAIN_DENSITY, 10);
    }

    private void todFromPrefs(SharedPreferences prefs) {
        pref_useTimeOfDay = prefs.getBoolean(BaseWallpaperSettings.PREF_USE_TOD, false);
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
        this.mTextureManager.loadTextureFromPath(gl10, this.pref_background);
        this.mTextureManager.loadTextureFromPath(gl10, "trees_overlay");
        this.mTextureManager.loadTextureFromPath(gl10, "clouddark1");
        this.mTextureManager.loadTextureFromPath(gl10, "clouddark2");
        this.mTextureManager.loadTextureFromPath(gl10, "clouddark3");
        this.mTextureManager.loadTextureFromPath(gl10, "clouddark4");
        this.mTextureManager.loadTextureFromPath(gl10, "clouddark5");
        this.mTextureManager.loadTextureFromPath(gl10, "cloudflare1");
        this.mTextureManager.loadTextureFromPath(gl10, "cloudflare2");
        this.mTextureManager.loadTextureFromPath(gl10, "cloudflare3");
        this.mTextureManager.loadTextureFromPath(gl10, "cloudflare4");
        this.mTextureManager.loadTextureFromPath(gl10, "cloudflare5");
        this.mTextureManager.loadTextureFromPath(gl10, "raindrop");
        this.mMeshManager.createMeshFromFile(gl10, "plane_16x16");
        this.mMeshManager.createMeshFromFile(gl10, "cloud1m");
        this.mMeshManager.createMeshFromFile(gl10, "cloud2m");
        this.mMeshManager.createMeshFromFile(gl10, "cloud3m");
        this.mMeshManager.createMeshFromFile(gl10, "cloud4m");
        this.mMeshManager.createMeshFromFile(gl10, "cloud5m");
        this.mMeshManager.createMeshFromFile(gl10, "grass_overlay", true);
        this.mMeshManager.createMeshFromFile(gl10, "trees_overlay", true);
        this.mMeshManager.createMeshFromFile(gl10, "trees_overlay_terrain");
    }

    public void load(GL10 gl) {
        spawnClouds(false);
    }

    public void unload(GL10 gl) {
        super.unload(gl);
        gl.glDisable(16384);
        gl.glDisable(16385);
        gl.glDisable(2896);
    }

    public void updateTimeOfDay(TimeOfDay tod) {
        if (pref_useTimeOfDay) {
            int iMain = tod.getMainIndex();
            int iBlend = tod.getBlendIndex();
            Vector4.mix(this.v_light1_ambientLight, this.pref_todColors[iMain], this.pref_todColors[iBlend], tod.getBlendAmount());
        }
    }

    public void draw(GL10 gl, GlobalTime time) {
        checkAssetReload(gl);
        this.mThingManager.update(time.sTimeDelta);
        gl.glMatrixMode(5888);
        gl.glLoadIdentity();
        gl.glBlendFunc(1, 771);
        renderBackground(gl, time.sTimeElapsed);
        renderRain(gl, time.sTimeDelta);
        checkForLightning(time.sTimeDelta);
        updateLightValues(gl, time.sTimeDelta);
        gl.glTranslatef(0.0f, 0.0f, 40.0f);
        this.mThingManager.render(gl, this.mTextureManager, this.mMeshManager);
        drawTree(gl, time.sTimeDelta);
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
        if (!this.pref_flashLights || this.lightFlashTime <= 0.0f) {
            gl.glEnable(2896);
            gl.glEnable(16385);
            this.light1_ambientLight[0] = this.v_light1_ambientLight.x;
            this.light1_ambientLight[1] = this.v_light1_ambientLight.y;
            this.light1_ambientLight[2] = this.v_light1_ambientLight.z;
            this.light1_ambientLight[3] = this.v_light1_ambientLight.a;
            gl.glLightfv(16385, 4608, this.light1_ambientLight, 0);
        }
        mesh.render(gl);
        gl.glDisable(16385);
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
                    cloud.scale.x *= -1.0f;
                }
                cloud.origin.x = (((float) i) * (90.0f / ((float) num_clouds))) - 0.099609375f;
                cloud.origin.y = cloudDepthList[i];
                cloud.origin.z = GlobalRand.floatRange(-20.0f, -10.0f);
                int which = (i % 5) + 1;
                cloud.meshName = "cloud" + which + "m";
                cloud.texName = "clouddark" + which;
                cloud.texNameFlare = "cloudflare" + which;
                cloud.targetName = "dark_cloud";
                cloud.velocity = new Vector3(pref_windSpeed * 1.5f, 0.0f, 0.0f);
                this.mThingManager.add(cloud);
            }
        }
    }

    private void spawnLightning(Vector3 touchPos) {
        boolean isTouch = false;
        if (touchPos != null) {
            isTouch = true;
        }
        if (this.pref_randomBoltColor) {
            GlobalRand.randomNormalizedVector(pref_boltColor);
        }
        ThingLightning lightning = new ThingLightning(pref_boltColor.x, pref_boltColor.y, pref_boltColor.z, isTouch);
        if (isTouch) {
            lightning.origin.set(touchPos);
        } else {
            lightning.origin.set(GlobalRand.floatRange(-25.0f, 25.0f), GlobalRand.floatRange(95.0f, 168.0f), 20.0f);
        }
        if (GlobalRand.intRange(0, 2) == 0) {
            lightning.scale.z *= -1.0f;
        }
        this.mThingManager.add(lightning);
        this.mThingManager.sortByY();
        this.lightFlashTime = 0.25f;
        this.lightFlashX = lightning.origin.x;
    }

    private void checkForLightning(float timeDelta) {
        if (GlobalRand.floatRange(0.0f, this.pref_boltFrequency * 0.75f) < timeDelta) {
            spawnLightning(null);
        }
    }

    private void updateLightValues(GL10 gl, float timeDelta) {
        float lightPosX = GlobalTime.waveCos(0.0f, 500.0f, 0.0f, 0.005f);
        if (!this.pref_flashLights || this.lightFlashTime <= 0.0f) {
            this.light_position[0] = lightPosX;
            gl.glLightfv(16384, 4610, this.light_specularLight, 0);
        } else {
            float flashRemaining = this.lightFlashTime / 0.25f;
            this.light_position[0] = (this.lightFlashX * flashRemaining) + ((1.0f - flashRemaining) * lightPosX);
            this.light_flashColor[0] = pref_boltColor.x;
            this.light_flashColor[1] = pref_boltColor.y;
            this.light_flashColor[2] = pref_boltColor.z;
            gl.glLightfv(16384, 4610, this.light_flashColor, 0);
            this.lightFlashTime -= timeDelta;
        }
        this.light_position[1] = 50.0f;
        this.light_position[2] = GlobalTime.waveSin(0.0f, 500.0f, 0.0f, 0.005f);
        gl.glLightfv(16384, 4608, this.light_ambientLight, 0);
        gl.glLightfv(16384, 4609, this.pref_diffuseLight, 0);
        gl.glLightfv(16384, 4611, this.light_position, 0);
    }
}
