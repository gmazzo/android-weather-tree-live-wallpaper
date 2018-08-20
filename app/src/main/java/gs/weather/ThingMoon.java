package gs.weather;

import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

import gs.weather.engine.MeshManager;
import gs.weather.engine.TextureManager;
import gs.weather.engine.Thing;
import gs.weather.engine.Vector4;
import gs.weather.sky_manager.SkyManager;

public class ThingMoon extends Thing {
    private static final String TAG = "Moon";
    private int mOldPhase;
    private int mPhase;

    public ThingMoon() {
        this.mPhase = 0;
        this.mOldPhase = 6;
        this.texName = "moon_6";
        this.meshName = "plane_16x16";
        this.color = new Vector4(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void render(GL10 gl, TextureManager texturemanager, MeshManager meshmanager) {
        if (this.mPhase != this.mOldPhase) {
            texturemanager.unload(gl, this.texName);
            this.texName = "moon_" + this.mPhase;
            this.mOldPhase = this.mPhase;
        }
        gl.glBlendFunc(770, 771);
        super.render(gl, texturemanager, meshmanager);
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
            this.color.a = alpha;
            this.origin.z = altitude - 80.0f;
            if (this.origin.z > 0.0f) {
                this.origin.z = 0.0f;
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
