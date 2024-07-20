package io.github.gmazzo.android.livewallpaper.weather.engine.things;

import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_MODULATE;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import io.github.gmazzo.android.livewallpaper.weather.R;
import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor;
import io.github.gmazzo.android.livewallpaper.weather.engine.scenes.SceneBase;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Models;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Texture;
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Textures;

public class ThingSun extends Thing {
    private static final String TAG = "Sun";
    private Texture sunBlend;

    public ThingSun() {
        this.engineColor = new EngineColor(1.0f, 1.0f, 0.95f, 1.0f);
    }

    @Override
    public void render(GL10 gl, Textures textures, Models models) {
        if (texture == null) {
            texture = textures.get(R.raw.sun);
            sunBlend = textures.get(R.raw.sun_blend);
            model = models.get(R.raw.plane_16x16);
        }

        gl.glBlendFunc(1, 769);
        gl.glColor4f(this.engineColor.getR(), this.engineColor.getG(), this.engineColor.getB(), this.engineColor.getA());
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef(this.origin.getX(), this.origin.getY(), this.origin.getZ());
        gl.glScalef(this.scale.getX(), this.scale.getY(), this.scale.getZ());
        gl.glRotatef((this.sTimeElapsed * 12.0f) % 360.0f, 0.0f, 1.0f, 0.0f);
        gl.glMatrixMode(5890);
        gl.glPushMatrix();
        float f11 = (this.sTimeElapsed * 18.0f) % 360.0f;
        gl.glTranslatef(0.5f, 0.5f, 0.0f);
        gl.glRotatef(f11, 0.0f, 0.0f, 1.0f);
        gl.glTranslatef(-0.5f, -0.5f, 0.0f);
        if (gl instanceof GL11) {
            model.renderFrameMultiTexture(sunBlend, texture, GL_MODULATE, false);
        } else {
            gl.glBindTexture(GL_TEXTURE0, texture.getGlId());
            model.render();
        }
        gl.glPopMatrix();
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPopMatrix();
    }

    public void update(float timeDelta) {
        super.update(timeDelta);
        float sunPos = SceneBase.todSunPosition;
        float alpha = 0.0f;
        if (sunPos > 0.0f) {
            this.scale.set(2.0f);
            float altitude = 175.0f * sunPos;
            alpha = altitude / 25.0f;
            if (alpha > 1.0f) {
                alpha = 1.0f;
            }
            this.origin.setZ(altitude - 50.0f);
            if (this.origin.getZ() > 40.0f) {
                this.origin.setZ(40.0f);
            }
        } else {
            this.scale.set(0.0f);
        }
        this.engineColor.set(SceneBase.todEngineColorFinal);
        this.engineColor.times(alpha);
    }
}
