package io.github.gmazzo.android.livewallpaper.weather.engine.particles;

import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import io.github.gmazzo.android.livewallpaper.weather.IsolatedRenderer;
import io.github.gmazzo.android.livewallpaper.weather.R;
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand;
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector;
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.Scene;
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneBase;
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneSnow;

public class ParticleSnow extends ParticleSystem {
    public ParticleSnow() {
        this.spawnRate = 0.25f;
        this.spawnRateVariance = 0.05f;
        this.startEngineColor.set(1.0f, 1.0f, 1.0f, 3.0f);
        this.destEngineColor.set(1.0f, 1.0f, 1.0f, 0.0f);
        this.spawnRangeX = 20.0f;
    }

    @Override
    public void render(GL11 gl, Vector systemOrigin) {
        if (model == null) {
            model = Scene.sModels.get(R.raw.flakes);
            texture = Scene.sTextures.get(R.raw.p_snow1);
        }
        super.render(gl, systemOrigin);
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
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void update(float timeDelta) {
        super.update(timeDelta);
        this.startEngineColor.set(SceneBase.todEngineColorFinal.getR(), SceneBase.todEngineColorFinal.getG(), SceneBase.todEngineColorFinal.getB(), 3.0f);
        this.destEngineColor.set(SceneBase.todEngineColorFinal.getR(), SceneBase.todEngineColorFinal.getG(), SceneBase.todEngineColorFinal.getB(), 0.0f);
    }
}
