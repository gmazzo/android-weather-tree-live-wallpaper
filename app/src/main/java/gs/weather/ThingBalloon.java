package gs.weather;

import javax.microedition.khronos.opengles.GL10;

import gs.weather.engine.AnimPlayer;
import gs.weather.engine.GlobalRand;
import gs.weather.engine.GlobalTime;
import gs.weather.engine.MeshManager;
import gs.weather.engine.TextureManager;
import gs.weather.engine.Thing;
import gs.weather.engine.Vector;
import gs.weather.engine.Color;

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
        this.velocity = new Vector(0.0f, 0.0f, 0.0f);
        this.phase = GlobalRand.floatRange(0.0f, 1.0f);
        this.speedMod = 7.0f;
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.meshName = "balloon";
        this.anim = new AnimPlayer(0, 49, GlobalRand.floatRange(2.0f, 4.0f), true);
        this.targetName = "balloon";
    }

    private void updateArrival(float timeDelta) {
        if (this.origin.getZ() >= this.goalAltitude - 1.0f) {
            this.hasReachedGoalAltitude = true;
            return;
        }
        this.velocity.setX(SceneBase.pref_windSpeed * this.speedMod);
        this.velocity.setZ((this.goalAltitude - this.origin.getZ()) * 0.5f);
    }

    private void updateIdle(float timeDelta) {
        this.velocity.setX(SceneBase.pref_windSpeed * this.speedMod);
        this.velocity.setZ(GlobalTime.waveSin(0.0f, 2.0f, this.phase, 1.0f));
        float f8 = 45.0f + (0.5f * this.origin.getY());
        if (this.origin.getX() > f8) {
            Vector vector = this.origin;
            vector.setX(vector.getX() - f8 * 2.0f);
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
        this.color.setR(SceneBase.todColorFinal.getR());
        this.color.setG(SceneBase.todColorFinal.getG());
        this.color.setB(SceneBase.todColorFinal.getB());
        if (!this.active) {
            Vector vector = this.velocity;
            vector.setZ(vector.getZ() + 1.0f * timeDelta);
            if (this.origin.getZ() > 65.0f) {
                delete();
            }
        } else if (this.hasReachedGoalAltitude) {
            updateIdle(timeDelta);
        } else {
            updateArrival(timeDelta);
        }
    }
}
