package io.github.gmazzo.android.livewallpaper.weather.engine

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange

// TODO make this immutable or replace it with Color
data class EngineColor(
    @param:FloatRange(from = 0.0, to = 1.0) var r: Float = 1f,
    @param:FloatRange(from = 0.0, to = 1.0) var g: Float = 1f,
    @param:FloatRange(from = 0.0, to = 1.0) var b: Float = 1f,
    @param:FloatRange(from = 0.0, to = 1.0) var a: Float = 1f,
) {

    constructor(@ColorInt color: Int) : this(
        Color.red(color) / 255f,
        Color.green(color) / 255f,
        Color.blue(color) / 255f,
        Color.alpha(color) / 255f
    )

    operator fun timesAssign(scale: Float) {
        this.r *= scale
        this.g *= scale
        this.b *= scale
        this.a *= scale
    }

    fun set(
        @FloatRange(from = 0.0, to = 1.0) r: Float,
        @FloatRange(from = 0.0, to = 1.0) g: Float,
        @FloatRange(from = 0.0, to = 1.0) b: Float,
        @FloatRange(from = 0.0, to = 1.0) a: Float
    ): EngineColor {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
        return this
    }

    fun set(@ColorInt color: Int) = set(
        Color.red(color) / 255f,
        Color.green(color) / 255f,
        Color.blue(color) / 255f,
        Color.alpha(color) / 255f
    )

    fun set(other: EngineColor, @FloatRange(from = 0.0, to = 1.0) alpha: Float = other.a) =
        set(other.r, other.g, other.b, alpha)

    fun toArray(into: FloatArray) {
        check(into.size == 4) { "into must be a float[4]" }

        into[0] = this.r
        into[1] = this.g
        into[2] = this.b
        into[3] = this.a
    }

}
