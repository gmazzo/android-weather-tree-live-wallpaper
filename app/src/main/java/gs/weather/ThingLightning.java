package gs.weather;

import javax.microedition.khronos.opengles.GL10;

import gs.weather.engine.Color;
import gs.weather.engine.GlobalRand;
import gs.weather.engine.Thing;
import gs.weather.wallpaper.Models;
import gs.weather.wallpaper.Texture;
import gs.weather.wallpaper.Textures;

import static javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;

public class ThingLightning extends Thing {
    private static final int MODELS[] = {
            R.raw.lightning1, R.raw.lightning2, R.raw.lightning3
    };
    private Texture lightningGlow;
    private Texture lightningCore;

    public ThingLightning(float r, float g, float b) {
        this.color = new Color(r, g, b, 1.0f);
    }

    @Override
    public void render(GL10 gl, Textures textures, Models models) {
        if (model == null) {
            int number = GlobalRand.intRange(1, 4);
            model = models.loadBMDL("lightning" + number, MODELS[number - 1]);
            lightningGlow = textures.loadTGA("lightning_pieces_glow", R.raw.lightning_pieces_glow);
            lightningCore = textures.loadTGA("lightning_pieces_core", R.raw.lightning_pieces_core);
        }

        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_COLOR_BUFFER_BIT);

        gl.glBlendFunc(770, 1);
        gl.glPushMatrix();
        gl.glTranslatef(this.origin.getX(), this.origin.getY(), this.origin.getZ());
        gl.glScalef(this.scale.getX(), this.scale.getX(), this.scale.getX());
        gl.glRotatef(this.angles.getA(), this.angles.getR(), this.angles.getG(), this.angles.getB());
        if (this.color != null) {
            gl.glColor4f(this.color.getR(), this.color.getG(), this.color.getB(), this.color.getA());
        }
        model.renderFrameMultiTexture(lightningGlow, lightningCore, 260, false);
        gl.glPopMatrix();
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glDisable(GL_COLOR_BUFFER_BIT);
        gl.glDisable(GL_LIGHTING);
    }

    public void update(float timeDelta) {
        super.update(timeDelta);
        this.color.setA(this.color.getA() - 2.0f * timeDelta);
        if (this.color.getA() <= 0.0f) {
            delete();
        }
    }
}
