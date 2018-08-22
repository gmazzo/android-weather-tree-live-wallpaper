package gs.weather;

import javax.microedition.khronos.opengles.GL10;

import gs.weather.engine.AnimPlayer;
import gs.weather.engine.Color;
import gs.weather.engine.GlobalRand;
import gs.weather.engine.Mesh;
import gs.weather.engine.MeshManager;
import gs.weather.engine.TextureManager;
import gs.weather.engine.Thing;
import gs.weather.engine.Vector;

import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;

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
        this.velocity = new Vector(0.0f, 0.0f, 0.0f);
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.anim = new AnimPlayer(0, 39, GlobalRand.floatRange(2.0f, 4.0f), true);
        this.targetName = "ufo";
        this.loopsRemaining = GlobalRand.intRange(1, 4);
        this.vis_width = 0.0f;
    }

    private void updateArrival(float f) {
        if (this.origin.getZ() <= this.goalAltitude + 1.0f) {
            this.hasReachedGoalAltitude = true;
            return;
        }
        this.velocity.setX(SceneBase.pref_windSpeed * this.speedMod);
        this.velocity.setZ((this.goalAltitude - this.origin.getZ()) * 0.5f);
    }

    private void updateIdle(float f) {
        this.velocity.setX(this.speedMod);
    }

    public void render(GL10 gl, TextureManager texturemanager, MeshManager meshmanager) {
        int ufoTexId = texturemanager.getTextureID(gl, "ufo");
        int glowTexId = texturemanager.getTextureID(gl, "ufo_glow");
        Mesh ufo = meshmanager.getMeshByName(gl, "ufo");
        Mesh ring = meshmanager.getMeshByName(gl, "ufo_ring");
        gl.glEnable(2929);
        gl.glMatrixMode(5888);
        gl.glPushMatrix();
        gl.glTranslatef(this.origin.getX(), this.origin.getY(), this.origin.getZ());
        gl.glScalef(this.scale.getX(), this.scale.getY(), this.scale.getZ());
        gl.glBindTexture(GL_TEXTURE_2D, ufoTexId);
        gl.glBlendFunc(770, 771);
        gl.glColor4f(this.color.getR(), this.color.getG(), this.color.getB(), this.color.getA());
        ring.render(gl);
        gl.glBindTexture(GL_TEXTURE_2D, glowTexId);
        gl.glBlendFunc(1, 1);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        ring.render(gl);
        gl.glRotatef((this.sTimeElapsed * 270.0f) % 360.0f, this.angles.getR(), this.angles.getG(), this.angles.getB());
        gl.glBindTexture(GL_TEXTURE_2D, ufoTexId);
        gl.glBlendFunc(770, 771);
        gl.glColor4f(this.color.getR(), this.color.getG(), this.color.getB(), this.color.getA());
        ufo.render(gl);
        gl.glBindTexture(GL_TEXTURE_2D, glowTexId);
        gl.glBlendFunc(1, 1);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        ufo.render(gl);
        gl.glPopMatrix();
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glDisable(2929);
    }

    public void update(float f) {
        super.update(f);
        this.color.setR(SceneBase.todColorFinal.getR());
        this.color.setG(SceneBase.todColorFinal.getG());
        this.color.setB(SceneBase.todColorFinal.getB());
        float x = 45.0f + (0.5f * this.origin.getY());
        if (this.origin.getX() > x) {
            this.origin.setX(this.origin.getX() - 2.0f * x);
            this.loopsRemaining--;
        }
        if (this.loopsRemaining <= 0) {
            this.active = false;
        }
        if (!this.active) {
            this.velocity.setZ(this.velocity.getZ() + 1.0f * f);
            if (this.origin.getZ() > 65.0f) {
                delete();
            }
        } else if (this.hasReachedGoalAltitude) {
            updateIdle(f);
        } else {
            updateArrival(f);
        }
    }
}
