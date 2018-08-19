package com.hm.weather;

import com.hm.weather.engine.GlobalRand;
import com.hm.weather.engine.ParticleSystem;
import com.hm.weather.engine.ParticleSystem.Particle;
import javax.microedition.khronos.opengles.GL10;

public class ParticleRain extends ParticleSystem {
    public ParticleRain(int density) {
        this.spawnRate = 1.0f / ((float) density);
        this.spawnRateVariance = 0.05f;
        this.texName = "raindrop";
        this.meshName = "rain";
        this.spawnRangeX = 15.0f;
        this.spawnRangeY = 5.0f;
        this.spawnRangeZ = 0.0f;
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
        gl.glBlendFunc(1, 771);
    }
}
