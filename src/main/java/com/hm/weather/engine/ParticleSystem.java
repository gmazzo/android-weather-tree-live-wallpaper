package com.hm.weather.engine;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

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
    protected Vector4 destColor = new Vector4(1.0f, 1.0f, 1.0f, 1.0f);
    public boolean enableSpawning = true;
    protected Vector3 flowDirection = null;
    protected String meshName;
    private Vector3 orientScratch = null;
    protected int spawnBurst = 0;
    protected float spawnRangeX = 0.0f;
    protected float spawnRangeY = 0.0f;
    protected float spawnRangeZ = 0.0f;
    protected float spawnRate = 1.0f;
    protected float spawnRateVariance = 0.2f;
    protected Vector4 startColor = new Vector4(1.0f, 1.0f, 1.0f, 1.0f);
    protected String texName;

    public class Particle {
        private float _angle;
        private Vector4 _color = new Vector4();
        private Vector3 _position = new Vector3();
        private Vector3 _scale = new Vector3();
        private float _timeElapsed;
        private boolean _useAngles;
        private boolean _useScale;
        protected Vector3 _velocity = new Vector3();
        public boolean alive = false;
        public float destAngle;
        public Vector3 destScale = new Vector3();
        public Vector3 destVelocity = new Vector3();
        public float lifetime;
        public float startAngle;
        public Vector3 startScale = new Vector3();
        public Vector3 startVelocity = new Vector3();

        public Particle() {
            this._position.set(0.0f);
            this._angle = 0.0f;
            this._useAngles = false;
            this._useScale = false;
            this._timeElapsed = 0.0f;
        }

        void modifyPosition(float offset_x, float offset_y, float offset_z) {
            this._position.x += offset_x;
            this._position.y += offset_y;
            this._position.z += offset_z;
        }

        public void render(GL11 gl11, Mesh mesh) {
            gl11.glMatrixMode(5888);
            gl11.glPushMatrix();
            gl11.glTranslatef(this._position.x, this._position.y, this._position.z);
            if (ParticleSystem.this._useColor) {
                gl11.glColor4f(this._color.x, this._color.y, this._color.z, this._color.a);
            }
            if (this._useScale) {
                gl11.glScalef(this._scale.x, this._scale.y, this._scale.z);
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
            if (this.startScale.x == 1.0f && this.startScale.y == 1.0f && this.startScale.z == 1.0f && this.destScale.x == 1.0f && this.destScale.y == 1.0f && this.destScale.z == 1.0f) {
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
                this._color.set((ParticleSystem.this.startColor.x * invPercentage) + (ParticleSystem.this.destColor.x * percentage), (ParticleSystem.this.startColor.y * invPercentage) + (ParticleSystem.this.destColor.y * percentage), (ParticleSystem.this.startColor.z * invPercentage) + (ParticleSystem.this.destColor.z * percentage), (ParticleSystem.this.startColor.a * invPercentage) + (ParticleSystem.this.destColor.a * percentage));
            }
            if (this._useScale) {
                this._scale.set((this.startScale.x * invPercentage) + (this.destScale.x * percentage), (this.startScale.y * invPercentage) + (this.destScale.y * percentage), (this.startScale.z * invPercentage) + (this.destScale.z * percentage));
            }
            if (this._useAngles) {
                this._angle = (this.startAngle * invPercentage) + (this.destAngle * percentage);
            }
            this._position.add(this._velocity.x * timeDelta, this._velocity.y * timeDelta, this._velocity.z * timeDelta);
            return true;
        }

        public void updateVelocity(float timeDelta, float percentage, float invPercentage) {
            this._velocity.set((this.startVelocity.x * invPercentage) + (this.destVelocity.x * percentage), (this.startVelocity.y * invPercentage) + (this.destVelocity.y * percentage), (this.startVelocity.z * invPercentage) + (this.destVelocity.z * percentage));
        }
    }

    public ParticleSystem() {
        for (int i = 0; i < this._particles.length; i++) {
            this._particles[i] = newParticle();
        }
    }

    private void handleOrientation(GL11 gl11, Vector3 newDirection) {
        if (this.orientScratch == null) {
            this.orientScratch = new Vector3();
        }
        Vector3.crossProduct(this.orientScratch, this.flowDirection, newDirection);
        this.orientScratch.normalize();
        gl11.glRotatef(((float) Math.acos((double) newDirection.dotProduct(this.flowDirection))) * 57.295776f, this.orientScratch.x, this.orientScratch.y, this.orientScratch.z);
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

    public void render(GL11 gl, TextureManager tm, MeshManager mm, Vector3 systemOrigin) {
        render(gl, tm, mm, systemOrigin, null);
    }

    public void render(GL11 gl, TextureManager tm, MeshManager mm, Vector3 systemOrigin, Vector3 direction) {
        tm.bindTextureID(gl, this.texName);
        Mesh mesg = mm.getMeshByName(gl, this.meshName);
        gl.glMatrixMode(5888);
        gl.glPushMatrix();
        gl.glTranslatef(systemOrigin.x, systemOrigin.y, systemOrigin.z);
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
        if (this.startColor.x == 1.0f && this.startColor.y == 1.0f && this.startColor.z == 1.0f && this.startColor.a == 1.0f && this.destColor.x == 1.0f && this.destColor.y == 1.0f && this.destColor.z == 1.0f && this.destColor.a == 1.0f) {
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
