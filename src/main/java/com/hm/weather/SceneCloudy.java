package com.hm.weather;

import android.content.Context;
import android.content.SharedPreferences;

public class SceneCloudy extends SceneClear {
    private final String TAG;

    public SceneCloudy(Context ctx) {
        super(ctx);
        this.TAG = "Cloudy";
        this.pref_background = "bg1";
    }

    public void backgroundFromPrefs(SharedPreferences prefs) {
        String bg = "bg1";
        if (!bg.equals(this.pref_background)) {
            this.pref_background = bg;
            this.reloadAssets = true;
        }
    }
}
