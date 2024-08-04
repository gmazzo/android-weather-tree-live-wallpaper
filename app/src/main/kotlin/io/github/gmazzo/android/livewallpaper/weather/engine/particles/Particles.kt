package io.github.gmazzo.android.livewallpaper.weather.engine.particles

import io.github.gmazzo.android.livewallpaper.weather.engine.EngineColor
import io.github.gmazzo.android.livewallpaper.weather.engine.Vector
import io.github.gmazzo.android.livewallpaper.weather.engine.models.Model
import io.github.gmazzo.android.livewallpaper.weather.engine.nextFloat
import io.github.gmazzo.android.livewallpaper.weather.engine.pushMatrix
import io.github.gmazzo.android.livewallpaper.weather.engine.textures.Texture
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL10.GL_MODELVIEW
import javax.microedition.khronos.opengles.GL10.GL_ONE
import javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA
import javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D
import javax.microedition.khronos.opengles.GL11
import kotlin.math.acos
import kotlin.random.Random

sealed class Particles(
    private val gl: GL11,
    private val model: Model,
    private val texture: Texture,
    private val spawnRate: Float = 1f,
    private val spawnRateVariance: Float = .2f,
    private val spawnRangeX: Float = 0f,
    private val spawnRangeY: Float = 0f,
    private val spawnRangeZ: Float = 0f,
    private val translucent: Boolean = false,
) {
    private var animCurrentFrame = 0
    private var animTimeElapsed = 0f
    private var nextSpawnRateVariance = 0f
    private var numParticles = 0
    private val particles = (0..MAX_PARTICLES).map { Particle() }.toTypedArray()
    private var timeSinceLastSpawn = 0f
    private var useColor = true
    private var animFrameOffset = 0
    private var animFrameRate = 20f
    private var animLastFrame = 0
    private var enableSpawning = true
    private var flowDirection: Vector? = null
    private var orientScratch = Vector()
    private var spawnBurst = 0
    protected val destEngineColor = EngineColor(1f, 1f, 1f, 1f)
    protected val startEngineColor = EngineColor(1f, 1f, 1f, 1f)

    inner class Particle {
        private var angle = 0f
        private val color = EngineColor()
        lateinit var position: Vector
        private var scale = Vector()
        private var timeElapsed = 0f
        private var useAngles = false
        private var useScale = false
        private var velocity: Vector = Vector()
        var alive = false
        private var destAngle = 0f
        lateinit var destScale: Vector
        lateinit var destVelocity: Vector
        var lifetime = 0f
        private var startAngle = 0f
        lateinit var startScale: Vector
        lateinit var startVelocity: Vector

        fun render() = gl.pushMatrix(GL_MODELVIEW) {
            gl.glTranslatef(position.x, position.y, position.z)
            if (useColor) {
                gl.glColor4f(color.r, color.g, color.b, color.a)
            }
            if (useScale) {
                gl.glScalef(scale.x, scale.y, scale.z)
            }
            if (useAngles) {
                gl.glRotatef(angle, 0f, 1f, 0f)
            }

            model.render()
        }

        @Inject
        fun reset() {
            position = Vector()
            timeElapsed = 0f
            startVelocity = Vector()
            destVelocity = Vector()
            startScale = Vector(1f)
            destScale = Vector(1f)
            startAngle = 0f
            destAngle = 0f
            lifetime = 1f
        }

        fun setUsageFlags() {
            useAngles = startAngle != 0f || destAngle != 0f
            useScale =
                (startScale.x != 1f) || (startScale.y != 1f) || (startScale.z != 1f) || (destScale.x != 1f) || (destScale.y != 1f) || (destScale.z != 1f)
        }

        fun update(timeDelta: Float): Boolean {
            timeElapsed += timeDelta
            if (timeElapsed > lifetime) {
                alive = false
                return false
            }
            val percentage = timeElapsed / lifetime
            val invPercentage = 1f - percentage
            updateVelocity(percentage, invPercentage)
            if (useColor) {
                color.set(
                    (startEngineColor.r * invPercentage) + (destEngineColor.r * percentage),
                    (startEngineColor.g * invPercentage) + (destEngineColor.g * percentage),
                    (startEngineColor.b * invPercentage) + (destEngineColor.b * percentage),
                    (startEngineColor.a * invPercentage) + (destEngineColor.a * percentage)
                )
            }
            if (useScale) {
                scale = Vector(
                    (startScale.x * invPercentage) + (destScale.x * percentage),
                    (startScale.y * invPercentage) + (destScale.y * percentage),
                    (startScale.z * invPercentage) + (destScale.z * percentage)
                )
            }
            if (useAngles) {
                angle = (startAngle * invPercentage) + (destAngle * percentage)
            }
            position += velocity * timeDelta
            return true
        }

        private fun updateVelocity(percentage: Float, invPercentage: Float) {
            velocity = Vector(
                (startVelocity.x * invPercentage) + (destVelocity.x * percentage),
                (startVelocity.y * invPercentage) + (destVelocity.y * percentage),
                (startVelocity.z * invPercentage) + (destVelocity.z * percentage)
            )
        }
    }

    private fun handleOrientation(newDirection: Vector) {
        orientScratch = (flowDirection!! * newDirection).normalized

        val angle = acos(newDirection.scalarProduct(flowDirection!!)) * 57.295776f

        gl.glRotatef(angle, orientScratch.x, orientScratch.y, orientScratch.z)
    }

    open fun particleSetup(particle: Particle) {
        particle.reset()
        particle.position += Vector(
            if (spawnRangeX > .01f) Random.nextFloat(-spawnRangeX, spawnRangeX) else 0f,
            if (spawnRangeY > .01f) Random.nextFloat(-spawnRangeY, spawnRangeY) else 0f,
            if (spawnRangeZ > .01f) Random.nextFloat(-spawnRangeZ, spawnRangeZ) else 0f,
        )
        particle.alive = true
    }

    fun render(systemOrigin: Vector?, direction: Vector? = null) = gl.pushMatrix(GL_MODELVIEW) {
        gl.glBindTexture(GL_TEXTURE_2D, texture.glId)
        gl.glTranslatef(systemOrigin!!.x, systemOrigin.y, systemOrigin.z)
        if (!(direction == null || flowDirection == null)) {
            handleOrientation(direction)
        }
        gl.glBlendFunc(if (translucent) GL_SRC_ALPHA else GL_ONE, GL_ONE_MINUS_SRC_ALPHA)

        particles.forEach {
            if (it.alive) {
                it.render()
            }
        }


        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0)
        gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0)
        gl.glColor4f(1f, 1f, 1f, 1f)
    }

    open fun update(timeDelta: Float) {
        var createNew = 0

        if (enableSpawning && spawnBurst > 0) {
            createNew = spawnBurst
            enableSpawning = false
        }
        if (numParticles < MAX_PARTICLES) {
            timeSinceLastSpawn += timeDelta
            while (timeSinceLastSpawn + nextSpawnRateVariance > spawnRate) {
                timeSinceLastSpawn -= spawnRate + nextSpawnRateVariance
                nextSpawnRateVariance =
                    Random.nextFloat(-spawnRateVariance, spawnRateVariance)
                createNew++
            }
        }
        particles.forEach { particle ->
            if (particle.alive) {
                if (!particle.update(timeDelta)) {
                    numParticles--
                }

            } else if (createNew > 0) {
                var fakeTimeElapsed = .001f

                if (createNew > 1 && spawnBurst == 0) {
                    fakeTimeElapsed = ((createNew - 1).toFloat()) * spawnRate
                }

                particleSetup(particle)
                particle.setUsageFlags()
                particle.update(fakeTimeElapsed)

                numParticles++
                createNew--
                if (animLastFrame > 0) {
                    animTimeElapsed += timeDelta
                    animCurrentFrame =
                        ((animTimeElapsed * animFrameRate) + animFrameOffset).toInt() % (animLastFrame + 1)
                }
            }
        }
    }

    companion object {
        protected const val MAX_PARTICLES: Int = 64
    }
}
