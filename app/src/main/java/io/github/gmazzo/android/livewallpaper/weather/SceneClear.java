package io.github.gmazzo.android.livewallpaper.weather;

import static javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;
import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_MODULATE;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.DrawableRes;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor;
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand;
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime;
import io.github.gmazzo.android.livewallpaper.weather.engine.ThingManager;
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector;
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingCloud;
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingMoon;
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingSun;
import io.github.gmazzo.android.livewallpaper.weather.engine.things.ThingWispy;
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Model;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Texture;

public class SceneClear extends SceneBase {
    protected static final float BALLOON_START_ALTITUDE = -50.0f;
    protected static final float CLOUD_START_DISTANCE = 175.0f;
    public static final float CLOUD_X_RANGE = 45.0f;
    protected static final float CLOUD_Z_RANGE = 20.0f;
    private static final String TAG = "Clear";
    protected static final float UFO_START_ALTITUDE = 65.0f;
    protected static final float WISPY_X_RANGE = 60.0f;
    protected static final float WISPY_Z_RANGE = 30.0f;
    protected static String[] validBalloonTextures = new String[]{"bal_red", "bal_blue", "bal_yellow", "bal_green"};
    protected int batteryLevel;
    protected float nextUfoSpawn;
    private boolean pref_ufoBattery;
    private boolean pref_useMoon;
    private boolean pref_useSun;
    private boolean pref_useUfo;
    protected long smsLastUnreadCheckTime;
    protected int smsUnreadCount;
    private final @DrawableRes
    int backgroundId;

    public SceneClear(Context context, GL11 gl) {
        this(context, gl, R.drawable.bg3);
    }

    public SceneClear(Context context, GL11 gl, @DrawableRes int backgroundId) {
        super(context, gl);
        this.backgroundId = backgroundId;
        this.mThingManager = new ThingManager();
        todEngineColorFinal = new EngineColor();
        this.pref_todEngineColors = new EngineColor[4];
        this.pref_todEngineColors[0] = new EngineColor();
        this.pref_todEngineColors[1] = new EngineColor();
        this.pref_todEngineColors[2] = new EngineColor();
        this.pref_todEngineColors[3] = new EngineColor();
        this.reloadAssets = true;
        this.batteryLevel = 100;
        this.pref_numClouds = 20;
        this.pref_numWisps = 6;
        this.pref_useUfo = true;
        this.pref_ufoBattery = true;
        this.pref_useSun = true;
        this.pref_useMoon = true;
        this.nextUfoSpawn = WISPY_X_RANGE;
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
            todFromPrefs(prefs);
            this.pref_useSun = prefs.getBoolean("pref_usesun", true);
            this.pref_useMoon = prefs.getBoolean("pref_usemoon", true);
            this.pref_useUfo = prefs.getBoolean("pref_useufo", false);
            this.pref_ufoBattery = prefs.getBoolean("pref_ufobattery", true);
            if (key != null && (key.contains("numclouds") || key.contains("windspeed") || key.contains("numwisps"))) {
                spawnClouds(true);
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
        this.reloadAssets = true;
    }

    public void precacheAssets(GL10 gl10) {
        textures.get(backgroundId);
        textures.get(R.drawable.trees_overlay);
        textures.get(R.drawable.cloud1);
        textures.get(R.drawable.cloud2);
        textures.get(R.drawable.cloud3);
        textures.get(R.drawable.cloud4);
        textures.get(R.drawable.cloud5);
        textures.get(R.drawable.stars);
        textures.get(R.drawable.noise);
        textures.get(R.raw.wispy1);
        textures.get(R.raw.wispy2);
        textures.get(R.raw.wispy3);
        textures.get(R.raw.sun);
        textures.get(R.raw.sun_blend);
        textures.get(R.drawable.moon_0);
        models.get(R.raw.plane_16x16);
        models.get(R.raw.cloud1m);
        models.get(R.raw.cloud2m);
        models.get(R.raw.cloud3m);
        models.get(R.raw.cloud4m);
        models.get(R.raw.cloud5m);
        models.get(R.raw.grass_overlay);
        models.get(R.raw.trees_overlay);
        models.get(R.raw.trees_overlay_terrain);
        models.get(R.raw.stars);
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

    public void backgroundFromPrefs(SharedPreferences prefs) {
    }

    private void todFromPrefs(SharedPreferences prefs) {
        pref_useTimeOfDay = prefs.getBoolean(WallpaperSettings.PREF_USE_TOD, false);
        this.pref_todEngineColors[0].set(prefs.getString(WallpaperSettings.PREF_LIGHT_COLOR1, "0.5 0.5 0.75 1"), 0.0f, 1.0f);
        this.pref_todEngineColors[1].set(prefs.getString(WallpaperSettings.PREF_LIGHT_COLOR2, "1 0.73 0.58 1"), 0.0f, 1.0f);
        this.pref_todEngineColors[2].set(prefs.getString(WallpaperSettings.PREF_LIGHT_COLOR3, "1 1 1 1"), 0.0f, 1.0f);
        this.pref_todEngineColors[3].set(prefs.getString(WallpaperSettings.PREF_LIGHT_COLOR4, "1 0.85 0.75 1"), 0.0f, 1.0f);
    }

    private void removeMoon() {
        this.mThingManager.clearByTargetname("moon");
    }

    private void removeSun() {
        this.mThingManager.clearByTargetname("sun");
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
                    cloud.scale.setX(cloud.scale.getX() * -1.0f);
                }
                cloud.origin.setX((((float) i) * (90.0f / ((float) num_clouds))) - 0.099609375f);
                cloud.origin.setY(cloudDepthList[i]);
                cloud.origin.setZ(GlobalRand.floatRange(-20.0f, -10.0f));
                cloud.which = (i % 5) + 1;
                cloud.targetName = "cloud";
                cloud.velocity = new Vector(pref_windSpeed * 1.5f, 0.0f, 0.0f);
                this.mThingManager.add(cloud);
            }
            for (i = 0; i < cloudDepthList.length; i++) {
                ThingWispy wispy = new ThingWispy();
                wispy.which = (i % 3) + 1;
                wispy.targetName = "wispy";
                wispy.velocity = new Vector(pref_windSpeed * 1.5f, 0.0f, 0.0f);
                wispy.scale.set(GlobalRand.floatRange(1.0f, 3.0f), 1.0f, GlobalRand.floatRange(1.0f, 1.5f));
                wispy.origin.setX((((float) i) * (120.0f / ((float) num_wisps))) - 0.0703125f);
                wispy.origin.setY(GlobalRand.floatRange(87.5f, CLOUD_START_DISTANCE));
                wispy.origin.setZ(GlobalRand.floatRange(-40.0f, -20.0f));
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
        gl.glDisable(GL_COLOR_BUFFER_BIT);
        gl.glDisable(16385);
        gl.glDisable(GL_LIGHTING);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glBlendFunc(1, 771);
        renderBackground(gl, time.sTimeElapsed);
        gl.glTranslatef(0.0f, 0.0f, 40.0f);
        this.mThingManager.render(gl, textures, models);
        drawTree(gl, time.sTimeDelta);
    }

    private void renderBackground(GL10 gl, float timeDelta) {
        gl.glBindTexture(GL_TEXTURE_2D, textures.get(backgroundId).getGlId());
        gl.glColor4f(todEngineColorFinal.getR(), todEngineColorFinal.getG(), todEngineColorFinal.getB(), 1.0f);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 250.0f, 35.0f);
        gl.glScalef(this.BG_PADDING * 2.0f, this.BG_PADDING, this.BG_PADDING);
        gl.glMatrixMode(5890);
        gl.glPushMatrix();
        gl.glTranslatef(((pref_windSpeed * timeDelta) * -0.005f) % 1.0f, 0.0f, 0.0f);
        Model mesh = models.get(R.raw.plane_16x16);
        mesh.render();
        renderStars(gl, timeDelta);
        gl.glPopMatrix();
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPopMatrix();
    }

    private void renderStars(GL10 gl, float timeDelta) {
        if (pref_useTimeOfDay && todSunPosition <= 0.0f) {
            gl.glColor4f(1.0f, 1.0f, 1.0f, todSunPosition * -2.0f);
            gl.glBlendFunc(770, 1);
            Model starMesh = models.get(R.raw.stars);
            Texture noise = textures.get(R.drawable.noise);
            Texture star = textures.get(R.drawable.stars);
            gl.glTranslatef((0.1f * timeDelta) % 1.0f, 300.0f, -100.0f);
            if (gl instanceof GL11) {
                starMesh.renderFrameMultiTexture(noise, star, GL_MODULATE, false);
                return;
            }
            gl.glBindTexture(GL_TEXTURE0, star.getGlId());
            starMesh.render();
        }
    }
}
