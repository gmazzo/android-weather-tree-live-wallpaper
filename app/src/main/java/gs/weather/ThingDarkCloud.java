package gs.weather;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import gs.weather.engine.Color;
import gs.weather.engine.GlobalRand;
import gs.weather.engine.Thing;
import gs.weather.wallpaper.Models;
import gs.weather.wallpaper.Textures;

import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;

public class ThingDarkCloud extends Thing {
    static Color pref_boltColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    public static boolean pref_minimalist = false;
    private float flashIntensity;
    public String texNameFlare;
    private boolean withFlare;

    public ThingDarkCloud(boolean flare) {
        this.withFlare = false;
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.withFlare = flare;
        this.flashIntensity = 0.0f;
        this.texNameFlare = "";
    }

    private void setFade(float alpha) {
        this.color.times(alpha);
        this.color.setA(alpha);
    }

    private float calculateCloudRangeX() {
        return ((this.origin.getY() * IsolatedRenderer.horizontalFOV) / 90.0f) + Math.abs(this.scale.getX() * 1.0f);
    }

    public float randomWithinRangeX() {
        float x = calculateCloudRangeX();
        return GlobalRand.floatRange(-x, x);
    }

    public void randomizeScale() {
        this.scale.set(3.5f + GlobalRand.floatRange(0.0f, 2.0f), 3.0f, 3.5f + GlobalRand.floatRange(0.0f, 2.0f));
    }

    @Override
    public void render(GL10 gl, Textures textures, Models models) {
        if (this.particleSystem != null) {
            this.particleSystem.render((GL11) gl, textures.getManager(), models.getManager(), this.origin);
        }
        if (this.texture != null && this.model != null) {
            textures.getManager().bindTextureID(gl, this.texture.getName());

            gl.glBlendFunc(1, 771);
            gl.glPushMatrix();
            gl.glTranslatef(this.origin.getX(), this.origin.getY(), this.origin.getZ());
            gl.glScalef(this.scale.getX(), this.scale.getY(), this.scale.getZ());
            gl.glRotatef(this.angles.getA(), this.angles.getR(), this.angles.getG(), this.angles.getB());
            if (!pref_minimalist) {
                model.render();
            }
            if (this.withFlare && this.flashIntensity > 0.0f) {
                gl.glDisable(GL_LIGHTING);
                textures.getManager().bindTextureID(gl, this.texNameFlare);
                gl.glColor4f(pref_boltColor.getR(), pref_boltColor.getG(), pref_boltColor.getB(), this.flashIntensity);
                gl.glBlendFunc(770, 1);
                model.render();
                gl.glEnable(GL_LIGHTING);
            }
            gl.glPopMatrix();
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    public void update(float timeDelta) {
        super.update(timeDelta);
        float rangX = calculateCloudRangeX();
        if (this.origin.getX() > rangX) {
            this.origin.setX(-rangX);
        } else if (this.origin.getX() < (-rangX)) {
            this.origin.setX(rangX);
        }
        Color todColors = SceneBase.todColorFinal;
        this.color.setR(0.2f);
        this.color.setG(0.2f);
        this.color.setB(0.2f);
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
