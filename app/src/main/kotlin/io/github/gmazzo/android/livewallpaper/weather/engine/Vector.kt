package io.github.gmazzo.android.livewallpaper.weather.engine

import kotlin.math.sqrt

data class Vector(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
) {

    constructor(value: Float) : this(value, value, value)

    operator fun plus(other: Vector) = copy(x = x + other.x, y = y + other.y, z = z + other.z)

    operator fun minus(other: Vector) = copy(x = x - other.x, y = y - other.y, z = z - other.z)

    operator fun times(scalar: Float) = copy(x = x * scalar, y = y * scalar, z = z * scalar)

    operator fun times(other: Vector) = copy(
        x = y * other.z - z * other.y,
        y = z * other.x - x * other.z,
        z = x * other.y - y * other.x
    )

    fun scalarProduct(other: Vector) = x * other.x + y * other.y + z * other.z

    val normalized: Vector
        get() {
            val magnitude = sqrt(x * x + y * y + z * z)
            val reciprocal = 1.0f / magnitude
            return copy(x = x * reciprocal, y = y * reciprocal, z = z * reciprocal)
        }

}
