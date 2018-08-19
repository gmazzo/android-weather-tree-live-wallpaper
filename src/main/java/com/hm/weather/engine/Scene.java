package com.hm.weather.engine;

import android.content.Context;
import android.content.SharedPreferences;
import com.hm.weather.sky_manager.TimeOfDay;
import javax.microedition.khronos.opengles.GL10;

public abstract class Scene {
    protected Context mContext;
    protected boolean mLandscape;
    protected MeshManager mMeshManager;
    protected TextureManager mTextureManager;
    protected ThingManager mThingManager;

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
