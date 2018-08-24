package gs.weather;

import android.content.SharedPreferences;

import javax.microedition.khronos.opengles.GL10;

import gs.weather.engine.AnimPlayer;
import gs.weather.engine.Color;
import gs.weather.engine.GlobalRand;
import gs.weather.engine.GlobalTime;
import gs.weather.engine.Scene;
import gs.weather.sky_manager.TimeOfDay;
import gs.weather.wallpaper.AnimatedModel;
import gs.weather.wallpaper.Model;

import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;

public abstract class SceneBase extends Scene {
    public static boolean pref_useTimeOfDay;
    public static float pref_windSpeed = 3.0f;
    public static Color todColorFinal;
    public static float todSunPosition = 0.0f;
    protected float BG_PADDING = 20.0f;
    protected final boolean DBG = false;
    protected float TREE_ANIMATE_DELAY_MIN = 3.0f;
    protected float TREE_ANIMATE_DELAY_RANGE = 7.0f;
    protected GlobalTime mGlobalTime;
    protected int pref_numClouds;
    protected int pref_numWisps;
    public Color[] pref_todColors;
    protected boolean pref_treeAnim = true;
    protected boolean reloadAssets;
    protected AnimPlayer treesAnim = new AnimPlayer(0, 19, 5.0f, false);
    protected float treesAnimateDelay = 5.0f;

    protected void checkAssetReload(GL10 gl10) {
        if (this.reloadAssets) {
            synchronized (this) {
                precacheAssets(gl10);
                this.reloadAssets = false;
            }
        }
    }

    public void unload(GL10 gl) {
        this.mThingManager.clear();
    }

    public void numCloudsFromPrefs(SharedPreferences prefs) {
        this.pref_numClouds = prefs.getInt(WallpaperSettings.PREF_NUM_CLOUDS, 10);
    }

    public void windSpeedFromPrefs(SharedPreferences prefs) {
        pref_windSpeed = Float.valueOf(prefs.getString(WallpaperSettings.PREF_WIND_SPEED, "3")) * 0.5f;
    }

    public void update(GlobalTime globalTime) {
        this.mGlobalTime = globalTime;
    }

    public void updateTimeOfDay(TimeOfDay tod) {
        if (pref_useTimeOfDay) {
            int iMain = tod.getMainIndex();
            int iBlend = tod.getBlendIndex();
            todColorFinal.blend(this.pref_todColors[iMain], this.pref_todColors[iBlend], tod.getBlendAmount());
            return;
        }
        todColorFinal.set(1.0f, 1.0f, 1.0f, 1.0f);
    }

    protected void drawTree(GL10 gl, float timeDelta) {
        if (this.pref_treeAnim && this.treesAnim.getCount() > 0) {
            this.treesAnimateDelay -= timeDelta;
            if (this.treesAnimateDelay <= 0.0f) {
                this.treesAnimateDelay = this.TREE_ANIMATE_DELAY_MIN + (this.TREE_ANIMATE_DELAY_RANGE * GlobalRand.rand.nextFloat());
                this.treesAnim.reset();
            }
        }

        Model tree_terrain = models.get("trees_overlay_terrain");
        gl.glBindTexture(GL_TEXTURE_2D, textures.get("trees_overlay").getId());
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPushMatrix();
        if (this.mLandscape) {
            gl.glTranslatef(2.0f, 70.0f, -65.0f);
        } else {
            gl.glTranslatef(-8.0f, 70.0f, -70.0f);
        }
        gl.glScalef(5.0f, 5.0f, 5.0f);
        gl.glBlendFunc(770, 771);
        tree_terrain.render();

        AnimatedModel grass = (AnimatedModel) models.get("grass_overlay");
        AnimatedModel tree = (AnimatedModel) models.get("trees_overlay");
        grass.setAnimator(treesAnim);
        tree.setAnimator(treesAnim);
        this.treesAnim.update(timeDelta);
        tree.render();
        grass.render();

        gl.glPopMatrix();
    }
}
