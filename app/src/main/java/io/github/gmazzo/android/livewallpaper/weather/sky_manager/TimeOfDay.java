package io.github.gmazzo.android.livewallpaper.weather.sky_manager;

import android.util.Log;

import java.util.Calendar;

public class TimeOfDay {
    public static final int MINUTES_IN_DAY = 1440;
    public static final int MS_IN_DAY = 86400000;
    private static final String TAG = "GL Engine";
    private float _blendAmount = 0.0f;
    private int _blendIndex = 1;
    private float[] _fakeSunArray = new float[]{-1.0f, 0.0f, 1.0f, 0.0f};
    private boolean _fakeSunPosition = true;
    private float _latitude = 0.0f;
    private float _longitude = 0.0f;
    private int _mainIndex = 0;
    private float _sunPosition = 0.0f;
    private int[] _todTime = new int[4];

    private int deriveMidpoint(int a, int b) {
        int l;
        if (a < b) {
            l = a + ((b - a) / 2);
        } else {
            l = a + (((1440 - a) + b) / 2);
        }
        if (l < 0) {
            l += MINUTES_IN_DAY;
        }
        if (l > MINUTES_IN_DAY) {
            return l - 1440;
        }
        return l;
    }

    private int timeSince(int from, int to) {
        if (from > to) {
            return from - to;
        }
        return (1440 - to) + from;
    }

    private int timeUntil(int from, int to) {
        if (from <= to) {
            return to - from;
        }
        return (1440 - from) + to;
    }

    public void calculateTimeTable(float latitude, float longitude) {
        int minOfSunrise = 360;
        int minOfSunset = 1080;
        if (latitude == 0.0f || longitude == 0.0f) {
            this._fakeSunPosition = true;
        } else {
            Calendar sunrise_time = SkyManager.GetSunrise((double) latitude, (double) longitude);
            Calendar sunset_time = SkyManager.GetSunset((double) latitude, (double) longitude);
            if (sunrise_time != null) {
                minOfSunrise = (sunrise_time.get(11) * 60) + sunrise_time.get(12);
                Log.v(TAG, "sunrise minutes of day is " + minOfSunrise);
            }
            if (sunset_time != null) {
                minOfSunset = (sunset_time.get(11) * 60) + sunset_time.get(12);
                Log.v(TAG, "sunset minutes of day is " + minOfSunset);
            }
            this._fakeSunPosition = false;
        }
        int minOfNoon = deriveMidpoint(minOfSunrise, minOfSunset);
        int minOfMidnight = deriveMidpoint(minOfSunset, minOfSunrise);
        this._todTime[0] = minOfMidnight;
        this._todTime[1] = minOfSunrise;
        this._todTime[2] = minOfNoon;
        this._todTime[3] = minOfSunset;
        this._latitude = latitude;
        this._longitude = longitude;
        Log.v(TAG, "calculateTimeTable @ " + latitude + "r" + longitude + ": " + minOfMidnight + "   " + minOfSunrise + "   " + minOfNoon + "   " + minOfSunset);
    }

    public float getBlendAmount() {
        return this._blendAmount;
    }

    public int getBlendIndex() {
        return this._blendIndex;
    }

    public int getMainIndex() {
        return this._mainIndex;
    }

    public float getSunPosition() {
        return this._sunPosition;
    }

    public void update(int minutes, boolean useSunriseSunsetWeighting) {
        int i;
        int sinceDelta = 999999;
        int sinceIndex = -1;
        for (i = 0; i < this._todTime.length; i++) {
            int since = timeSince(minutes, this._todTime[i]);
            if (since < sinceDelta) {
                sinceDelta = since;
                sinceIndex = i;
            }
        }
        this._mainIndex = sinceIndex;
        int nextDelta = 999999;
        int nextIndex = -1;
        for (i = 0; i < this._todTime.length; i++) {
            int until = timeUntil(minutes, this._todTime[i]);
            if (until < nextDelta) {
                nextIndex = i;
                nextDelta = until;
            }
        }
        this._blendIndex = nextIndex;
        this._blendAmount = ((float) sinceDelta) / ((float) (sinceDelta + nextDelta));
        this._sunPosition = (this._fakeSunArray[this._mainIndex] * (1.0f - this._blendAmount)) + (this._fakeSunArray[this._blendIndex] * this._blendAmount);
        if (!useSunriseSunsetWeighting) {
            return;
        }
        if (this._blendIndex == 1 || this._blendIndex == 3) {
            this._blendAmount -= 0.5f;
            if (this._blendAmount < 0.0f) {
                this._blendAmount = 0.0f;
            }
            this._blendAmount *= 2.0f;
        } else if (this._blendIndex == 0 || this._blendIndex == 2) {
            this._blendAmount *= 2.0f;
            if (this._blendAmount > 1.0f) {
                this._blendAmount = 1.0f;
            }
        }
    }
}
