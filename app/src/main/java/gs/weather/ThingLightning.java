package gs.weather;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import gs.weather.engine.GlobalRand;
import gs.weather.engine.Mesh;
import gs.weather.engine.MeshManager;
import gs.weather.engine.TextureManager;
import gs.weather.engine.Thing;
import gs.weather.engine.Vector4;

public class ThingLightning extends Thing {
    static final int NUM_LIGHTNING_MODELS = 3;

    public ThingLightning(float r, float g, float b, boolean isTouch) {
        if (isTouch) {
            this.meshName = "lightning" + GlobalRand.intRange(1, 4) + "t";
        } else {
            this.meshName = "lightning" + GlobalRand.intRange(1, 4);
        }
        this.texName = "lightning_pieces_core";
        this.color = new Vector4(r, g, b, 1.0f);
    }

    public void render(GL10 gl, TextureManager tm, MeshManager mm) {
        if (this.texName != null && this.meshName != null) {
            gl.glEnable(2896);
            gl.glEnable(16384);
            int glowId = tm.getTextureID(gl, "lightning_pieces_glow");
            int coreId = tm.getTextureID(gl, "lightning_pieces_core");
            Mesh mesh = mm.getMeshByName(gl, this.meshName);
            gl.glBlendFunc(770, 1);
            gl.glPushMatrix();
            gl.glTranslatef(this.origin.x, this.origin.y, this.origin.z);
            gl.glScalef(this.scale.x, this.scale.x, this.scale.x);
            gl.glRotatef(this.angles.a, this.angles.x, this.angles.y, this.angles.z);
            if (this.color != null) {
                gl.glColor4f(this.color.x, this.color.y, this.color.z, this.color.a);
            }
            mesh.renderFrameMultiTexture((GL11) gl, 0, glowId, coreId, 260, false);
            gl.glPopMatrix();
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            gl.glDisable(16384);
            gl.glDisable(2896);
        }
    }

    public void update(float timeDelta) {
        super.update(timeDelta);
        this.color.a -= 2.0f * timeDelta;
        if (this.color.a <= 0.0f) {
            delete();
        }
    }
}
