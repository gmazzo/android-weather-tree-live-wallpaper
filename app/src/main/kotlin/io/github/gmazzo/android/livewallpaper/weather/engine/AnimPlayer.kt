package io.github.gmazzo.android.livewallpaper.weather.engine

import android.util.Log

class AnimPlayer(first: Int, last: Int, duration: Float, loop: Boolean) {
    var blendFrame: Int = 0
        private set
    var blendFrameAmount: Float = 0.0f
        private set
    var currentFrame: Int = 0
        private set
    var firstFrame: Int = first
        private set
    var lastFrame: Int = last
        private set
    private var halfFrameTime = 0f
    private var isLooping = loop
    private var isPaused = false
    private var numFrames = 20
    var duration: Float = duration
        private set
    private var sTimeElapsed = 0.0f
    var count: Int = 0
        private set

    init {
        this.numFrames = (this.lastFrame - this.firstFrame) + 1
        this.halfFrameTime = (this.duration / (numFrames.toFloat())) * 0.5f
        if (this.duration <= 0.0f) {
            Log.v(
                TAG,
                "AnimPlayer WARNING: Duration shouldn't be zero, setting to 0.01f"
            )
            this.duration = 0.01f
        }
        reset()
    }

    val percentageComplete: Float
        get() = ((this.currentFrame - this.firstFrame).toFloat()) / ((this.lastFrame - this.firstFrame).toFloat())

    fun pause() {
        this.isPaused = true
    }

    fun quickRound(f: Float): Int {
        if (((f % 1.0f).toDouble()) < 0.5) {
            return f.toInt()
        }
        return ((f.toDouble()) + 0.5).toInt()
    }

    fun reset() {
        this.sTimeElapsed = 0.0f
        this.count = 0
        this.currentFrame = this.firstFrame
        this.blendFrame = this.firstFrame
        this.blendFrameAmount = 0.0f
    }

    fun resetCount() {
        this.count = 0
    }

    fun resume() {
        this.isPaused = false
    }

    fun update(timeDelta: Float) {
        if (!this.isPaused) {
            this.sTimeElapsed += timeDelta
            if (this.sTimeElapsed < this.duration + this.halfFrameTime) {
                val framesPassed = ((numFrames.toFloat()) * this.sTimeElapsed) / this.duration
                val currentFrame = quickRound(framesPassed)
                this.currentFrame = this.firstFrame + currentFrame
                if (this.currentFrame > this.lastFrame) {
                    this.currentFrame = this.lastFrame
                } else if (this.currentFrame < this.firstFrame) {
                    this.currentFrame = this.firstFrame
                }
                if (framesPassed <= (currentFrame.toFloat())) {
                    this.blendFrameAmount = (currentFrame.toFloat()) - framesPassed
                    this.blendFrame = this.currentFrame - 1
                    if (this.blendFrame < this.firstFrame) {
                        if (this.isLooping) {
                            this.blendFrame = this.lastFrame
                            return
                        } else {
                            this.blendFrame = this.firstFrame
                            return
                        }
                    } else if (this.blendFrame > this.lastFrame) {
                        this.blendFrame = this.lastFrame
                        return
                    } else {
                        return
                    }
                }
                this.blendFrameAmount = framesPassed - (currentFrame.toFloat())
                this.blendFrame = this.currentFrame + 1
                if (this.blendFrame <= this.lastFrame) {
                    return
                }
                if (this.isLooping) {
                    this.blendFrame = this.firstFrame
                } else {
                    this.blendFrame = this.lastFrame
                }
            } else if (this.isLooping) {
                this.sTimeElapsed =
                    (this.sTimeElapsed - this.duration) + (this.halfFrameTime * 2.0f)
                count++
            } else {
                this.currentFrame = this.lastFrame
                this.count = 1
                this.blendFrame = 0
                this.blendFrameAmount = 0.0f
            }
        }
    }

    companion object {
        private const val TAG = "GL Engine"
    }
}
