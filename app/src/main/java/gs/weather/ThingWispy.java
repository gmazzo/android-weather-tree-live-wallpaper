package gs.weather;

import javax.microedition.khronos.opengles.GL10;

import gs.weather.engine.MeshManager;
import gs.weather.engine.TextureManager;
import gs.weather.engine.Thing;
import gs.weather.engine.Vector;
import gs.weather.engine.Color;

public class ThingWispy extends Thing {
    public void render(GL10 gl10, TextureManager texturemanager, MeshManager meshmanager) {
        Color todColor = SceneBase.todColorFinal;
        gl10.glColor4f(todColor.getR(), todColor.getG(), todColor.getB(), (todColor.getR() + todColor.getG()) + (todColor.getB() / 3.0f));
        gl10.glBlendFunc(770, 771);
        super.render(gl10, texturemanager, meshmanager);
    }

    public void update(float f) {
        super.update(f);
        if (this.origin.getX() > 123.75f) {
            Vector vector = this.origin;
            vector.setX(vector.getX() - 247.5f);
        }
    }
}
