package io.github.gmazzo.android.livewallpaper.weather.engine.things;

import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

import io.github.gmazzo.android.livewallpaper.weather.R;
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor;
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneBase;
import io.github.gmazzo.android.livewallpaper.weather.sky_manager.SkyManager;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures;

public class ThingMoon extends Thing {
    private static final String TAG = "Moon";
    private static final int[] PHASES = {
            R.drawable.moon_0, R.drawable.moon_1, R.drawable.moon_2, R.drawable.moon_3,
            R.drawable.moon_4, R.drawable.moon_5, R.drawable.moon_6, R.drawable.moon_7,
            R.drawable.moon_8, R.drawable.moon_9, R.drawable.moon_10, R.drawable.moon_11};
    private int mOldPhase;
    private int mPhase;

    public ThingMoon() {
        this.mPhase = 0;
        this.mOldPhase = 6;
        this.texture = null;
        this.engineColor = new EngineColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void render(GL10 gl, Textures textures, Models models) {
        if (model == null) {
            model = models.get(R.raw.plane_16x16);
        }
        if (this.texture == null || this.mPhase != this.mOldPhase) {
            this.texture = textures.get(PHASES[mPhase]);
            this.mOldPhase = this.mPhase;
        }
        gl.glBlendFunc(770, 771);
        super.render(gl, textures, models);
    }

    public void update(float timeDelta) {
        super.update(timeDelta);
        float position = SceneBase.todSunPosition;
        if (position >= 0.0f) {
            this.scale.set(0.0f);
        } else {
            this.scale.set(2.0f);
            float altitude = position * -175.0f;
            float alpha = altitude / 25.0f;
            if (alpha > 1.0f) {
                alpha = 1.0f;
            }
            this.engineColor.setA(alpha);
            this.origin.setZ(altitude - 80.0f);
            if (this.origin.getZ() > 0.0f) {
                this.origin.setZ(0.0f);
            }
        }
        double moon_phase = SkyManager.GetMoonPhase();
        this.mPhase = Math.round(((float) moon_phase) * 12.0f);
        if (this.mPhase > 11) {
            this.mPhase = 0;
        }
        if (this.mPhase != this.mOldPhase) {
            Log.i(TAG, "moon_phase=" + moon_phase + " tex=" + this.mPhase);
        }
    }
}
