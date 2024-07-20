package io.github.gmazzo.android.livewallpaper.weather.engine.things;

import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneClear.CLOUD_X_RANGE;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import io.github.gmazzo.android.livewallpaper.weather.R;
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor;
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand;
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneBase;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Texture;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures;

public class ThingDarkCloud extends Thing {
    static EngineColor pref_boltEngineColor = new EngineColor(1.0f, 1.0f, 1.0f, 1.0f);
    public static boolean pref_minimalist = false;
    private static final int MODELS[] = {
            R.raw.cloud1m, R.raw.cloud2m, R.raw.cloud3m,
            R.raw.cloud4m, R.raw.cloud5m};
    private static final int TEXTURES[] = {
            R.drawable.clouddark1, R.drawable.clouddark2, R.drawable.clouddark3,
            R.drawable.clouddark4, R.drawable.clouddark5};
    private static final int FLARES[] = {
            R.drawable.cloudflare1, R.drawable.cloudflare2, R.drawable.cloudflare3,
            R.drawable.cloudflare4, R.drawable.cloudflare5};
    public int which;
    private float flashIntensity;
    public Texture texNameFlare;
    private boolean withFlare;

    public ThingDarkCloud(boolean flare) {
        this.withFlare = false;
        this.engineColor = new EngineColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.withFlare = flare;
        this.flashIntensity = 0.0f;
    }

    private void setFade(float alpha) {
        this.engineColor.times(alpha);
        this.engineColor.setA(alpha);
    }

    private float calculateCloudRangeX() {
        return ((this.origin.getY() * CLOUD_X_RANGE) / 90.0f) + Math.abs(this.scale.getX() * 1.0f);
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
        if (model == null) {
            model = models.get(MODELS[which - 1]);
            texture = textures.get(TEXTURES[which - 1]);
            texNameFlare = textures.get(FLARES[which - 1]);
        }
        if (this.particleSystem != null) {
            this.particleSystem.render((GL11) gl, this.origin);
        }
        if (this.texture != null && this.model != null) {
            gl.glBindTexture(GL_TEXTURE_2D, texture.getGlId());

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
                gl.glBindTexture(GL_TEXTURE_2D, texNameFlare.getGlId());
                gl.glColor4f(pref_boltEngineColor.getR(), pref_boltEngineColor.getG(), pref_boltEngineColor.getB(), this.flashIntensity);
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
        EngineColor todColors = SceneBase.todEngineColorFinal;
        this.engineColor.setR(0.2f);
        this.engineColor.setG(0.2f);
        this.engineColor.setB(0.2f);
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
