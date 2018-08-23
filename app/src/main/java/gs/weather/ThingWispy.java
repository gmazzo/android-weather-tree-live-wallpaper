package gs.weather;

import javax.microedition.khronos.opengles.GL10;

import gs.weather.engine.Color;
import gs.weather.engine.Thing;
import gs.weather.engine.Vector;
import gs.weather.wallpaper.Models;
import gs.weather.wallpaper.Textures;

public class ThingWispy extends Thing {
    public int which;

    @Override
    public void render(GL10 gl, Textures textures, Models models) {
        if (model == null) {
            model = models.get("plane_16x16");
            texture = textures.get("wispy" + which);
        }

        Color todColor = SceneBase.todColorFinal;
        gl.glColor4f(todColor.getR(), todColor.getG(), todColor.getB(), (todColor.getR() + todColor.getG()) + (todColor.getB() / 3.0f));
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
