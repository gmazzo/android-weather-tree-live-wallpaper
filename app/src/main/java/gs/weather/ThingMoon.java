package gs.weather;

import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

import gs.weather.engine.Color;
import gs.weather.engine.Thing;
import gs.weather.sky_manager.SkyManager;
import gs.weather.wallpaper.Models;
import gs.weather.wallpaper.Textures;

public class ThingMoon extends Thing {
    private static final String TAG = "Moon";
    private static final int[] phases = {
            R.drawable.moon_0, R.drawable.moon_1, R.drawable.moon_2, R.drawable.moon_3,
            R.drawable.moon_4, R.drawable.moon_5, R.drawable.moon_6, R.drawable.moon_7,
            R.drawable.moon_8, R.drawable.moon_9, R.drawable.moon_10, R.drawable.moon_11};
    private int mOldPhase;
    private int mPhase;

    public ThingMoon() {
        this.mPhase = 0;
        this.mOldPhase = 6;
        this.texture = null;
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void render(GL10 gl, Textures textures, Models models) {
        if (model == null) {
            model = models.loadBMDL("plane_16x16", R.raw.plane_16x16);
        }
        if (this.mPhase != this.mOldPhase) {
            if (texture != null) {
                textures.unload(texture);
            }
            this.texture = textures.loadBitmap("moon_" + mPhase, phases[mPhase]);
            this.mOldPhase = this.mPhase;
        }
        gl.glBlendFunc(770, 771);
        super.render(gl, textures, models);
    }

    public void update(float timeDelta) {
        super.update(timeDelta);
        float position = SceneBase.todSunPosition;
        if (position >= 0.0f || !SceneBase.pref_useTimeOfDay) {
            this.scale.set(0.0f);
        } else {
            this.scale.set(2.0f);
            float altitude = position * -175.0f;
            float alpha = altitude / 25.0f;
            if (alpha > 1.0f) {
                alpha = 1.0f;
            }
            this.color.setA(alpha);
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
