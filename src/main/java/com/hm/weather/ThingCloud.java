package com.hm.weather;

import com.hm.weather.engine.GlobalRand;
import com.hm.weather.engine.MeshManager;
import com.hm.weather.engine.TextureManager;
import com.hm.weather.engine.Thing;
import com.hm.weather.engine.Vector4;
import javax.microedition.khronos.opengles.GL10;

public class ThingCloud extends Thing {
    static final float CLOUD_FADE_START_X = 25.0f;
    static final float CLOUD_FADE_START_Y = 25.0f;
    static final float CLOUD_RESET_X = 10.0f;
    static final float CLOUD_RESET_Y = 10.0f;
    float fade;

    public ThingCloud() {
        this.color = new Vector4(1.0f, 1.0f, 1.0f, 1.0f);
        this.vis_width = 0.0f;
        this.origin.x = -100.0f;
        this.origin.y = 15.0f;
        this.origin.z = 50.0f;
    }

    private void setFade(float alpha) {
        this.color.multiply(alpha);
        this.color.a = alpha;
    }

    public void randomizeScale() {
        this.scale.set(3.5f + GlobalRand.floatRange(0.0f, 2.0f), 3.0f, 3.5f + GlobalRand.floatRange(0.0f, 2.0f));
    }

    private float calculateCloudRangeX() {
        return ((this.origin.y * IsolatedRenderer.horizontalFOV) / 90.0f) + Math.abs(this.scale.x * 6.0f);
    }

    public void render(GL10 gl10, TextureManager texMagr, MeshManager meshMagr) {
        gl10.glBlendFunc(1, 771);
        super.render(gl10, texMagr, meshMagr);
    }

    public void update(float timeDelta) {
        super.update(timeDelta);
        float rangX = calculateCloudRangeX();
        if (this.origin.x > rangX) {
            this.origin.x = GlobalRand.floatRange((-rangX) - 5.0f, (-rangX) + 5.0f);
            this.fade = 0.0f;
            setFade(this.fade);
            this.sTimeElapsed = 0.0f;
            randomizeScale();
        }
        Vector4 todColors = SceneBase.todColorFinal;
        this.color.x = todColors.x;
        this.color.y = todColors.y;
        this.color.z = todColors.z;
        if (this.sTimeElapsed < 2.0f) {
            setFade(this.sTimeElapsed * 0.5f);
        }
    }
}
