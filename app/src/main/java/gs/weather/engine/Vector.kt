package gs.weather.engine

data class Vector(var x: Float = 0f,
                  var y: Float = 0f,
                  var z: Float = 0f) {

    val magnitude: Float
        get() = Math.sqrt(x.toDouble() * x + y.toDouble() * y + z.toDouble() * z).toFloat()

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
        magnitude.takeIf { it != 0f }?.also {
            val reciprocal = 1.0f / it
            this.x *= reciprocal
            this.y *= reciprocal
            this.z *= reciprocal
        }
        return this
    }

    fun rotateAroundZ(degrees: Float): Vector {
        val radians = Math.toRadians(degrees.toDouble())
        val radSin = Math.sin(radians).toFloat()
        val radCos = Math.cos(radians).toFloat()
        this.x = this.x * radCos - this.y * radSin
        this.y = this.x * radSin + this.y * radCos
        return this
    }

    fun set(xyz: Float) = set(xyz, xyz, xyz)

    fun set(X: Float, Y: Float, Z: Float): Vector {
        this.x = X
        this.y = Y
        this.z = Z
        return this
    }

    fun set(vector: Vector) = set(vector.x, vector.y, vector.z)

    fun crossProduct(other: Vector) = crossProduct(this, other)

    fun crossProduct(a: Vector, b: Vector) = set(
            a.y * b.z - a.z * b.y,
            a.z * b.x - a.x * b.z,
            a.x * b.y - a.y * b.x)

}
