package com.hm.weather;

import com.hm.weather.engine.AnimPlayer;
import com.hm.weather.engine.GlobalRand;
import com.hm.weather.engine.Mesh;
import com.hm.weather.engine.MeshManager;
import com.hm.weather.engine.TextureManager;
import com.hm.weather.engine.Thing;
import com.hm.weather.engine.Vector3;
import com.hm.weather.engine.Vector4;
import javax.microedition.khronos.opengles.GL10;

public class ThingUFO extends Thing {
    private boolean active;
    public float goalAltitude;
    private boolean hasReachedGoalAltitude;
    private int loopsRemaining;
    private float speedMod;

    public ThingUFO() {
        this.active = true;
        this.speedMod = 12.0f;
        this.hasReachedGoalAltitude = false;
        this.goalAltitude = 15.0f;
        this.loopsRemaining = 0;
        this.velocity = new Vector3(0.0f, 0.0f, 0.0f);
        this.color = new Vector4(1.0f, 1.0f, 1.0f, 1.0f);
        this.anim = new AnimPlayer(0, 39, GlobalRand.floatRange(2.0f, 4.0f), true);
        this.targetName = "ufo";
        this.loopsRemaining = GlobalRand.intRange(1, 4);
        this.vis_width = 0.0f;
    }

    private void updateArrival(float f) {
        if (this.origin.z <= this.goalAltitude + 1.0f) {
            this.hasReachedGoalAltitude = true;
            return;
        }
        this.velocity.x = SceneBase.pref_windSpeed * this.speedMod;
        this.velocity.z = (this.goalAltitude - this.origin.z) * 0.5f;
    }

    private void updateIdle(float f) {
        this.velocity.x = this.speedMod;
    }

    public void render(GL10 gl, TextureManager texturemanager, MeshManager meshmanager) {
        int ufoTexId = texturemanager.getTextureID(gl, "ufo");
        int glowTexId = texturemanager.getTextureID(gl, "ufo_glow");
        Mesh ufo = meshmanager.getMeshByName(gl, "ufo");
        Mesh ring = meshmanager.getMeshByName(gl, "ufo_ring");
        gl.glEnable(2929);
        gl.glMatrixMode(5888);
        gl.glPushMatrix();
        gl.glTranslatef(this.origin.x, this.origin.y, this.origin.z);
        gl.glScalef(this.scale.x, this.scale.y, this.scale.z);
        gl.glBindTexture(3553, ufoTexId);
        gl.glBlendFunc(770, 771);
        gl.glColor4f(this.color.x, this.color.y, this.color.z, this.color.a);
        ring.render(gl);
        gl.glBindTexture(3553, glowTexId);
        gl.glBlendFunc(1, 1);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        ring.render(gl);
        gl.glRotatef((this.sTimeElapsed * 270.0f) % 360.0f, this.angles.x, this.angles.y, this.angles.z);
        gl.glBindTexture(3553, ufoTexId);
        gl.glBlendFunc(770, 771);
        gl.glColor4f(this.color.x, this.color.y, this.color.z, this.color.a);
        ufo.render(gl);
        gl.glBindTexture(3553, glowTexId);
        gl.glBlendFunc(1, 1);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        ufo.render(gl);
        gl.glPopMatrix();
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glDisable(2929);
    }

    public void update(float f) {
        super.update(f);
        this.color.x = SceneBase.todColorFinal.x;
        this.color.y = SceneBase.todColorFinal.y;
        this.color.z = SceneBase.todColorFinal.z;
        float x = 45.0f + (0.5f * this.origin.y);
        if (this.origin.x > x) {
            this.origin.x -= 2.0f * x;
            this.loopsRemaining--;
        }
        if (this.loopsRemaining <= 0) {
            this.active = false;
        }
        if (!this.active) {
            this.velocity.z += 1.0f * f;
            if (this.origin.z > 65.0f) {
                delete();
            }
        } else if (this.hasReachedGoalAltitude) {
            updateIdle(f);
        } else {
            updateArrival(f);
        }
    }
}
