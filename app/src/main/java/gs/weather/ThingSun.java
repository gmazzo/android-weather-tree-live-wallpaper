package gs.weather;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import gs.weather.engine.Mesh;
import gs.weather.engine.MeshManager;
import gs.weather.engine.TextureManager;
import gs.weather.engine.Thing;
import gs.weather.engine.Color;

public class ThingSun extends Thing {
    private static final String TAG = "Sun";

    public ThingSun() {
        this.texName = "sun";
        this.meshName = "plane_16x16";
        this.color = new Color(1.0f, 1.0f, 0.95f, 1.0f);
    }

    public void render(GL10 gl, TextureManager texturemanager, MeshManager meshmanager) {
        gl.glBlendFunc(1, 769);
        int blendTexId = texturemanager.getTextureID(gl, "sun_blend");
        int sunTexId = texturemanager.getTextureID(gl, "sun");
        Mesh mesh = meshmanager.getMeshByName(gl, this.meshName);
        gl.glColor4f(this.color.getR(), this.color.getG(), this.color.getB(), this.color.getA());
        gl.glMatrixMode(5888);
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
            mesh.renderFrameMultiTexture((GL11) gl, 0, blendTexId, sunTexId, 8448, false);
        } else {
            gl.glBindTexture(33984, sunTexId);
            mesh.render(gl);
        }
        gl.glPopMatrix();
        gl.glMatrixMode(5888);
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
