package io.github.gmazzo.android.livewallpaper.weather.engine.particles;

import static javax.microedition.khronos.opengles.GL10.GL_ONE;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import io.github.gmazzo.android.livewallpaper.weather.R;
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand;
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector;
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.Scene;

public class ParticleRain extends ParticleSystem {
    public ParticleRain(int density) {
        this.spawnRate = 1.0f / ((float) density);
        this.spawnRateVariance = 0.05f;
        this.spawnRangeX = 15.0f;
        this.spawnRangeY = 5.0f;
        this.spawnRangeZ = 0.0f;
    }

    @Override
    public void render(GL11 gl, Vector systemOrigin) {
        if (model == null) {
            model = Scene.sModels.get(R.raw.rain);
            texture = Scene.sTextures.get(R.drawable.raindrop);
        }

        super.render(gl, systemOrigin);
    }

    public void particleSetup(Particle particle) {
        super.particleSetup(particle);
        particle.lifetime = 1.0f;
        float startScale = GlobalRand.floatRange(1.0f, 1.5f);
        particle.startScale.set(startScale, startScale, startScale);
        particle.destScale.set(startScale, startScale, startScale);
        float velocity = GlobalRand.floatRange(0.95f, 1.05f);
        particle.startVelocity.set(8.0f, 0.0f, -15.0f);
        particle.destVelocity.set(9.45f * velocity, 0.0f, -35.0f * velocity);
    }

    public void renderEnd(GL10 gl) {
    }

    public void renderStart(GL10 gl) {
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    }
}
