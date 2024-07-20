package io.github.gmazzo.android.livewallpaper.weather.sunrisesunset;

import java.util.Calendar;
import java.util.TimeZone;

public final class SunriseSunsetCalculator {

    private SunriseSunsetCalculator() {
    }

    public static Calendar getSunrise(double latitude, double longitude, TimeZone timeZone, Calendar date, double degrees) {
        return new SolarEventCalculator(latitude, longitude, timeZone).computeSunriseCalendar(new Zenith(90.0d - degrees), date);
    }

    public static Calendar getSunset(double latitude, double longitude, TimeZone timeZone, Calendar date, double degrees) {
        return new SolarEventCalculator(latitude, longitude, timeZone).computeSunsetCalendar(new Zenith(90.0d - degrees), date);
    }

}
