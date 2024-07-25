package io.github.gmazzo.android.livewallpaper.weather.engine

import android.graphics.Color
import androidx.annotation.ColorInt

data class EngineColor(
    var r: Float = 1f,
    var g: Float = 1f,
    var b: Float = 1f,
    var a: Float = 1f
) {

    operator fun timesAssign(scale: Float) {
        this.r *= scale
        this.g *= scale
        this.b *= scale
        this.a *= scale
    }

    fun set(x: Float, y: Float, z: Float, a: Float): EngineColor {
        this.r = x
        this.g = y
        this.b = z
        this.a = a
        return this
    }

    fun set(@ColorInt color: Int) = set(
        Color.red(color) / 255f,
        Color.green(color) / 255f,
        Color.blue(color) / 255f,
        Color.alpha(color) / 255f
    )

    fun set(other: Vector, a: Float) = set(other.x, other.y, other.z, a)

    fun set(other: EngineColor) =
        set(other.r, other.g, other.b, other.a)

    fun set(
        prefColor: String,
        min: Float,
        range: Float
    ): EngineColor {
        val parts = prefColor.split(" ")
        if (parts.size >= 3) {
            this.r = parts[0].toFloat() * range + min
            this.g = parts[1].toFloat() * range + min
            this.b = parts[2].toFloat() * range + min
        }
        if (parts.size == 4) {
            this.a = parts[3].toFloat() * range + min
        }
        return this
    }

    fun setToArray(ret: FloatArray?) {
        if (ret != null && ret.size == 4) {
            ret[0] = this.r
            ret[1] = this.g
            ret[2] = this.b
            ret[3] = this.a
        }
    }

    fun blend(
        main: EngineColor,
        blend: EngineColor,
        amount: Float
    ) = set(
        main.r * (1 - amount) + blend.r * amount,
        main.g * (1 - amount) + blend.g * amount,
        main.b * (1 - amount) + blend.b * amount,
        main.a * (1 - amount) + blend.a * amount
    )

}
