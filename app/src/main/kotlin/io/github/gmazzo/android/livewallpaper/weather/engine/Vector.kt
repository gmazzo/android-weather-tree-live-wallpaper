package io.github.gmazzo.android.livewallpaper.weather.engine

import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Vector(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f
) {

    fun plus(x: Float, y: Float, z: Float): Vector {
        this.x += x
        this.y += y
        this.z += z
        return this
    }

    operator fun plus(other: Vector) = plus(other.x, other.y, other.z)

    operator fun times(scalar: Float) = times(scalar, scalar, scalar)

    operator fun times(other: Vector) = this.x * other.x + this.y * other.y + this.z * other.z

    fun times(x: Float, y: Float, z: Float): Vector {
        this.x *= x
        this.y *= y
        this.z *= z
        return this
    }

    fun normalize(): Vector {
        val magnitude = sqrt(x * x + y * y + z * z)

        if (magnitude > 0) {
            val reciprocal = 1.0f / magnitude

            this.x *= reciprocal
            this.y *= reciprocal
            this.z *= reciprocal
        }
        return this
    }

    fun rotateAroundZ(degrees: Float): Vector {
        val radians = toRadians(degrees.toDouble())
        val radSin = sin(radians).toFloat()
        val radCos = cos(radians).toFloat()

        this.x = this.x * radCos - this.y * radSin
        this.y = this.x * radSin + this.y * radCos
        return this
    }

    fun set(xyz: Float) = set(xyz, xyz, xyz)

    fun set(x: Float, y: Float, z: Float): Vector {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    fun set(vector: Vector) = set(vector.x, vector.y, vector.z)

    operator fun timesAssign(other: Vector) {
        set(
            this.y * other.z - this.z * other.y,
            this.z * other.x - this.x * other.z,
            this.x * other.y - this.y * other.x
        )
    }

}
