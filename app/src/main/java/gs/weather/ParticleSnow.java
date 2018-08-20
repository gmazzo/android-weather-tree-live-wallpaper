package gs.weather;

import javax.microedition.khronos.opengles.GL10;

import gs.weather.engine.GlobalRand;
import gs.weather.engine.ParticleSystem;

public class ParticleSnow extends ParticleSystem {
    public ParticleSnow() {
        this.spawnRate = 0.25f;
        this.spawnRateVariance = 0.05f;
        this.meshName = "flakes";
        this.texName = "p_snow1";
        this.startColor.set(1.0f, 1.0f, 1.0f, 3.0f);
        this.destColor.set(1.0f, 1.0f, 1.0f, 0.0f);
        this.spawnRangeX = 20.0f;
    }

    public void particleSetup(Particle particle) {
        super.particleSetup(particle);
        float bias = ((IsolatedRenderer.homeOffsetPercentage * 2.0f) - 1.0f) * 4.0f;
        particle.lifetime = 4.5f;
        particle.startScale.set(GlobalRand.floatRange(0.15f, 0.3f));
        particle.destScale.set(GlobalRand.floatRange(0.15f, 0.3f));
        float randX1 = (GlobalRand.floatRange(-6.0f, 6.0f) * SceneSnow.pref_snowNoise) + bias;
        float randX2 = (GlobalRand.floatRange(-8.0f, 8.0f) * SceneSnow.pref_snowNoise) + bias;
        float randY1 = GlobalRand.floatRange(-2.0f, 2.0f);
        float randY2 = GlobalRand.floatRange(-2.0f, 2.0f);
        float randZ = GlobalRand.floatRange(-(3.0f + (SceneSnow.pref_snowGravity * 1.5f)), -3.0f);
        particle.startVelocity.set(randX1, randY1, randZ);
        particle.destVelocity.set(randX2, randY2, randZ);
    }

    public void renderEnd(GL10 gl) {
    }

    public void renderStart(GL10 gl) {
        gl.glBlendFunc(770, 771);
    }

    public void update(float timeDelta) {
        super.update(timeDelta);
        this.texName = SceneSnow.pref_snowImage;
        this.startColor.set(SceneBase.todColorFinal.x, SceneBase.todColorFinal.y, SceneBase.todColorFinal.z, 3.0f);
        this.destColor.set(SceneBase.todColorFinal.x, SceneBase.todColorFinal.y, SceneBase.todColorFinal.z, 0.0f);
    }
}
