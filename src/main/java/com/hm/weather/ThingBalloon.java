package com.hm.weather;

import com.hm.weather.engine.AnimPlayer;
import com.hm.weather.engine.GlobalRand;
import com.hm.weather.engine.GlobalTime;
import com.hm.weather.engine.MeshManager;
import com.hm.weather.engine.TextureManager;
import com.hm.weather.engine.Thing;
import com.hm.weather.engine.Vector3;
import com.hm.weather.engine.Vector4;
import javax.microedition.khronos.opengles.GL10;

public class ThingBalloon extends Thing {
    private boolean active;
    public float goalAltitude;
    private boolean hasReachedGoalAltitude;
    private float phase;
    private float speedMod;

    public ThingBalloon() {
        this.active = true;
        this.goalAltitude = 0.0f;
        this.hasReachedGoalAltitude = false;
        this.phase = 0.0f;
        this.speedMod = 1.0f;
        this.velocity = new Vector3(0.0f, 0.0f, 0.0f);
        this.phase = GlobalRand.floatRange(0.0f, 1.0f);
        this.speedMod = 7.0f;
        this.color = new Vector4(1.0f, 1.0f, 1.0f, 1.0f);
        this.meshName = "balloon";
        this.anim = new AnimPlayer(0, 49, GlobalRand.floatRange(2.0f, 4.0f), true);
        this.targetName = "balloon";
    }

    private void updateArrival(float timeDelta) {
        if (this.origin.z >= this.goalAltitude - 1.0f) {
            this.hasReachedGoalAltitude = true;
            return;
        }
        this.velocity.x = SceneBase.pref_windSpeed * this.speedMod;
        this.velocity.z = (this.goalAltitude - this.origin.z) * 0.5f;
    }

    private void updateIdle(float timeDelta) {
        this.velocity.x = SceneBase.pref_windSpeed * this.speedMod;
        this.velocity.z = GlobalTime.waveSin(0.0f, 2.0f, this.phase, 1.0f);
        float f8 = 45.0f + (0.5f * this.origin.y);
        if (this.origin.x > f8) {
            Vector3 vector3 = this.origin;
            vector3.x -= f8 * 2.0f;
        }
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return this.active;
    }

    public void render(GL10 gl, TextureManager tm, MeshManager mm) {
        gl.glBlendFunc(1, 771);
        super.render(gl, tm, mm);
    }

    public void update(float timeDelta) {
        super.update(timeDelta);
        this.color.x = SceneBase.todColorFinal.x;
        this.color.y = SceneBase.todColorFinal.y;
        this.color.z = SceneBase.todColorFinal.z;
        if (!this.active) {
            Vector3 vector3 = this.velocity;
            vector3.z += 1.0f * timeDelta;
            if (this.origin.z > 65.0f) {
                delete();
            }
        } else if (this.hasReachedGoalAltitude) {
            updateIdle(timeDelta);
        } else {
            updateArrival(timeDelta);
        }
    }
}
