package com.hm.weather;

import com.hm.weather.engine.GlobalRand;
import com.hm.weather.engine.Mesh;
import com.hm.weather.engine.MeshManager;
import com.hm.weather.engine.TextureManager;
import com.hm.weather.engine.Thing;
import com.hm.weather.engine.Vector4;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class ThingDarkCloud extends Thing {
    static Vector4 pref_boltColor = new Vector4(1.0f, 1.0f, 1.0f, 1.0f);
    public static boolean pref_minimalist = false;
    private float flashIntensity;
    public String texNameFlare;
    private boolean withFlare;

    public ThingDarkCloud(boolean flare) {
        this.withFlare = false;
        this.color = new Vector4(1.0f, 1.0f, 1.0f, 1.0f);
        this.withFlare = flare;
        this.flashIntensity = 0.0f;
        this.texNameFlare = "";
    }

    private void setFade(float alpha) {
        this.color.multiply(alpha);
        this.color.a = alpha;
    }

    private float calculateCloudRangeX() {
        return ((this.origin.y * IsolatedRenderer.horizontalFOV) / 90.0f) + Math.abs(this.scale.x * 1.0f);
    }

    public float randomWithinRangeX() {
        float x = calculateCloudRangeX();
        return GlobalRand.floatRange(-x, x);
    }

    public void randomizeScale() {
        this.scale.set(3.5f + GlobalRand.floatRange(0.0f, 2.0f), 3.0f, 3.5f + GlobalRand.floatRange(0.0f, 2.0f));
    }

    public void render(GL10 gl, TextureManager tm, MeshManager mm) {
        if (this.particleSystem != null) {
            this.particleSystem.render((GL11) gl, tm, mm, this.origin);
        }
        if (this.texName != null && this.meshName != null) {
            tm.bindTextureID(gl, this.texName);
            Mesh mesh = mm.getMeshByName(gl, this.meshName);
            gl.glBlendFunc(1, 771);
            gl.glPushMatrix();
            gl.glTranslatef(this.origin.x, this.origin.y, this.origin.z);
            gl.glScalef(this.scale.x, this.scale.y, this.scale.z);
            gl.glRotatef(this.angles.a, this.angles.x, this.angles.y, this.angles.z);
            if (!pref_minimalist) {
                mesh.render(gl);
            }
            if (this.withFlare && this.flashIntensity > 0.0f) {
                gl.glDisable(2896);
                tm.bindTextureID(gl, this.texNameFlare);
                gl.glColor4f(pref_boltColor.x, pref_boltColor.y, pref_boltColor.z, this.flashIntensity);
                gl.glBlendFunc(770, 1);
                mesh.render(gl);
                gl.glEnable(2896);
            }
            gl.glPopMatrix();
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    public void update(float timeDelta) {
        super.update(timeDelta);
        float rangX = calculateCloudRangeX();
        if (this.origin.x > rangX) {
            this.origin.x = -rangX;
        } else if (this.origin.x < (-rangX)) {
            this.origin.x = rangX;
        }
        Vector4 todColors = SceneBase.todColorFinal;
        this.color.x = 0.2f;
        this.color.y = 0.2f;
        this.color.z = 0.2f;
        if (this.sTimeElapsed < 2.0f) {
            setFade(this.sTimeElapsed * 0.5f);
        }
        if (this.withFlare) {
            if (this.flashIntensity > 0.0f) {
                this.flashIntensity -= 1.25f * timeDelta;
            }
            if (this.flashIntensity <= 0.0f && GlobalRand.floatRange(0.0f, 4.5f) < timeDelta) {
                this.flashIntensity = 0.5f;
            }
        }
    }
}
