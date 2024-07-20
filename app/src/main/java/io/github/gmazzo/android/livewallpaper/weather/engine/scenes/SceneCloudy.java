package io.github.gmazzo.android.livewallpaper.weather.engine.scenes;

import android.content.Context;

import javax.microedition.khronos.opengles.GL11;

import io.github.gmazzo.android.livewallpaper.weather.R;

public class SceneCloudy extends SceneClear {

    public SceneCloudy(Context ctx, GL11 gl) {
        super(ctx, gl, R.drawable.bg1);
    }

}
