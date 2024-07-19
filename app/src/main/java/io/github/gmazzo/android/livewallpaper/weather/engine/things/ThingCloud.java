package io.github.gmazzo.android.livewallpaper.weather.engine.things;

import static io.github.gmazzo.android.livewallpaper.weather.SceneClear.CLOUD_X_RANGE;

import javax.microedition.khronos.opengles.GL10;

import io.github.gmazzo.android.livewallpaper.weather.R;
import io.github.gmazzo.android.livewallpaper.weather.SceneBase;
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor;
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures;

public class ThingCloud extends Thing {
    private static final int MODELS[] = {
            R.raw.cloud1m, R.raw.cloud2m, R.raw.cloud3m,
            R.raw.cloud4m, R.raw.cloud5m};
    private static final int TEXTURES[] = {
            R.drawable.cloud1, R.drawable.cloud2, R.drawable.cloud3,
            R.drawable.cloud4, R.drawable.cloud5};
    static final float CLOUD_FADE_START_X = 25.0f;
    static final float CLOUD_FADE_START_Y = 25.0f;
    static final float CLOUD_RESET_X = 10.0f;
    static final float CLOUD_RESET_Y = 10.0f;
    public int which;
    float fade;

    public ThingCloud() {
        this.engineColor = new EngineColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.vis_width = 0.0f;
        this.origin.setX(-100.0f);
        this.origin.setY(15.0f);
        this.origin.setZ(50.0f);
    }

    private void setFade(float alpha) {
        this.engineColor.times(alpha);
        this.engineColor.setA(alpha);
    }

    public void randomizeScale() {
        this.scale.set(3.5f + GlobalRand.floatRange(0.0f, 2.0f), 3.0f, 3.5f + GlobalRand.floatRange(0.0f, 2.0f));
    }

    private float calculateCloudRangeX() {
        return ((this.origin.getY() * CLOUD_X_RANGE) / 90.0f) + Math.abs(this.scale.getX() * 6.0f);
    }

    @Override
    public void render(GL10 gl, Textures textures, Models models) {
        if (model == null) {
            model = models.get(MODELS[which - 1]);
            texture = textures.get(TEXTURES[which - 1]);
        }
        gl.glBlendFunc(1, 771);
        super.render(gl, textures, models);
    }

    public void update(float timeDelta) {
        super.update(timeDelta);
        float rangX = calculateCloudRangeX();
        if (this.origin.getX() > rangX) {
            this.origin.setX(GlobalRand.floatRange((-rangX) - 5.0f, (-rangX) + 5.0f));
            this.fade = 0.0f;
            setFade(this.fade);
            this.sTimeElapsed = 0.0f;
            randomizeScale();
        }
        EngineColor todColors = SceneBase.todEngineColorFinal;
        this.engineColor.setR(todColors.getR());
        this.engineColor.setG(todColors.getG());
        this.engineColor.setB(todColors.getB());
        if (this.sTimeElapsed < 2.0f) {
            setFade(this.sTimeElapsed * 0.5f);
        }
    }
}
