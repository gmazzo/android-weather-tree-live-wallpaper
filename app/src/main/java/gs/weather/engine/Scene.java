package gs.weather.engine;

import android.content.Context;
import android.content.SharedPreferences;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import gs.weather.sky_manager.TimeOfDay;
import gs.weather.wallpaper.Models;
import gs.weather.wallpaper.Textures;

public abstract class Scene {
    public static Textures sTextures;
    public static Models sModels;
    protected final Context mContext;
    protected boolean mLandscape;
    protected final Textures textures;
    protected final Models models;
    protected ThingManager mThingManager;

    protected Scene(Context context, GL11 gl) {
        this.mContext = context;
        this.textures = new Textures(mContext.getResources(), gl);
        this.models = new Models(mContext.getResources(), gl);
    }

    public abstract void draw(GL10 gl10, GlobalTime globalTime);

    public abstract void load(GL10 gl10);

    public abstract void unload(GL10 gl10);

    public void precacheAssets(GL10 gl) {
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
