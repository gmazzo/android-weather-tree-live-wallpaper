package gs.weather.engine;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class Thing {
    public Color angles = new Color(0.0f, 0.0f, 1.0f, 0.0f);
    public AnimPlayer anim = null;
    public boolean animInterpolate = false;
    public Color color = null;
    private boolean deleteMe = false;
    public String meshName = null;
    public Vector origin = new Vector(0.0f, 0.0f, 0.0f);
    public ParticleSystem particleSystem;
    public float sTimeElapsed = 0.0f;
    public Vector scale = new Vector(1.0f, 1.0f, 1.0f);
    public String targetName;
    public String texName = null;
    public Vector velocity = null;
    private Vector visScratch = new Vector(0.0f, 0.0f, 0.0f);
    private boolean vis_isVisible = true;
    public float vis_width = 3.0f;

    public void checkVisibility(Vector cameraPos, float cameraAngleZ, float fov) {
        if (this.vis_width == 0.0f) {
            this.vis_isVisible = true;
            return;
        }
        this.visScratch.set(this.origin.getX() - cameraPos.getX(), this.origin.getY() - cameraPos.getY(), this.origin.getZ() - cameraPos.getZ());
        this.visScratch.rotateAroundZ(cameraAngleZ);
        if (Math.abs(this.visScratch.getX()) < this.vis_width + ((this.visScratch.getY() * 0.01111111f) * fov)) {
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
            gl.glTranslatef(this.origin.getX(), this.origin.getY(), this.origin.getZ());
            gl.glScalef(this.scale.getX(), this.scale.getY(), this.scale.getZ());
            if (this.angles.getA() != 0.0f) {
                gl.glRotatef(this.angles.getA(), this.angles.getR(), this.angles.getG(), this.angles.getB());
            }
            gl.glBindTexture(3553, textureId);
            if (this.color != null) {
                gl.glColor4f(this.color.getR(), this.color.getG(), this.color.getB(), this.color.getA());
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
            this.origin.plus(this.velocity.getX() * timeDelta, this.velocity.getY() * timeDelta, this.velocity.getZ() * timeDelta);
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
