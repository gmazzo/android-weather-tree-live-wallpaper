package io.github.gmazzo.android.livewallpaper.weather;

import android.content.Context;
import android.content.SharedPreferences;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor;
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand;
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime;
import io.github.gmazzo.android.livewallpaper.weather.engine.ThingManager;
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector;
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Model;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Texture;

import static javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;
import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;

public class SceneRain extends SceneBase {
    private static final int CLOUD_MODELS[] = {
            R.raw.cloud1m, R.raw.cloud2m, R.raw.cloud3m,
            R.raw.cloud4m, R.raw.cloud5m};
    private static final int CLOUD_TEXTURES[] = {
            R.drawable.clouddark1, R.drawable.clouddark2, R.drawable.clouddark3,
            R.drawable.clouddark4, R.drawable.clouddark5};
    private final String TAG;
    float[] light_diffuse;
    ParticleRain particleRain;
    Vector particleRainOrigin;
    int rainDensity;
    EngineColor v_light_diffuse;

    public SceneRain(Context context, GL11 gl) {
        super(context, gl);
        this.TAG = "Rain";
        this.rainDensity = 10;
        this.mThingManager = new ThingManager();
        todEngineColorFinal = new EngineColor();
        this.pref_todEngineColors = new EngineColor[4];
        this.pref_todEngineColors[0] = new EngineColor();
        this.pref_todEngineColors[1] = new EngineColor();
        this.pref_todEngineColors[2] = new EngineColor();
        this.pref_todEngineColors[3] = new EngineColor();
        this.reloadAssets = false;
        this.pref_numClouds = 20;
        this.pref_numWisps = 6;
        this.v_light_diffuse = new EngineColor(0.5f, 0.5f, 0.5f, 1.0f);
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
                cloud.model = models.get(CLOUD_MODELS[which - 1]);
                cloud.texture = textures.get(CLOUD_TEXTURES[which - 1]);
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
        this.reloadAssets = true;
    }

    private void todFromPrefs(SharedPreferences prefs) {
        pref_useTimeOfDay = prefs.getBoolean(WallpaperSettings.PREF_USE_TOD, false);
        this.pref_todEngineColors[0].set("0.25 0.2 0.2 1", 0.0f, 1.0f);
        this.pref_todEngineColors[1].set("0.6 0.6 0.6 1", 0.0f, 1.0f);
        this.pref_todEngineColors[2].set("0.9 0.9 0.9 1", 0.0f, 1.0f);
        this.pref_todEngineColors[3].set("0.65 0.6 0.6 1", 0.0f, 1.0f);
    }

    public void backgroundFromPrefs(SharedPreferences prefs) {
    }

    private void rainDensityFromPrefs(SharedPreferences prefs) {
        this.rainDensity = prefs.getInt(WallpaperSettings.PREF_RAIN_DENSITY, 10);
    }

    public void precacheAssets(GL10 gl10) {
        textures.get(R.drawable.storm_bg);
        textures.get(R.drawable.trees_overlay);
        textures.get(R.drawable.clouddark1);
        textures.get(R.drawable.clouddark2);
        textures.get(R.drawable.clouddark3);
        textures.get(R.drawable.clouddark4);
        textures.get(R.drawable.clouddark5);
        textures.get(R.drawable.raindrop);
        models.get(R.raw.plane_16x16);
        models.get(R.raw.cloud1m);
        models.get(R.raw.cloud2m);
        models.get(R.raw.cloud3m);
        models.get(R.raw.cloud4m);
        models.get(R.raw.cloud5m);
        models.get(R.raw.grass_overlay);
        models.get(R.raw.trees_overlay);
        models.get(R.raw.trees_overlay_terrain);
    }

    public void updateTimeOfDay(TimeOfDay tod) {
        if (pref_useTimeOfDay) {
            int iMain = tod.getMainIndex();
            int iBlend = tod.getBlendIndex();
            this.v_light_diffuse.blend(this.pref_todEngineColors[iMain], this.pref_todEngineColors[iBlend], tod.getBlendAmount());
        }
    }

    private void renderBackground(GL10 gl, float timeDelta) {
        Texture storm_bg = textures.get(R.drawable.storm_bg);
        gl.glBindTexture(GL_TEXTURE_2D, storm_bg.getGlId());
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
        this.particleRain.render((GL11) gl, this.particleRainOrigin);
        gl.glPopMatrix();
    }

    public void draw(GL10 gl, GlobalTime time) {
        checkAssetReload(gl);
        this.mThingManager.update(time.sTimeDelta);
        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_COLOR_BUFFER_BIT);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glBlendFunc(1, 771);
        this.light_diffuse[0] = this.v_light_diffuse.getR();
        this.light_diffuse[1] = this.v_light_diffuse.getG();
        this.light_diffuse[2] = this.v_light_diffuse.getB();
        this.light_diffuse[3] = this.v_light_diffuse.getA();
        gl.glLightfv(GL_COLOR_BUFFER_BIT, 4609, this.light_diffuse, 0);
        gl.glLightfv(GL_COLOR_BUFFER_BIT, 4608, this.light_diffuse, 0);
        renderBackground(gl, time.sTimeElapsed);
        renderRain(gl, time.sTimeDelta);
        gl.glTranslatef(0.0f, 0.0f, 40.0f);
        this.mThingManager.render(gl, this.textures, this.models);
        gl.glDisable(GL_COLOR_BUFFER_BIT);
        gl.glDisable(GL_LIGHTING);
        drawTree(gl, time.sTimeDelta);
    }
}