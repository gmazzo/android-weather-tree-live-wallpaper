package gs.weather.engine;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;

public class ParticleSystem {
    private static final String TAG = "GL Engine";
    protected static final int _maxParticles = 64;
    private int _animCurrentFrame = 0;
    private float _animTimeElapsed = 0.0f;
    private float _nextSpawnRateVariance = 0.0f;
    private int _numParticles;
    private Particle[] _particles = new Particle[_maxParticles];
    private float _timeSinceLastSpawn = 0.0f;
    private boolean _useColor = true;
    protected int animFrameOffset = 0;
    protected float animFramerate = 20.0f;
    protected int animLastFrame = 0;
    protected Color destColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    public boolean enableSpawning = true;
    protected Vector flowDirection = null;
    protected String meshName;
    private Vector orientScratch = null;
    protected int spawnBurst = 0;
    protected float spawnRangeX = 0.0f;
    protected float spawnRangeY = 0.0f;
    protected float spawnRangeZ = 0.0f;
    protected float spawnRate = 1.0f;
    protected float spawnRateVariance = 0.2f;
    protected Color startColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    protected String texName;

    public class Particle {
        private float _angle;
        private Color _color = new Color();
        private Vector _position = new Vector();
        private Vector _scale = new Vector();
        private float _timeElapsed;
        private boolean _useAngles;
        private boolean _useScale;
        protected Vector _velocity = new Vector();
        public boolean alive = false;
        public float destAngle;
        public Vector destScale = new Vector();
        public Vector destVelocity = new Vector();
        public float lifetime;
        public float startAngle;
        public Vector startScale = new Vector();
        public Vector startVelocity = new Vector();

        public Particle() {
            this._position.set(0.0f);
            this._angle = 0.0f;
            this._useAngles = false;
            this._useScale = false;
            this._timeElapsed = 0.0f;
        }

        void modifyPosition(float offset_x, float offset_y, float offset_z) {
            this._position.setX(this._position.getX() + offset_x);
            this._position.setY(this._position.getY() + offset_y);
            this._position.setZ(this._position.getZ() + offset_z);
        }

        public void render(GL11 gl11, Mesh mesh) {
            gl11.glMatrixMode(GL_MODELVIEW);
            gl11.glPushMatrix();
            gl11.glTranslatef(this._position.getX(), this._position.getY(), this._position.getZ());
            if (ParticleSystem.this._useColor) {
                gl11.glColor4f(this._color.getR(), this._color.getG(), this._color.getB(), this._color.getA());
            }
            if (this._useScale) {
                gl11.glScalef(this._scale.getX(), this._scale.getY(), this._scale.getZ());
            }
            if (this._useAngles) {
                gl11.glRotatef(this._angle, 0.0f, 1.0f, 0.0f);
            }
            mesh.renderFrame_gl11_render(gl11);
            gl11.glPopMatrix();
        }

        void reset() {
            this._position.set(0.0f, 0.0f, 0.0f);
            this._timeElapsed = 0.0f;
            this.startVelocity.set(0.0f, 0.0f, 0.0f);
            this.destVelocity.set(0.0f, 0.0f, 0.0f);
            this.startScale.set(1.0f, 1.0f, 1.0f);
            this.destScale.set(1.0f, 1.0f, 1.0f);
            this.startAngle = 0.0f;
            this.destAngle = 0.0f;
            this.lifetime = 1.0f;
        }

        public void setUsageFlags() {
            if (this.startAngle == 0.0f && this.destAngle == 0.0f) {
                this._useAngles = false;
            } else {
                this._useAngles = true;
            }
            if (this.startScale.getX() == 1.0f && this.startScale.getY() == 1.0f && this.startScale.getZ() == 1.0f && this.destScale.getX() == 1.0f && this.destScale.getY() == 1.0f && this.destScale.getZ() == 1.0f) {
                this._useScale = false;
            } else {
                this._useScale = true;
            }
        }

        public boolean update(int id, float timeDelta) {
            this._timeElapsed += timeDelta;
            if (this._timeElapsed > this.lifetime) {
                this.alive = false;
                return false;
            }
            float percentage = this._timeElapsed / this.lifetime;
            float invPercentage = 1.0f - percentage;
            updateVelocity(timeDelta, percentage, invPercentage);
            if (ParticleSystem.this._useColor) {
                this._color.set((ParticleSystem.this.startColor.getR() * invPercentage) + (ParticleSystem.this.destColor.getR() * percentage), (ParticleSystem.this.startColor.getG() * invPercentage) + (ParticleSystem.this.destColor.getG() * percentage), (ParticleSystem.this.startColor.getB() * invPercentage) + (ParticleSystem.this.destColor.getB() * percentage), (ParticleSystem.this.startColor.getA() * invPercentage) + (ParticleSystem.this.destColor.getA() * percentage));
            }
            if (this._useScale) {
                this._scale.set((this.startScale.getX() * invPercentage) + (this.destScale.getX() * percentage), (this.startScale.getY() * invPercentage) + (this.destScale.getY() * percentage), (this.startScale.getZ() * invPercentage) + (this.destScale.getZ() * percentage));
            }
            if (this._useAngles) {
                this._angle = (this.startAngle * invPercentage) + (this.destAngle * percentage);
            }
            this._position.plus(this._velocity.getX() * timeDelta, this._velocity.getY() * timeDelta, this._velocity.getZ() * timeDelta);
            return true;
        }

        public void updateVelocity(float timeDelta, float percentage, float invPercentage) {
            this._velocity.set((this.startVelocity.getX() * invPercentage) + (this.destVelocity.getX() * percentage), (this.startVelocity.getY() * invPercentage) + (this.destVelocity.getY() * percentage), (this.startVelocity.getZ() * invPercentage) + (this.destVelocity.getZ() * percentage));
        }
    }

    public ParticleSystem() {
        for (int i = 0; i < this._particles.length; i++) {
            this._particles[i] = newParticle();
        }
    }

    private void handleOrientation(GL11 gl11, Vector newDirection) {
        if (this.orientScratch == null) {
            this.orientScratch = new Vector();
        }
        this.orientScratch.crossProduct(this.flowDirection, newDirection).normalize();
        gl11.glRotatef(((float) Math.acos((double) newDirection.times(this.flowDirection))) * 57.295776f, this.orientScratch.getX(), this.orientScratch.getY(), this.orientScratch.getZ());
    }

    protected float getSpawnRangeX() {
        return this.spawnRangeX;
    }

    protected float getSpawnRangeY() {
        return this.spawnRangeY;
    }

    protected float getSpawnRangeZ() {
        return this.spawnRangeZ;
    }

    protected Particle newParticle() {
        return new Particle();
    }

    public void particleSetup(Particle particle) {
        particle.reset();
        float rX = 0.0f;
        float rY = 0.0f;
        float rZ = 0.0f;
        if (getSpawnRangeX() > 0.01f) {
            rX = GlobalRand.floatRange(-getSpawnRangeX(), getSpawnRangeX());
        }
        if (getSpawnRangeY() > 0.01f) {
            rY = GlobalRand.floatRange(-getSpawnRangeY(), getSpawnRangeY());
        }
        if (getSpawnRangeZ() > 0.01f) {
            rZ = GlobalRand.floatRange(-getSpawnRangeZ(), getSpawnRangeZ());
        }
        particle.modifyPosition(rX, rY, rZ);
        particle.alive = true;
    }

    public void render(GL11 gl, TextureManager tm, MeshManager mm, Vector systemOrigin) {
        render(gl, tm, mm, systemOrigin, null);
    }

    public void render(GL11 gl, TextureManager tm, MeshManager mm, Vector systemOrigin, Vector direction) {
        tm.bindTextureID(gl, this.texName);
        Mesh mesg = mm.getMeshByName(gl, this.meshName);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glTranslatef(systemOrigin.getX(), systemOrigin.getY(), systemOrigin.getZ());
        if (!(direction == null || this.flowDirection == null)) {
            handleOrientation(gl, direction);
        }
        renderStart(gl);
        mesg.renderFrame_gl11_setup(gl, this._animCurrentFrame);
        for (int i = 0; i < this._particles.length; i++) {
            if (this._particles[i].alive) {
                this._particles[i].render(gl, mesg);
            }
        }
        mesg.renderFrame_gl11_clear(gl);
        renderEnd(gl);
        gl.glPopMatrix();
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    protected void renderEnd(GL10 gl) {
    }

    protected void renderStart(GL10 gl) {
    }

    protected void setUsageFlags() {
        if (this.startColor.getR() == 1.0f && this.startColor.getG() == 1.0f && this.startColor.getB() == 1.0f && this.startColor.getA() == 1.0f && this.destColor.getR() == 1.0f && this.destColor.getG() == 1.0f && this.destColor.getB() == 1.0f && this.destColor.getA() == 1.0f) {
            this._useColor = false;
        } else {
            this._useColor = true;
        }
    }

    public void update(float timeDelta) {
        int createNew = 0;
        if (this.enableSpawning && this.spawnBurst > 0) {
            createNew = this.spawnBurst;
            this.enableSpawning = false;
        }
        if (this._numParticles < _maxParticles) {
            this._timeSinceLastSpawn += timeDelta;
            while (this._timeSinceLastSpawn + this._nextSpawnRateVariance > this.spawnRate) {
                this._timeSinceLastSpawn -= this.spawnRate + this._nextSpawnRateVariance;
                this._nextSpawnRateVariance = GlobalRand.floatRange(-this.spawnRateVariance, this.spawnRateVariance);
                createNew++;
            }
        }
        for (int i = 0; i < this._particles.length; i++) {
            if (this._particles[i].alive) {
                if (!this._particles[i].update(i, timeDelta)) {
                    this._numParticles--;
                }
            } else if (createNew > 0) {
                float fakeTimeElapsed = 0.001f;
                if (createNew > 1 && this.spawnBurst == 0) {
                    fakeTimeElapsed = ((float) (createNew - 1)) * this.spawnRate;
                }
                particleSetup(this._particles[i]);
                this._particles[i].setUsageFlags();
                this._particles[i].update(i, fakeTimeElapsed);
                this._numParticles++;
                createNew--;
                if (this.animLastFrame > 0) {
                    this._animTimeElapsed += timeDelta;
                    this._animCurrentFrame = (int) (this._animTimeElapsed * this.animFramerate);
                    this._animCurrentFrame += this.animFrameOffset;
                    this._animCurrentFrame %= this.animLastFrame + 1;
                }
            }
        }
    }
}
