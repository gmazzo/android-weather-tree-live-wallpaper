package io.github.gmazzo.android.livewallpaper.weather.engine.things;

import static javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;
import static javax.microedition.khronos.opengles.GL10.GL_ONE;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;

import javax.microedition.khronos.opengles.GL10;

import io.github.gmazzo.android.livewallpaper.weather.R;
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor;
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Texture;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures;

public class ThingLightning extends Thing {
    private static final int MODELS[] = {
            R.raw.lightning1, R.raw.lightning2, R.raw.lightning3
    };
    private Texture lightningGlow;
    private Texture lightningCore;

    public ThingLightning(float r, float g, float b) {
        this.engineColor = new EngineColor(r, g, b, 1.0f);
    }

    @Override
    public void render(GL10 gl, Textures textures, Models models) {
        if (model == null) {
            int number = GlobalRand.intRange(1, 4);
            model = models.get(MODELS[number - 1]);
            lightningGlow = textures.get(R.raw.lightning_pieces_glow);
            lightningCore = textures.get(R.raw.lightning_pieces_core);
        }

        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_COLOR_BUFFER_BIT);

        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        gl.glPushMatrix();
        gl.glTranslatef(this.origin.getX(), this.origin.getY(), this.origin.getZ());
        gl.glScalef(this.scale.getX(), this.scale.getX(), this.scale.getX());
        gl.glRotatef(this.angles.getA(), this.angles.getR(), this.angles.getG(), this.angles.getB());
        if (this.engineColor != null) {
            gl.glColor4f(this.engineColor.getR(), this.engineColor.getG(), this.engineColor.getB(), this.engineColor.getA());
        }
        model.renderFrameMultiTexture(lightningGlow, lightningCore, 260, false);
        gl.glPopMatrix();
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glDisable(GL_COLOR_BUFFER_BIT);
        gl.glDisable(GL_LIGHTING);
    }

    public void update(float timeDelta) {
        super.update(timeDelta);
        this.engineColor.setA(this.engineColor.getA() - 2.0f * timeDelta);
        if (this.engineColor.getA() <= 0.0f) {
            delete();
        }
    }
}
