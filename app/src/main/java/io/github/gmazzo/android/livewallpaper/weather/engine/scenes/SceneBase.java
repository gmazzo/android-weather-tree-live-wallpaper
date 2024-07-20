package io.github.gmazzo.android.livewallpaper.weather.engine.scenes;

import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import io.github.gmazzo.android.livewallpaper.weather.R;
import io.github.gmazzo.android.livewallpaper.weather.WeatherType;
import io.github.gmazzo.android.livewallpaper.weather.engine.AnimPlayer;
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor;
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand;
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalTime;
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.TimeOfDay;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.AnimatedModel;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Model;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Texture;

public abstract class SceneBase extends Scene {
    public static float pref_windSpeed = 3.0f;
    public static EngineColor todEngineColorFinal;
    public static float todSunPosition = 0.0f;
    protected float BG_PADDING = 20.0f;
    protected final boolean DBG = false;
    protected float TREE_ANIMATE_DELAY_MIN = 3.0f;
    protected float TREE_ANIMATE_DELAY_RANGE = 7.0f;
    protected GlobalTime mGlobalTime;
    protected int pref_numClouds;
    protected int pref_numWisps;
    public EngineColor[] pref_todEngineColors;
    protected boolean pref_treeAnim = true;
    protected boolean reloadAssets;
    protected AnimPlayer treesAnim = new AnimPlayer(0, 19, 5.0f, false);
    protected float treesAnimateDelay = 5.0f;

    public SceneBase(Context context, GL11 gl) {
        super(context, gl);
    }

    protected void checkAssetReload(GL10 gl10) {
        if (this.reloadAssets) {
            synchronized (this) {
                this.models.close();
                this.textures.close();
                precacheAssets(gl10);
                this.reloadAssets = false;
            }
        }
    }

    @Override
    public void unload(GL10 gl) {
        this.models.close();
        this.textures.close();
        this.mThingManager.clear();
    }

    public void numCloudsFromPrefs(WeatherType weather) {
        this.pref_numClouds = weather.getClouds();
    }

    public void windSpeedFromPrefs() {
        pref_windSpeed = 3 * 0.5f;
    }

    @Override
    public void update(GlobalTime globalTime) {
        this.mGlobalTime = globalTime;
    }

    @Override
    public void updateTimeOfDay(TimeOfDay tod) {
        int iMain = tod.getMainIndex();
        int iBlend = tod.getBlendIndex();
        todEngineColorFinal.blend(this.pref_todEngineColors[iMain], this.pref_todEngineColors[iBlend], tod.getBlendAmount());
    }

    protected void drawTree(GL10 gl, float timeDelta) {
        if (this.pref_treeAnim && this.treesAnim.getCount() > 0) {
            this.treesAnimateDelay -= timeDelta;
            if (this.treesAnimateDelay <= 0.0f) {
                this.treesAnimateDelay = this.TREE_ANIMATE_DELAY_MIN + (this.TREE_ANIMATE_DELAY_RANGE * GlobalRand.rand.nextFloat());
                this.treesAnim.reset();
            }
        }

        Model tree_terrain = models.get(R.raw.trees_overlay_terrain);
        Texture trees_overlay = textures.get(R.drawable.trees_overlay);
        gl.glBindTexture(GL_TEXTURE_2D, trees_overlay.getGlId());
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

        AnimatedModel grass = (AnimatedModel) models.get(R.raw.grass_overlay);
        AnimatedModel tree = (AnimatedModel) models.get(R.raw.trees_overlay);
        grass.setAnimator(treesAnim);
        tree.setAnimator(treesAnim);
        this.treesAnim.update(timeDelta);
        tree.render();
        grass.render();

        gl.glPopMatrix();
    }
}
