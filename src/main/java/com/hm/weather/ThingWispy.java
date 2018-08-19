package com.hm.weather;

import com.hm.weather.engine.MeshManager;
import com.hm.weather.engine.TextureManager;
import com.hm.weather.engine.Thing;
import com.hm.weather.engine.Vector3;
import com.hm.weather.engine.Vector4;
import javax.microedition.khronos.opengles.GL10;

public class ThingWispy extends Thing {
    public void render(GL10 gl10, TextureManager texturemanager, MeshManager meshmanager) {
        Vector4 todColor = SceneBase.todColorFinal;
        gl10.glColor4f(todColor.x, todColor.y, todColor.z, (todColor.x + todColor.y) + (todColor.z / 3.0f));
        gl10.glBlendFunc(770, 771);
        super.render(gl10, texturemanager, meshmanager);
    }

    public void update(float f) {
        super.update(f);
        if (this.origin.x > 123.75f) {
            Vector3 vector3 = this.origin;
            vector3.x -= 247.5f;
        }
    }
}
