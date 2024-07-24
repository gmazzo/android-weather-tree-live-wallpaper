package io.github.gmazzo.android.livewallpaper.weather.engine.particles

import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.GlobalRand
import io.github.gmazzo.android.livewallpaper.weather.engine.Mesh
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Model
import io.github.gmazzo.android.livewallpaper.weather.wallpaper.Texture
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.math.acos

open class ParticleSystem(
    protected val model: Model,
    protected val texture: Texture,
) {
    private var _animCurrentFrame = 0
    private var _animTimeElapsed = 0.0f
    private var _nextSpawnRateVariance = 0.0f
    private var _numParticles = 0
    private val _particles = arrayOfNulls<Particle>(_maxParticles)
    private var _timeSinceLastSpawn = 0.0f
    private var _useColor = true
    protected var animFrameOffset: Int = 0
    protected var animFramerate: Float = 20.0f
    protected var animLastFrame: Int = 0
    protected var destEngineColor: EngineColor = EngineColor(1.0f, 1.0f, 1.0f, 1.0f)
    var enableSpawning: Boolean = true
    protected var flowDirection: Vector? = null
    private var orientScratch: Vector? = null
    protected var spawnBurst: Int = 0
    protected var spawnRangeX: Float = 0.0f
    protected var spawnRangeY: Float = 0.0f
    protected var spawnRangeZ: Float = 0.0f
    protected var spawnRate: Float = 1.0f
    protected var spawnRateVariance: Float = 0.2f
    protected var startEngineColor: EngineColor = EngineColor(1.0f, 1.0f, 1.0f, 1.0f)

    inner class Particle {
        private var _angle: Float
        private val _Engine_color = EngineColor()
        private val _position = Vector()
        private val _scale = Vector()
        private var _timeElapsed: Float
        private var _useAngles: Boolean
        private var _useScale: Boolean
        protected var _velocity: Vector = Vector()
        var alive: Boolean = false
        var destAngle: Float = 0f
        var destScale: Vector = Vector()
        var destVelocity: Vector = Vector()
        var lifetime: Float = 0f
        var startAngle: Float = 0f
        var startScale: Vector = Vector()
        var startVelocity: Vector = Vector()

        init {
            _position.set(0.0f)
            this._angle = 0.0f
            this._useAngles = false
            this._useScale = false
            this._timeElapsed = 0.0f
        }

        fun modifyPosition(offset_x: Float, offset_y: Float, offset_z: Float) {
            _position.x = _position.x + offset_x
            _position.y = _position.y + offset_y
            _position.z = _position.z + offset_z
        }

        fun render(gl11: GL11, mesh: Mesh) {
            gl11.glMatrixMode(GL10.GL_MODELVIEW)
            gl11.glPushMatrix()
            gl11.glTranslatef(
                _position.x,
                _position.y,
                _position.z
            )
            if (this@ParticleSystem._useColor) {
                gl11.glColor4f(
                    _Engine_color.r,
                    _Engine_color.g,
                    _Engine_color.b,
                    _Engine_color.a
                )
            }
            if (this._useScale) {
                gl11.glScalef(_scale.x, _scale.y, _scale.z)
            }
            if (this._useAngles) {
                gl11.glRotatef(this._angle, 0.0f, 1.0f, 0.0f)
            }
            mesh.renderFrame_gl11_render(gl11)
            gl11.glPopMatrix()
        }

        fun reset() {
            _position.set(0.0f, 0.0f, 0.0f)
            this._timeElapsed = 0.0f
            startVelocity.set(0.0f, 0.0f, 0.0f)
            destVelocity.set(0.0f, 0.0f, 0.0f)
            startScale.set(1.0f, 1.0f, 1.0f)
            destScale.set(1.0f, 1.0f, 1.0f)
            this.startAngle = 0.0f
            this.destAngle = 0.0f
            this.lifetime = 1.0f
        }

        fun setUsageFlags() {
            this._useAngles = this.startAngle != 0.0f || this.destAngle != 0.0f
            this._useScale =
                (startScale.x != 1.0f) || (startScale.y != 1.0f) || (startScale.z != 1.0f) || (destScale.x != 1.0f) || (destScale.y != 1.0f) || (destScale.z != 1.0f)
        }

        fun update(id: Int, timeDelta: Float): Boolean {
            this._timeElapsed += timeDelta
            if (this._timeElapsed > this.lifetime) {
                this.alive = false
                return false
            }
            val percentage = this._timeElapsed / this.lifetime
            val invPercentage = 1.0f - percentage
            updateVelocity(timeDelta, percentage, invPercentage)
            if (this@ParticleSystem._useColor) {
                _Engine_color.set(
                    (startEngineColor.r * invPercentage) + (destEngineColor.r * percentage),
                    (startEngineColor.g * invPercentage) + (destEngineColor.g * percentage),
                    (startEngineColor.b * invPercentage) + (destEngineColor.b * percentage),
                    (startEngineColor.a * invPercentage) + (destEngineColor.a * percentage)
                )
            }
            if (this._useScale) {
                _scale.set(
                    (startScale.x * invPercentage) + (destScale.x * percentage),
                    (startScale.y * invPercentage) + (destScale.y * percentage),
                    (startScale.z * invPercentage) + (destScale.z * percentage)
                )
            }
            if (this._useAngles) {
                this._angle = (this.startAngle * invPercentage) + (this.destAngle * percentage)
            }
            _position.plus(
                _velocity.x * timeDelta,
                _velocity.y * timeDelta,
                _velocity.z * timeDelta
            )
            return true
        }

        fun updateVelocity(timeDelta: Float, percentage: Float, invPercentage: Float) {
            _velocity.set(
                (startVelocity.x * invPercentage) + (destVelocity.x * percentage),
                (startVelocity.y * invPercentage) + (destVelocity.y * percentage),
                (startVelocity.z * invPercentage) + (destVelocity.z * percentage)
            )
        }
    }

    init {
        for (i in _particles.indices) {
            _particles[i] = newParticle()
        }
    }

    private fun handleOrientation(gl11: GL11, newDirection: Vector) {
        if (this.orientScratch == null) {
            this.orientScratch = Vector()
        }
        orientScratch!!.crossProduct(flowDirection!!, newDirection).normalize()
        gl11.glRotatef(
            (acos(
                newDirection.times(
                    flowDirection!!
                ).toDouble()
            )
                .toFloat()) * 57.295776f,
            orientScratch!!.x,
            orientScratch!!.y,
            orientScratch!!.z
        )
    }

    protected fun newParticle(): Particle {
        return Particle()
    }

    open fun particleSetup(particle: Particle?) {
        particle!!.reset()
        var rX = 0.0f
        var rY = 0.0f
        var rZ = 0.0f
        if (spawnRangeX > 0.01f) {
            rX = GlobalRand.floatRange(-spawnRangeX, spawnRangeX)
        }
        if (spawnRangeY > 0.01f) {
            rY = GlobalRand.floatRange(-spawnRangeY, spawnRangeY)
        }
        if (spawnRangeZ > 0.01f) {
            rZ = GlobalRand.floatRange(-spawnRangeZ, spawnRangeZ)
        }
        particle.modifyPosition(rX, rY, rZ)
        particle.alive = true
    }

    open fun render(gl: GL11, systemOrigin: Vector?) {
        render(gl, systemOrigin, null)
    }

    fun render(gl: GL11, systemOrigin: Vector?, direction: Vector?) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.glId)

        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glPushMatrix()
        gl.glTranslatef(systemOrigin!!.x, systemOrigin.y, systemOrigin.z)
        if (!(direction == null || this.flowDirection == null)) {
            handleOrientation(gl, direction)
        }
        renderStart(gl)
        val mesg = model!!.asMesh()
        mesg.renderFrame_gl11_setup(gl, this._animCurrentFrame)
        for (i in _particles.indices) {
            if (_particles[i]!!.alive) {
                _particles[i]!!.render(gl, mesg)
            }
        }
        mesg.renderFrame_gl11_clear(gl)
        renderEnd(gl)
        gl.glPopMatrix()
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    protected open fun renderEnd(gl: GL10?) {
    }

    protected open fun renderStart(gl: GL10) {
    }

    protected fun setUsageFlags() {
        this._useColor =
            (startEngineColor.r != 1.0f) || (startEngineColor.g != 1.0f) || (startEngineColor.b != 1.0f) || (startEngineColor.a != 1.0f) || (destEngineColor.r != 1.0f) || (destEngineColor.g != 1.0f) || (destEngineColor.b != 1.0f) || (destEngineColor.a != 1.0f)
    }

    open fun update(timeDelta: Float) {
        var createNew = 0
        if (this.enableSpawning && this.spawnBurst > 0) {
            createNew = this.spawnBurst
            this.enableSpawning = false
        }
        if (this._numParticles < _maxParticles) {
            this._timeSinceLastSpawn += timeDelta
            while (this._timeSinceLastSpawn + this._nextSpawnRateVariance > this.spawnRate) {
                this._timeSinceLastSpawn -= this.spawnRate + this._nextSpawnRateVariance
                this._nextSpawnRateVariance =
                    GlobalRand.floatRange(-this.spawnRateVariance, this.spawnRateVariance)
                createNew++
            }
        }
        for (i in _particles.indices) {
            if (_particles[i]!!.alive) {
                if (!_particles[i]!!.update(i, timeDelta)) {
                    _numParticles--
                }
            } else if (createNew > 0) {
                var fakeTimeElapsed = 0.001f
                if (createNew > 1 && this.spawnBurst == 0) {
                    fakeTimeElapsed = ((createNew - 1).toFloat()) * this.spawnRate
                }
                particleSetup(_particles[i])
                _particles[i]!!.setUsageFlags()
                _particles[i]!!.update(i, fakeTimeElapsed)
                _numParticles++
                createNew--
                if (this.animLastFrame > 0) {
                    this._animTimeElapsed += timeDelta
                    this._animCurrentFrame = (this._animTimeElapsed * this.animFramerate).toInt()
                    this._animCurrentFrame += this.animFrameOffset
                    this._animCurrentFrame %= this.animLastFrame + 1
                }
            }
        }
    }

    companion object {
        private const val TAG = "GL Engine"
        protected const val _maxParticles: Int = 64
    }
}
