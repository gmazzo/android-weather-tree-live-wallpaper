package gs.weather.engine;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class Thing {
    public Vector4 angles = new Vector4(0.0f, 0.0f, 1.0f, 0.0f);
    public AnimPlayer anim = null;
    public boolean animInterpolate = false;
    public Vector4 color = null;
    private boolean deleteMe = false;
    public String meshName = null;
    public Vector3 origin = new Vector3(0.0f, 0.0f, 0.0f);
    public ParticleSystem particleSystem;
    public float sTimeElapsed = 0.0f;
    public Vector3 scale = new Vector3(1.0f, 1.0f, 1.0f);
    public String targetName;
    public String texName = null;
    public Vector3 velocity = null;
    private Vector3 visScratch = new Vector3(0.0f, 0.0f, 0.0f);
    private boolean vis_isVisible = true;
    public float vis_width = 3.0f;

    public void checkVisibility(Vector3 cameraPos, float cameraAngleZ, float fov) {
        if (this.vis_width == 0.0f) {
            this.vis_isVisible = true;
            return;
        }
        this.visScratch.set(this.origin.x - cameraPos.x, this.origin.y - cameraPos.y, this.origin.z - cameraPos.z);
        this.visScratch.rotateAroundZ(cameraAngleZ);
        if (Math.abs(this.visScratch.x) < this.vis_width + ((this.visScratch.y * 0.01111111f) * fov)) {
            this.vis_isVisible = true;
        } else {
            this.vis_isVisible = false;
        }
    }

    public void delete() {
        this.deleteMe = true;
    }

    public boolean isDeleted() {
        return this.deleteMe;
    }

    public void render(GL10 gl, TextureManager texturemanager, MeshManager meshmanager) {
        if (this.particleSystem != null && (gl instanceof GL11)) {
            this.particleSystem.render((GL11) gl, texturemanager, meshmanager, this.origin);
        }
        if (this.texName != null && this.meshName != null) {
            int textureId = texturemanager.getTextureID(gl, this.texName);
            Mesh mesh = meshmanager.getMeshByName(gl, this.meshName);
            gl.glMatrixMode(5888);
            gl.glPushMatrix();
            gl.glTranslatef(this.origin.x, this.origin.y, this.origin.z);
            gl.glScalef(this.scale.x, this.scale.y, this.scale.z);
            if (this.angles.a != 0.0f) {
                gl.glRotatef(this.angles.a, this.angles.x, this.angles.y, this.angles.z);
            }
            gl.glBindTexture(3553, textureId);
            if (this.color != null) {
                gl.glColor4f(this.color.x, this.color.y, this.color.z, this.color.a);
            }
            if (this.anim == null) {
                mesh.render(gl);
            } else if (this.animInterpolate) {
                mesh.renderFrameInterpolated(gl, this.anim.getCurrentFrame(), this.anim.getBlendFrame(), this.anim.getBlendFrameAmount());
            } else {
                mesh.renderFrame(gl, this.anim.getCurrentFrame());
            }
            gl.glPopMatrix();
            if (this.color != null) {
                gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    public void renderIfVisible(GL10 gl10, TextureManager textureManager, MeshManager meshManager) {
        if (this.vis_isVisible) {
            render(gl10, textureManager, meshManager);
        }
    }

    public void update(float timeDelta) {
        this.sTimeElapsed += timeDelta;
        if (this.velocity != null) {
            this.origin.add(this.velocity.x * timeDelta, this.velocity.y * timeDelta, this.velocity.z * timeDelta);
        }
        if (this.anim != null) {
            this.anim.update(timeDelta);
        }
        if (this.particleSystem != null) {
            this.particleSystem.update(timeDelta);
        }
    }

    public void updateIfVisible(float timeDelta) {
        if (this.vis_isVisible) {
            update(timeDelta);
        }
    }
}
