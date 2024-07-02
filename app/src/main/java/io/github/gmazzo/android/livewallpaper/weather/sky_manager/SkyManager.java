package io.github.gmazzo.android.livewallpaper.weather.sky_manager;

import android.location.Location;
import android.util.Log;

import io.github.gmazzo.android.livewallpaper.weather.sunrisesunset.SunriseSunsetCalculator;

import java.util.Calendar;
import java.util.TimeZone;

public class SkyManager {
    private static final int MILLISECONDS_PER_DAY = 86400000;
    private static final int MILLISECONDS_PER_HOUR = 3600000;
    private static final String TAG = "GL Engine";
    public static final double ZENITH_ASTRONOMICAL = 108.0d;
    public static final double ZENITH_CIVIL = 96.0d;
    public static final double ZENITH_NAUTICAL = 102.0d;
    public static final double ZENITH_OFFICIAL = 90.833333d;

    /* renamed from: io.github.gmazzo.android.livewallpaper.weather.sky_manager.SkyManager$1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$gs$weather$sky_manager$SkyManager$SunEvent = new int[SunEvent.values().length];

        static {
            try {
                $SwitchMap$gs$weather$sky_manager$SkyManager$SunEvent[SunEvent.SUNRISE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$gs$weather$sky_manager$SkyManager$SunEvent[SunEvent.SUNSET.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private enum SunEvent {
        SUNRISE,
        SUNSET
    }

    private static int DayOfYear() {
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    }

    public static double GetMoonPhase() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        double transformedYear = ((double) year) - Math.floor((double) ((12 - month) / 10));
        int transformedMonth = month + 9;
        if (transformedMonth >= 12) {
            transformedMonth -= 12;
        }
        double term3 = Math.floor(Math.floor((transformedYear / 100.0d) + 49.0d) * 0.75d) - 38.0d;
        double intermediate = ((Math.floor(365.25d * (4712.0d + transformedYear)) + Math.floor((30.6d * ((double) transformedMonth)) + 0.5d)) + ((double) day)) + 59.0d;
        if (intermediate > 2299160.0d) {
            intermediate -= term3;
        }
        double normalizedPhase = (intermediate - 2451550.1d) / 29.530588853d;
        normalizedPhase -= Math.floor(normalizedPhase);
        if (normalizedPhase < 0.0d) {
            return normalizedPhase + 1.0d;
        }
        return normalizedPhase;
    }

    private static Calendar GetSunEvent(SunEvent event, double lat, double lon, int day, double degree) {
        TimeZone tz = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance(tz);
        switch (AnonymousClass1.$SwitchMap$gs$weather$sky_manager$SkyManager$SunEvent[event.ordinal()]) {
            case 1:
                Log.i(TAG, "GetSunriseEvent: tz=" + tz.getDisplayName() + " lat=" + lat + " lon=" + lon);
                return SunriseSunsetCalculator.getSunrise(lat, lon, tz, calendar, degree);
            case 2:
                Log.i(TAG, "GetSunsetEvent: tz=" + tz.getDisplayName() + " lat=" + lat + " lon=" + lon);
                return SunriseSunsetCalculator.getSunset(lat, lon, tz, calendar, degree);
            default:
                return calendar;
        }
    }

    public static float GetSunPosition(double latitude, double longitude) {
        return GetSunPosition(0, latitude, longitude, 90.833333d);
    }

    public static float GetSunPosition(long time, double latitude, double longitude, double zenith) {
        int dayofyear = DayOfYear();
        long todaySunrise_time = GetSunEvent(SunEvent.SUNRISE, latitude, longitude, dayofyear, zenith).getTimeInMillis();
        long todaySunset_time = GetSunEvent(SunEvent.SUNSET, latitude, longitude, dayofyear, zenith).getTimeInMillis();
        if (time == 0) {
            time = Calendar.getInstance().getTimeInMillis();
        }
        if (time < todaySunrise_time) {
            long yesterdaySunset_time = GetSunEvent(SunEvent.SUNSET, latitude, longitude, dayofyear - 1, zenith).getTimeInMillis();
            return (((float) (time - yesterdaySunset_time)) / ((float) (todaySunrise_time - yesterdaySunset_time))) * -1.0f;
        } else if (time < todaySunset_time) {
            return ((float) (time - todaySunrise_time)) / ((float) (todaySunset_time - todaySunrise_time));
        } else {
            return (((float) (time - todaySunset_time)) / ((float) (GetSunEvent(SunEvent.SUNRISE, latitude, longitude, dayofyear + 1, zenith).getTimeInMillis() - todaySunset_time))) * -1.0f;
        }
    }

    public static float GetSunPosition(Location location) {
        return GetSunPosition(0, location.getLatitude(), location.getLongitude(), 90.833333d);
    }

    public static float GetSunPosition(Location location, double d) {
        return GetSunPosition(0, location.getLatitude(), location.getLongitude(), d);
    }

    public static Calendar GetSunrise(double lat, double lon) {
        return GetSunEvent(SunEvent.SUNRISE, lat, lon, DayOfYear(), 0.0d);
    }

    public static Calendar GetSunrise(double lat, double lon, double degree) {
        return GetSunEvent(SunEvent.SUNRISE, lat, lon, DayOfYear(), degree);
    }

    public static Calendar GetSunrise(Location location) {
        return GetSunEvent(SunEvent.SUNRISE, location.getLatitude(), location.getLongitude(), DayOfYear(), 0.0d);
    }

    public static Calendar GetSunrise(Location location, double d) {
        return GetSunEvent(SunEvent.SUNRISE, location.getLatitude(), location.getLongitude(), DayOfYear(), d);
    }

    public static Calendar GetSunset(double d, double d1) {
        return GetSunEvent(SunEvent.SUNSET, d, d1, DayOfYear(), 0.0d);
    }

    public static Calendar GetSunset(double d, double d1, double d2) {
        return GetSunEvent(SunEvent.SUNSET, d, d1, DayOfYear(), d2);
    }

    public static Calendar GetSunset(Location location) {
        return GetSunEvent(SunEvent.SUNSET, location.getLatitude(), location.getLongitude(), DayOfYear(), 0.0d);
    }

    public static Calendar GetSunset(Location location, double d) {
        return GetSunEvent(SunEvent.SUNSET, location.getLatitude(), location.getLongitude(), DayOfYear(), d);
    }

    private static int JulianDay(Calendar calendar) {
        long secs = calendar.getTimeInMillis() / 1000;
        long ss = secs % 60;
        long minutes = (secs - ss) / 60;
        long mm = minutes % 60;
        return (int) (((((secs - ss) - (60 * mm)) - (3600 * (((minutes - mm) / 60) % 24))) / 86400) + 2440588);
    }
}
