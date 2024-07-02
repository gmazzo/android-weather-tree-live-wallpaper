package io.github.gmazzo.android.livewallpaper.weather;

import javax.microedition.khronos.opengles.GL10;

import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor;
import io.github.gmazzo.android.livewallpaper.weather.engine.Thing;
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures;

public class ThingWispy extends Thing {
    private static final int WISPY_TEXTURES[] = {
            R.raw.wispy1, R.raw.wispy2, R.raw.wispy3};
    public int which;

    @Override
    public void render(GL10 gl, Textures textures, Models models) {
        if (model == null) {
            model = models.get(R.raw.plane_16x16);
            texture = textures.get(WISPY_TEXTURES[which - 1]);
        }

        EngineColor todEngineColor = SceneBase.todEngineColorFinal;
        gl.glColor4f(todEngineColor.getR(), todEngineColor.getG(), todEngineColor.getB(), (todEngineColor.getR() + todEngineColor.getG()) + (todEngineColor.getB() / 3.0f));
        gl.glBlendFunc(770, 771);
        super.render(gl, textures, models);
    }

    public void update(float f) {
        super.update(f);
        if (this.origin.getX() > 123.75f) {
            Vector vector = this.origin;
            vector.setX(vector.getX() - 247.5f);
        }
    }
}
