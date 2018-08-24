package gs.weather.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.CallSuper;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import gs.weather.sky_manager.TimeOfDay;
import gs.weather.wallpaper.Models;
import gs.weather.wallpaper.Textures;

public abstract class Scene {
    public static Textures sTextures;
    public static Models sModels;
    protected Context mContext;
    protected boolean mLandscape;
    protected Textures textures;
    protected Models models;
    protected MeshManager mMeshManager;
    protected ThingManager mThingManager;

    public abstract void draw(GL10 gl10, GlobalTime globalTime);

    public abstract void load(GL10 gl10);

    public abstract void unload(GL10 gl10);

    @CallSuper
    public void precacheAssets(GL10 gl) {
        if (sTextures == null) {
            sTextures = new Textures(mContext.getResources(), (GL11) gl);
        }
        if (models == null) {
            models = new Models(mContext.getResources(), (GL11) gl, mMeshManager);
        }
        textures = sTextures;
    }

    public void setScreenMode(boolean lanscape) {
        this.mLandscape = lanscape;
    }

    public void updateSharedPrefs(SharedPreferences prefs, String key) {
    }

    public void update(GlobalTime globalTime) {
    }

    public void updateTimeOfDay(TimeOfDay tod) {
    }
}
