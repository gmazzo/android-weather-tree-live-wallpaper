package io.github.gmazzo.android.livewallpaper.weather.engine;

public class GlobalTime {
    private static GlobalTime instance = null;
    private static float static_sTimeElapsed = 0.0f;
    final float WORST_FRAMERATE = 3.0f;
    final float WORST_FRAME_TIME = 0.3333333f;
    public long msTimeCurrent;
    private int msTimeDelta;
    private long msTimePrev;
    public float sTimeDelta;
    public float sTimeElapsed;

    public GlobalTime() {
        setInitialValues();
        instance = this;
    }

    public static GlobalTime getInstance() {
        if (instance == null) {
            instance = new GlobalTime();
        }
        return instance;
    }

    private void setInitialValues() {
        this.msTimeCurrent = System.currentTimeMillis();
        this.sTimeElapsed = 0.0f;
        this.msTimePrev = this.msTimeCurrent - 16;
        this.sTimeDelta = ((float) (this.msTimeCurrent - this.msTimePrev)) / 1000.0f;
        this.msTimeDelta = (int) (this.msTimeCurrent - this.msTimePrev);
        static_sTimeElapsed = 0.0f;
    }

    public static float waveCos(float base, float amplitude, float phase, float frequency) {
        return (float) (((double) base) + (((double) amplitude) * Math.cos((double) ((static_sTimeElapsed * frequency) + phase))));
    }

    public static float waveSin(float base, float amplitude, float phase, float frequency) {
        return waveSin(static_sTimeElapsed, base, amplitude, phase, frequency);
    }

    public static float waveSin(float inputTime, float base, float amplitude, float phase, float frequency) {
        return (float) (((double) base) + (((double) amplitude) * Math.sin((double) ((inputTime * frequency) + phase))));
    }

    public void reset() {
        setInitialValues();
    }

    public void updateTime() {
        this.msTimePrev = this.msTimeCurrent;
        this.msTimeCurrent = System.currentTimeMillis();
        this.sTimeDelta = ((float) (this.msTimeCurrent - this.msTimePrev)) / 1000.0f;
        this.msTimeDelta = (int) (this.msTimeCurrent - this.msTimePrev);
        if (this.sTimeDelta > 0.3333333f) {
            this.sTimeDelta = 0.3333333f;
        }
        if (this.sTimeDelta < 0.0f) {
            this.sTimeDelta = 0.0f;
        }
        this.sTimeElapsed += this.sTimeDelta;
        static_sTimeElapsed += this.sTimeDelta;
    }
}
