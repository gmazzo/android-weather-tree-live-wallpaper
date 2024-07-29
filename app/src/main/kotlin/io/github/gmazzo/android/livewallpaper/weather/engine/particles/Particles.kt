package io.github.gmazzo.android.livewallpaper.weather.engine.particles

import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Mesh
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Model
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11
import kotlin.math.acos
import kotlin.random.Random

sealed class Particles(
    private val gl: GL11,
    protected val model: Model,
    protected val texture: Texture,
) {
    private var animCurrentFrame = 0
    private var animTimeElapsed = 0.0f
    private var _nextSpawnRateVariance = 0.0f
    private var numParticles = 0
    private val particles = arrayOfNulls<Particle>(MAX_PARTICLES)
    private var timeSinceLastSpawn = 0.0f
    private var _useColor = true
    private var animFrameOffset: Int = 0
    private var animFramerate: Float = 20.0f
    private var animLastFrame: Int = 0
    protected var destEngineColor: EngineColor = EngineColor(1.0f, 1.0f, 1.0f, 1.0f)
    private var enableSpawning: Boolean = true
    private var flowDirection: Vector? = null
    private val orientScratch = Vector()
    private var spawnBurst: Int = 0
    protected var spawnRangeX: Float = 0.0f
    protected var spawnRangeY: Float = 0.0f
    protected var spawnRangeZ: Float = 0.0f
    protected var spawnRate: Float = 1.0f
    protected var spawnRateVariance: Float = 0.2f
    protected var startEngineColor: EngineColor = EngineColor(1.0f, 1.0f, 1.0f, 1.0f)

    open inner class Particle {
        private var angle: Float
        private val color = EngineColor()
        private val position = Vector()
        private val scale = Vector()
        private var timeElapsed: Float
        private var useAngles: Boolean
        private var useScale: Boolean
        private var velocity: Vector = Vector()
        var alive: Boolean = false
        private var destAngle: Float = 0f
        var destScale: Vector = Vector()
        var destVelocity: Vector = Vector()
        var lifetime: Float = 0f
        private var startAngle: Float = 0f
        var startScale: Vector = Vector()
        var startVelocity: Vector = Vector()

        init {
            position.set(0.0f)
            this.angle = 0.0f
            this.useAngles = false
            this.useScale = false
            this.timeElapsed = 0.0f
        }

        fun modifyPosition(offsetX: Float, offsetY: Float, offsetZ: Float) {
            position.x += offsetX
            position.y += offsetY
            position.z += offsetZ
        }

        fun render(gl11: GL11, mesh: Mesh) = gl.pushMatrix {
            gl11.glMatrixMode(GL10.GL_MODELVIEW)
            gl11.glTranslatef(
                position.x,
                position.y,
                position.z
            )
            if (this@Particles._useColor) {
                gl11.glColor4f(
                    color.r,
                    color.g,
                    color.b,
                    color.a
                )
            }
            if (useScale) {
                gl11.glScalef(scale.x, scale.y, scale.z)
            }
            if (useAngles) {
                gl11.glRotatef(angle, 0.0f, 1.0f, 0.0f)
            }
            mesh.renderFrame_gl11_render(gl11)
        }

        fun reset() {
            position.set(0.0f, 0.0f, 0.0f)
            this.timeElapsed = 0.0f
            startVelocity.set(0.0f, 0.0f, 0.0f)
            destVelocity.set(0.0f, 0.0f, 0.0f)
            startScale.set(1.0f, 1.0f, 1.0f)
            destScale.set(1.0f, 1.0f, 1.0f)
            this.startAngle = 0.0f
            this.destAngle = 0.0f
            this.lifetime = 1.0f
        }

        fun setUsageFlags() {
            this.useAngles = this.startAngle != 0.0f || this.destAngle != 0.0f
            this.useScale =
                (startScale.x != 1.0f) || (startScale.y != 1.0f) || (startScale.z != 1.0f) || (destScale.x != 1.0f) || (destScale.y != 1.0f) || (destScale.z != 1.0f)
        }

        fun update(timeDelta: Float): Boolean {
            this.timeElapsed += timeDelta
            if (this.timeElapsed > this.lifetime) {
                this.alive = false
                return false
            }
            val percentage = this.timeElapsed / this.lifetime
            val invPercentage = 1.0f - percentage
            updateVelocity(percentage, invPercentage)
            if (this@Particles._useColor) {
                color.set(
                    (startEngineColor.r * invPercentage) + (destEngineColor.r * percentage),
                    (startEngineColor.g * invPercentage) + (destEngineColor.g * percentage),
                    (startEngineColor.b * invPercentage) + (destEngineColor.b * percentage),
                    (startEngineColor.a * invPercentage) + (destEngineColor.a * percentage)
                )
            }
            if (this.useScale) {
                scale.set(
                    (startScale.x * invPercentage) + (destScale.x * percentage),
                    (startScale.y * invPercentage) + (destScale.y * percentage),
                    (startScale.z * invPercentage) + (destScale.z * percentage)
                )
            }
            if (this.useAngles) {
                this.angle = (this.startAngle * invPercentage) + (this.destAngle * percentage)
            }
            position.plus(
                velocity.x * timeDelta,
                velocity.y * timeDelta,
                velocity.z * timeDelta
            )
            return true
        }

        private fun updateVelocity(percentage: Float, invPercentage: Float) {
            velocity.set(
                (startVelocity.x * invPercentage) + (destVelocity.x * percentage),
                (startVelocity.y * invPercentage) + (destVelocity.y * percentage),
                (startVelocity.z * invPercentage) + (destVelocity.z * percentage)
            )
        }
    }

    init {
        for (i in particles.indices) {
            particles[i] = newParticle()
        }
    }

    private fun handleOrientation(gl11: GL11, newDirection: Vector) {
        orientScratch.set(flowDirection!!)
        orientScratch *= newDirection
        orientScratch.normalize()

        gl11.glRotatef(
            (acos(
                newDirection.times(
                    flowDirection!!
                ).toDouble()
            )
                .toFloat()) * 57.295776f,
            orientScratch.x,
            orientScratch.y,
            orientScratch.z
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
            rX = Random.nextFloat(-spawnRangeX, spawnRangeX)
        }
        if (spawnRangeY > 0.01f) {
            rY = Random.nextFloat(-spawnRangeY, spawnRangeY)
        }
        if (spawnRangeZ > 0.01f) {
            rZ = Random.nextFloat(-spawnRangeZ, spawnRangeZ)
        }
        particle.modifyPosition(rX, rY, rZ)
        particle.alive = true
    }

    fun render(systemOrigin: Vector?, direction: Vector? = null) = gl.pushMatrix {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.glId)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glTranslatef(systemOrigin!!.x, systemOrigin.y, systemOrigin.z)
        if (!(direction == null || flowDirection == null)) {
            handleOrientation(gl, direction)
        }
        renderStart(gl)
        val mesg = model.asMesh()
        mesg.renderFrame_gl11_setup(gl, animCurrentFrame)
        for (i in particles.indices) {
            if (particles[i]!!.alive) {
                particles[i]!!.render(gl, mesg)
            }
        }
        mesg.renderFrame_gl11_clear(gl)
        renderEnd(gl)
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
        if (this.numParticles < MAX_PARTICLES) {
            this.timeSinceLastSpawn += timeDelta
            while (this.timeSinceLastSpawn + this._nextSpawnRateVariance > this.spawnRate) {
                this.timeSinceLastSpawn -= this.spawnRate + this._nextSpawnRateVariance
                this._nextSpawnRateVariance =
                    Random.nextFloat(-this.spawnRateVariance, this.spawnRateVariance)
                createNew++
            }
        }
        for (i in particles.indices) {
            if (particles[i]!!.alive) {
                if (!particles[i]!!.update(timeDelta)) {
                    numParticles--
                }
            } else if (createNew > 0) {
                var fakeTimeElapsed = 0.001f
                if (createNew > 1 && this.spawnBurst == 0) {
                    fakeTimeElapsed = ((createNew - 1).toFloat()) * this.spawnRate
                }
                particleSetup(particles[i])
                particles[i]!!.setUsageFlags()
                particles[i]!!.update(fakeTimeElapsed)
                numParticles++
                createNew--
                if (this.animLastFrame > 0) {
                    this.animTimeElapsed += timeDelta
                    this.animCurrentFrame = (this.animTimeElapsed * this.animFramerate).toInt()
                    this.animCurrentFrame += this.animFrameOffset
                    this.animCurrentFrame %= this.animLastFrame + 1
                }
            }
        }
    }

    companion object {
        protected const val MAX_PARTICLES: Int = 64
    }
}
