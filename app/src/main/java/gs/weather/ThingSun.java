package gs.weather;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import gs.weather.engine.Color;
import gs.weather.engine.Thing;
import gs.weather.wallpaper.Models;
import gs.weather.wallpaper.Texture;
import gs.weather.wallpaper.Textures;

import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_MODULATE;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;

public class ThingSun extends Thing {
    private static final String TAG = "Sun";
    private Texture sunBlend;

    public ThingSun() {
        this.color = new Color(1.0f, 1.0f, 0.95f, 1.0f);
    }

    @Override
    public void render(GL10 gl, Textures textures, Models models) {
        if (texture == null) {
            texture = textures.get("sun");
            sunBlend = textures.get("sun_blend");
            model = models.get("plane_16x16");
        }

        gl.glBlendFunc(1, 769);
        gl.glColor4f(this.color.getR(), this.color.getG(), this.color.getB(), this.color.getA());
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
            gl.glBindTexture(GL_TEXTURE0, texture.getId());
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
        this.color.set(SceneBase.todColorFinal);
        this.color.times(alpha);
    }
}
