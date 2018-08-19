package com.hm.weather.engine;

import com.hm.weather.engine.Utility.Logger;

public class AnimPlayer {
    private static final String TAG = "GL Engine";
    private int frameBlend = 0;
    private float frameBlendAmount = 0.0f;
    private int frameCurrent = 0;
    private int frameFirst = 0;
    private int frameLast = 19;
    private float half_frame_time;
    private boolean isLooping = true;
    private boolean isPaused = false;
    private int numFrames = 20;
    private float sDuration = 1.0f;
    private float sTimeElapsed = 0.0f;
    private int timesCompleted = 0;

    public AnimPlayer(int first, int last, float duration, boolean loop) {
        this.frameFirst = first;
        this.frameLast = last;
        this.sDuration = duration;
        this.isLooping = loop;
        this.numFrames = (this.frameLast - this.frameFirst) + 1;
        this.half_frame_time = (this.sDuration / ((float) this.numFrames)) * 0.5f;
        if (this.sDuration <= 0.0f) {
            Logger.v(TAG, "AnimPlayer WARNING: Duration shouldn't be zero, setting to 0.01f");
            this.sDuration = 0.01f;
        }
        reset();
    }

    public int getBlendFrame() {
        return this.frameBlend;
    }

    public float getBlendFrameAmount() {
        return this.frameBlendAmount;
    }

    public int getCount() {
        return this.timesCompleted;
    }

    public int getCurrentFrame() {
        return this.frameCurrent;
    }

    public float getDuration() {
        return this.sDuration;
    }

    public int getFirstFrame() {
        return this.frameFirst;
    }

    public int getLastFrame() {
        return this.frameLast;
    }

    public float getPercentageComplete() {
        return ((float) (this.frameCurrent - this.frameFirst)) / ((float) (this.frameLast - this.frameFirst));
    }

    public void pause() {
        this.isPaused = true;
    }

    int quickRound(float f) {
        if (((double) (f % 1.0f)) < 0.5d) {
            return (int) f;
        }
        return (int) (((double) f) + 0.5d);
    }

    public void randomizeCurrentFrame() {
        this.sTimeElapsed = GlobalRand.floatRange(0.0f, this.sDuration);
    }

    public void reset() {
        this.sTimeElapsed = 0.0f;
        this.timesCompleted = 0;
        this.frameCurrent = this.frameFirst;
        this.frameBlend = this.frameFirst;
        this.frameBlendAmount = 0.0f;
    }

    public void resetCount() {
        this.timesCompleted = 0;
    }

    public void resume() {
        this.isPaused = false;
    }

    public void update(float timeDelta) {
        if (!this.isPaused) {
            this.sTimeElapsed += timeDelta;
            if (this.sTimeElapsed < this.sDuration + this.half_frame_time) {
                float framesPassed = (((float) this.numFrames) * this.sTimeElapsed) / this.sDuration;
                int currentFrame = quickRound(framesPassed);
                this.frameCurrent = this.frameFirst + currentFrame;
                if (this.frameCurrent > this.frameLast) {
                    this.frameCurrent = this.frameLast;
                } else if (this.frameCurrent < this.frameFirst) {
                    this.frameCurrent = this.frameFirst;
                }
                if (framesPassed <= ((float) currentFrame)) {
                    this.frameBlendAmount = ((float) currentFrame) - framesPassed;
                    this.frameBlend = this.frameCurrent - 1;
                    if (this.frameBlend < this.frameFirst) {
                        if (this.isLooping) {
                            this.frameBlend = this.frameLast;
                            return;
                        } else {
                            this.frameBlend = this.frameFirst;
                            return;
                        }
                    } else if (this.frameBlend > this.frameLast) {
                        this.frameBlend = this.frameLast;
                        return;
                    } else {
                        return;
                    }
                }
                this.frameBlendAmount = framesPassed - ((float) currentFrame);
                this.frameBlend = this.frameCurrent + 1;
                if (this.frameBlend <= this.frameLast) {
                    return;
                }
                if (this.isLooping) {
                    this.frameBlend = this.frameFirst;
                } else {
                    this.frameBlend = this.frameLast;
                }
            } else if (this.isLooping) {
                this.sTimeElapsed = (this.sTimeElapsed - this.sDuration) + (this.half_frame_time * 2.0f);
                this.timesCompleted++;
            } else {
                this.frameCurrent = this.frameLast;
                this.timesCompleted = 1;
                this.frameBlend = 0;
                this.frameBlendAmount = 0.0f;
            }
        }
    }
}
