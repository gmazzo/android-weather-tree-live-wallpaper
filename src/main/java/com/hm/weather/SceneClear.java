package com.hm.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import com.hm.weather.engine.GlobalRand;
import com.hm.weather.engine.GlobalTime;
import com.hm.weather.engine.Mesh;
import com.hm.weather.engine.MeshManager;
import com.hm.weather.engine.TextureManager;
import com.hm.weather.engine.Thing;
import com.hm.weather.engine.ThingManager;
import com.hm.weather.engine.Vector3;
import com.hm.weather.engine.Vector4;
import com.hm.weather.sky_manager.TimeOfDay;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class SceneClear extends SceneBase {
    protected static final float BALLOON_START_ALTITUDE = -50.0f;
    protected static final float CLOUD_START_DISTANCE = 175.0f;
    protected static final float CLOUD_X_RANGE = 45.0f;
    protected static final float CLOUD_Z_RANGE = 20.0f;
    private static final String TAG = "Clear";
    protected static final float UFO_START_ALTITUDE = 65.0f;
    protected static final float WISPY_X_RANGE = 60.0f;
    protected static final float WISPY_Z_RANGE = 30.0f;
    protected static String[] validBalloonTextures = new String[]{"bal_red", "bal_blue", "bal_yellow", "bal_green"};
    protected int batteryLevel;
    protected float nextUfoSpawn;
    private int pref_numBalloons;
    private boolean pref_redBalloonsOnly;
    private boolean pref_smsBalloons;
    private boolean pref_ufoBattery;
    private boolean pref_useBalloons;
    private boolean pref_useMoon;
    private boolean pref_useSun;
    private boolean pref_useUfo;
    protected long smsLastUnreadCheckTime;
    protected int smsUnreadCount;

    public SceneClear(Context ctx) {
        this.mThingManager = new ThingManager();
        this.mTextureManager = new TextureManager(ctx);
        this.mMeshManager = new MeshManager(ctx);
        this.mContext = ctx;
        this.pref_background = "bg3";
        todColorFinal = new Vector4();
        this.pref_todColors = new Vector4[4];
        this.pref_todColors[0] = new Vector4();
        this.pref_todColors[1] = new Vector4();
        this.pref_todColors[2] = new Vector4();
        this.pref_todColors[3] = new Vector4();
        this.reloadAssets = true;
        this.batteryLevel = 100;
        this.pref_numClouds = 20;
        this.pref_numWisps = 6;
        this.pref_numBalloons = 5;
        this.pref_useUfo = true;
        this.pref_ufoBattery = true;
        this.pref_useSun = true;
        this.pref_useMoon = true;
        this.nextUfoSpawn = WISPY_X_RANGE;
        this.pref_useBalloons = false;
        this.pref_smsBalloons = false;
        this.pref_redBalloonsOnly = false;
        this.smsUnreadCount = 0;
        this.smsLastUnreadCheckTime = 0;
    }

    public void load(GL10 gl) {
        checkSun();
        checkMoon();
        spawnClouds(false);
    }

    public void updateSharedPrefs(SharedPreferences prefs, String key) {
        if (key == null || !key.equals("pref_usemipmaps")) {
            backgroundFromPrefs(prefs);
            windSpeedFromPrefs(prefs);
            numCloudsFromPrefs(prefs);
            numBalloonsFromPrefs(prefs);
            todFromPrefs(prefs);
            this.pref_useSun = prefs.getBoolean("pref_usesun", true);
            this.pref_useMoon = prefs.getBoolean("pref_usemoon", true);
            this.pref_useBalloons = prefs.getBoolean("pref_useballoons", false);
            this.pref_smsBalloons = prefs.getBoolean("pref_smsballoons", false);
            this.pref_redBalloonsOnly = prefs.getBoolean("pref_redballoonsonly", false);
            this.pref_useUfo = prefs.getBoolean("pref_useufo", false);
            this.pref_ufoBattery = prefs.getBoolean("pref_ufobattery", true);
            if (key != null && (key.contains("numclouds") || key.contains("windspeed") || key.contains("numwisps"))) {
                spawnClouds(true);
            }
            if (!this.pref_useBalloons) {
                clearBalloons();
            }
            if (key != null && key.contains("usesun")) {
                spawnSun();
            }
            if (key != null && key.contains("usemoon")) {
                spawnMoon();
                return;
            }
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
        this.mTextureManager.loadTextureFromPath(gl10, "sun", false);
        this.mTextureManager.loadTextureFromPath(gl10, "sun_blend", false);
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

    private void checkBalloonCount() {
        if (this.pref_useBalloons) {
            long currentTime = this.mGlobalTime.msTimeCurrent;
            if (!this.pref_smsBalloons) {
                this.smsUnreadCount = this.pref_numBalloons;
            } else if (currentTime > this.smsLastUnreadCheckTime + 10000) {
                try {
                    Cursor localCursor = this.mContext.getContentResolver().query(Uri.parse("content://sms/inbox"), null, "read = 0", null, null);
                    this.smsUnreadCount = localCursor.getCount();
                    localCursor.deactivate();
                    localCursor.close();
                } catch (Exception e) {
                }
                this.smsLastUnreadCheckTime = currentTime;
            }
            if (this.smsUnreadCount < 0) {
                this.smsUnreadCount = 0;
            } else {
                manageBalloons(this.smsUnreadCount);
            }
        }
    }

    private void checkSpawnUFO(float timeDelta) {
        if (this.pref_useUfo && !this.pref_ufoBattery) {
            this.nextUfoSpawn -= timeDelta;
            if (this.nextUfoSpawn <= 0.0f) {
                spawnUFO();
                this.nextUfoSpawn = GlobalRand.floatRange(CLOUD_X_RANGE, 225.0f);
            }
        }
    }

    protected void spawnClouds(boolean force) {
        spawnClouds(this.pref_numClouds, this.pref_numWisps, force);
    }

    private void checkMoon() {
        if (this.pref_useMoon) {
            spawnMoon();
        } else {
            removeMoon();
        }
    }

    private void checkSun() {
        if (this.pref_useSun) {
            spawnSun();
        } else {
            removeSun();
        }
    }

    private void spawnBallonAt(Vector3 pos) {
        spawnBalloonAt(pos, this.pref_redBalloonsOnly);
    }

    public void numBalloonsFromPrefs(SharedPreferences prefs) {
        this.pref_numBalloons = Integer.valueOf(prefs.getString("pref_ballooncounttarget", "5")).intValue();
    }

    public void backgroundFromPrefs(SharedPreferences prefs) {
        String bg = "bg3";
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
    }

    private void clearBalloons() {
        this.mThingManager.clearByTargetname("balloon");
    }

    private void manageBalloons(int desiredCount) {
        int i;
        if (desiredCount > 12) {
            desiredCount = 12;
        }
        Thing[] balloons = this.mThingManager.getByTargetname("balloon");
        int numActive = 0;
        for (Thing thing : balloons) {
            if (((ThingBalloon) thing).isActive()) {
                numActive++;
            }
        }
        int diff;
        if (numActive < desiredCount) {
            diff = desiredCount - numActive;
            for (i = 0; i < diff; i++) {
                spawnBalloon(this.pref_redBalloonsOnly);
            }
        } else if (numActive > desiredCount) {
            diff = numActive - desiredCount;
            for (i = 0; i < diff; i++) {
                ((ThingBalloon) balloons[i]).deactivate();
            }
        }
    }

    private void removeMoon() {
        this.mThingManager.clearByTargetname("moon");
    }

    private void removeSun() {
        this.mThingManager.clearByTargetname("sun");
    }

    private void spawnBalloon(boolean redOnly) {
        ThingBalloon thingballoon = new ThingBalloon();
        if (redOnly) {
            thingballoon.texName = "bal_red";
        } else {
            thingballoon.texName = validBalloonTextures[GlobalRand.intRange(0, validBalloonTextures.length)];
        }
        float r_c = GlobalRand.floatRange(0.0f, 1.0f);
        float y = 87.5f + ((CLOUD_START_DISTANCE * GlobalRand.floatRange(0.0f, 1.0f)) * 0.5f);
        thingballoon.origin.x = GlobalRand.floatRange(-15.0f, 15.0f);
        thingballoon.origin.y = y;
        thingballoon.origin.z = BALLOON_START_ALTITUDE;
        float color = 1.0f - (r_c * 0.5f);
        thingballoon.color.set(color, color, color, color);
        thingballoon.goalAltitude = GlobalRand.floatRange(9.900001f, WISPY_Z_RANGE);
        this.mThingManager.add(thingballoon);
    }

    private void spawnBalloonAt(Vector3 pos, boolean redOnly) {
        ThingBalloon thingballoon = new ThingBalloon();
        if (redOnly) {
            thingballoon.texName = "bal_red";
        } else {
            thingballoon.texName = validBalloonTextures[GlobalRand.intRange(0, validBalloonTextures.length)];
        }
        float y = 87.5f + ((CLOUD_START_DISTANCE * GlobalRand.floatRange(0.0f, 1.0f)) * 0.5f);
        thingballoon.origin.x = pos.x;
        thingballoon.origin.y = y;
        thingballoon.origin.z = pos.z;
        float c = 1.0f - (GlobalRand.floatRange(0.0f, 1.0f) * 0.5f);
        thingballoon.color.set(c, c, c, c);
        thingballoon.goalAltitude = GlobalRand.floatRange(9.900001f, WISPY_Z_RANGE);
        this.mThingManager.add(thingballoon);
    }

    private void spawnMoon() {
        if (this.mThingManager.countByTargetname("moon") == 0) {
            ThingMoon moon = new ThingMoon();
            moon.origin.set(-30.0f, 100.0f, -100.0f);
            moon.targetName = "moon";
            this.mThingManager.add(moon);
        }
    }

    private void spawnSun() {
        if (this.mThingManager.countByTargetname("sun") == 0) {
            ThingSun sun = new ThingSun();
            sun.origin.set(WISPY_Z_RANGE, 100.0f, 0.0f);
            sun.targetName = "sun";
            this.mThingManager.add(sun);
        }
    }

    private void spawnUFO() {
        if (this.mThingManager.countByTargetname("ufo") <= 0) {
            ThingUFO ufo = new ThingUFO();
            float rand_y = (CLOUD_START_DISTANCE * GlobalRand.floatRange(0.0f, 1.0f)) * 0.5f;
            ufo.origin.x = GlobalRand.floatRange(0.0f, 0.0f);
            ufo.origin.y = 87.5f + rand_y;
            ufo.origin.z = UFO_START_ALTITUDE;
            this.mThingManager.add(ufo);
        }
    }

    protected void spawnClouds(int num_clouds, int num_wisps, boolean force) {
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
        mesh.render(gl);
        renderStars(gl, timeDelta);
        gl.glPopMatrix();
        gl.glMatrixMode(5888);
        gl.glPopMatrix();
    }

    private void renderStars(GL10 gl, float timeDelta) {
        if (pref_useTimeOfDay && todSunPosition <= 0.0f) {
            gl.glColor4f(1.0f, 1.0f, 1.0f, todSunPosition * -2.0f);
            gl.glBlendFunc(770, 1);
            Mesh starMesh = this.mMeshManager.getMeshByName(gl, "stars");
            int noiseId = this.mTextureManager.getTextureID(gl, "noise");
            int starId = this.mTextureManager.getTextureID(gl, "stars");
            gl.glTranslatef((0.1f * timeDelta) % 1.0f, 300.0f, -100.0f);
            if (gl instanceof GL11) {
                starMesh.renderFrameMultiTexture((GL11) gl, 0, noiseId, starId, 8448, false);
                return;
            }
            gl.glBindTexture(33984, starId);
            starMesh.render(gl);
        }
    }
}
