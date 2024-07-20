package io.github.gmazzo.android.livewallpaper.weather.sunrisesunset;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.TimeZone;

public class SolarEventCalculator {
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final TimeZone timeZone;

    public SolarEventCalculator(double latitude, double longitude, String timeZoneIdentifier) {
        this(latitude, longitude, TimeZone.getTimeZone(timeZoneIdentifier));
    }

    public SolarEventCalculator(double latitude, double longitude, TimeZone timeZone) {
        this.latitude = new BigDecimal(latitude);
        this.longitude = new BigDecimal(longitude);
        this.timeZone = timeZone;
    }

    public String computeSunriseTime(Zenith solarZenith, Calendar date) {
        return getLocalTimeAsString(computeSolarEventTime(solarZenith, date, true));
    }

    public Calendar computeSunriseCalendar(Zenith solarZenith, Calendar date) {
        return getLocalTimeAsCalendar(computeSolarEventTime(solarZenith, date, true), date);
    }

    public String computeSunsetTime(Zenith solarZenith, Calendar date) {
        return getLocalTimeAsString(computeSolarEventTime(solarZenith, date, false));
    }

    public Calendar computeSunsetCalendar(Zenith solarZenith, Calendar date) {
        return getLocalTimeAsCalendar(computeSolarEventTime(solarZenith, date, false), date);
    }

    private BigDecimal computeSolarEventTime(Zenith solarZenith, Calendar date, boolean isSunrise) {
        date.setTimeZone(this.timeZone);
        BigDecimal longitudeHour = getLongitudeHour(date, Boolean.valueOf(isSunrise));
        BigDecimal sunTrueLong = getSunTrueLongitude(getMeanAnomaly(longitudeHour));
        BigDecimal cosineSunLocalHour = getCosineSunLocalHour(sunTrueLong, solarZenith);
        if (cosineSunLocalHour.doubleValue() < -1.0d || cosineSunLocalHour.doubleValue() > 1.0d) {
            return null;
        }
        return getLocalTime(getLocalMeanTime(sunTrueLong, longitudeHour, getSunLocalHour(cosineSunLocalHour, Boolean.valueOf(isSunrise))), date);
    }

    private BigDecimal getBaseLongitudeHour() {
        return divideBy(longitude, BigDecimal.valueOf(15));
    }

    private BigDecimal getLongitudeHour(Calendar date, Boolean isSunrise) {
        int offset = 18;
        if (isSunrise.booleanValue()) {
            offset = 6;
        }
        return setScale(getDayOfYear(date).add(divideBy(BigDecimal.valueOf(offset).subtract(getBaseLongitudeHour()), BigDecimal.valueOf(24))));
    }

    private BigDecimal getMeanAnomaly(BigDecimal longitudeHour) {
        return setScale(multiplyBy(new BigDecimal("0.9856"), longitudeHour).subtract(new BigDecimal("3.289")));
    }

    private BigDecimal getSunTrueLongitude(BigDecimal meanAnomaly) {
        BigDecimal trueLongitude = meanAnomaly.add(multiplyBy(BigDecimal.valueOf(Math.sin(convertDegreesToRadians(meanAnomaly).doubleValue())), new BigDecimal("1.916"))).add(multiplyBy(BigDecimal.valueOf(Math.sin(multiplyBy(convertDegreesToRadians(meanAnomaly), BigDecimal.valueOf(2)).doubleValue())), new BigDecimal("0.020")).add(new BigDecimal("282.634")));
        if (trueLongitude.doubleValue() > 360.0d) {
            trueLongitude = trueLongitude.subtract(BigDecimal.valueOf(360));
        }
        return setScale(trueLongitude);
    }

    private BigDecimal getRightAscension(BigDecimal sunTrueLong) {
        BigDecimal rightAscension = setScale(convertRadiansToDegrees(BigDecimal.valueOf(Math.atan(convertDegreesToRadians(multiplyBy(convertRadiansToDegrees(BigDecimal.valueOf(Math.tan(convertDegreesToRadians(sunTrueLong).doubleValue()))), new BigDecimal("0.91764"))).doubleValue()))));
        if (rightAscension.doubleValue() < 0.0d) {
            rightAscension = rightAscension.add(BigDecimal.valueOf(360));
        } else if (rightAscension.doubleValue() > 360.0d) {
            rightAscension = rightAscension.subtract(BigDecimal.valueOf(360));
        }
        BigDecimal ninety = BigDecimal.valueOf(90);
        return divideBy(rightAscension.add(sunTrueLong.divide(ninety, 0, RoundingMode.FLOOR).multiply(ninety).subtract(rightAscension.divide(ninety, 0, RoundingMode.FLOOR).multiply(ninety))), BigDecimal.valueOf(15));
    }

    private BigDecimal getCosineSunLocalHour(BigDecimal sunTrueLong, Zenith zenith) {
        BigDecimal sinSunDeclination = getSinOfSunDeclination(sunTrueLong);
        BigDecimal cosineSunDeclination = getCosineOfSunDeclination(sinSunDeclination);
        return setScale(divideBy(BigDecimal.valueOf(Math.cos(convertDegreesToRadians(zenith.degrees()).doubleValue())).subtract(sinSunDeclination.multiply(BigDecimal.valueOf(Math.sin(convertDegreesToRadians(latitude).doubleValue())))), cosineSunDeclination.multiply(BigDecimal.valueOf(Math.cos(convertDegreesToRadians(latitude).doubleValue())))));
    }

    private BigDecimal getSinOfSunDeclination(BigDecimal sunTrueLong) {
        return setScale(BigDecimal.valueOf(Math.sin(convertDegreesToRadians(sunTrueLong).doubleValue())).multiply(new BigDecimal("0.39782")));
    }

    private BigDecimal getCosineOfSunDeclination(BigDecimal sinSunDeclination) {
        return setScale(BigDecimal.valueOf(Math.cos(BigDecimal.valueOf(Math.asin(sinSunDeclination.doubleValue())).doubleValue())));
    }

    private BigDecimal getSunLocalHour(BigDecimal cosineSunLocalHour, Boolean isSunrise) {
        BigDecimal localHour = convertRadiansToDegrees(getArcCosineFor(cosineSunLocalHour));
        if (isSunrise.booleanValue()) {
            localHour = BigDecimal.valueOf(360).subtract(localHour);
        }
        return divideBy(localHour, BigDecimal.valueOf(15));
    }

    private BigDecimal getLocalMeanTime(BigDecimal sunTrueLong, BigDecimal longitudeHour, BigDecimal sunLocalHour) {
        BigDecimal rightAscension = getRightAscension(sunTrueLong);
        BigDecimal localMeanTime = sunLocalHour.add(rightAscension).subtract(longitudeHour.multiply(new BigDecimal("0.06571"))).subtract(new BigDecimal("6.622"));
        if (localMeanTime.doubleValue() < 0.0d) {
            localMeanTime = localMeanTime.add(BigDecimal.valueOf(24));
        } else if (localMeanTime.doubleValue() > 24.0d) {
            localMeanTime = localMeanTime.subtract(BigDecimal.valueOf(24));
        }
        return setScale(localMeanTime);
    }

    private BigDecimal getLocalTime(BigDecimal localMeanTime, Calendar date) {
        return adjustForDST(localMeanTime.subtract(getBaseLongitudeHour()).add(getUTCOffSet(date)), date);
    }

    private BigDecimal adjustForDST(BigDecimal localMeanTime, Calendar date) {
        BigDecimal localTime = localMeanTime;
        if (this.timeZone.inDaylightTime(date.getTime())) {
            localTime = localTime.add(BigDecimal.ONE);
        }
        if (localTime.doubleValue() > 24.0d) {
            return localTime.subtract(BigDecimal.valueOf(24));
        }
        return localTime;
    }

    private String getLocalTimeAsString(BigDecimal localTimeParam) {
        if (localTimeParam == null) {
            return "99:99";
        }
        BigDecimal localTime = localTimeParam;
        if (localTime.compareTo(BigDecimal.ZERO) == -1) {
            localTime = localTime.add(BigDecimal.valueOf(24.0d));
        }
        String[] timeComponents = localTime.toPlainString().split("\\.");
        int hour = Integer.parseInt(timeComponents[0]);
        BigDecimal minutes = new BigDecimal("0." + timeComponents[1]).multiply(BigDecimal.valueOf(60)).setScale(0, RoundingMode.HALF_EVEN);
        if (minutes.intValue() == 60) {
            minutes = BigDecimal.ZERO;
            hour++;
        }
        if (hour == 24) {
            hour = 0;
        }
        return (hour < 10 ? "0" + hour : String.valueOf(hour)) + ":" + (minutes.intValue() < 10 ? "0" + minutes.toPlainString() : minutes.toPlainString());
    }

    protected Calendar getLocalTimeAsCalendar(BigDecimal localTimeParam, Calendar date) {
        if (localTimeParam == null) {
            return null;
        }
        Calendar resultTime = (Calendar) date.clone();
        BigDecimal localTime = localTimeParam;
        if (localTime.compareTo(BigDecimal.ZERO) == -1) {
            localTime = localTime.add(BigDecimal.valueOf(24.0d));
            resultTime.add(11, -24);
        }
        String[] timeComponents = localTime.toPlainString().split("\\.");
        int hour = Integer.parseInt(timeComponents[0]);
        BigDecimal minutes = new BigDecimal("0." + timeComponents[1]).multiply(BigDecimal.valueOf(60)).setScale(0, RoundingMode.HALF_EVEN);
        if (minutes.intValue() == 60) {
            minutes = BigDecimal.ZERO;
            hour++;
        }
        if (hour == 24) {
            hour = 0;
        }
        resultTime.set(11, hour);
        resultTime.set(12, minutes.intValue());
        resultTime.set(13, 0);
        resultTime.setTimeZone(date.getTimeZone());
        return resultTime;
    }

    private BigDecimal getDayOfYear(Calendar date) {
        return new BigDecimal(date.get(6));
    }

    private BigDecimal getUTCOffSet(Calendar date) {
        return new BigDecimal(date.get(15) / 3600000).setScale(0, RoundingMode.HALF_EVEN);
    }

    private BigDecimal getArcCosineFor(BigDecimal radians) {
        return setScale(BigDecimal.valueOf(Math.acos(radians.doubleValue())));
    }

    private BigDecimal convertRadiansToDegrees(BigDecimal radians) {
        return multiplyBy(radians, new BigDecimal("57.29577951308232"));
    }

    private BigDecimal convertDegreesToRadians(BigDecimal degrees) {
        return multiplyBy(degrees, BigDecimal.valueOf(0.017453292519943295d));
    }

    private BigDecimal multiplyBy(BigDecimal multiplicand, BigDecimal multiplier) {
        return setScale(multiplicand.multiply(multiplier));
    }

    private BigDecimal divideBy(BigDecimal dividend, BigDecimal divisor) {
        return dividend.divide(divisor, 4, RoundingMode.HALF_EVEN);
    }

    private BigDecimal setScale(BigDecimal number) {
        return number.setScale(4, RoundingMode.HALF_EVEN);
    }
}
