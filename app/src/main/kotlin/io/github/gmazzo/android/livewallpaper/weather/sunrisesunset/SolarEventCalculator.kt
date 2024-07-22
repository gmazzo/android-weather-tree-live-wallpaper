package io.github.gmazzo.android.livewallpaper.weather.sunrisesunset

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class SolarEventCalculator(latitude: Double, longitude: Double, private val timeZone: TimeZone) {
    private val latitude = BigDecimal(latitude)
    private val longitude = BigDecimal(longitude)

    constructor(latitude: Double, longitude: Double, timeZoneIdentifier: String?) : this(
        latitude,
        longitude,
        TimeZone.getTimeZone(timeZoneIdentifier)
    )

    fun computeSunriseTime(solarZenith: Zenith, date: Calendar): String {
        return getLocalTimeAsString(computeSolarEventTime(solarZenith, date, true))
    }

    fun computeSunriseCalendar(solarZenith: Zenith, date: Calendar): Calendar? {
        return getLocalTimeAsCalendar(computeSolarEventTime(solarZenith, date, true), date)
    }

    fun computeSunsetTime(solarZenith: Zenith, date: Calendar): String {
        return getLocalTimeAsString(computeSolarEventTime(solarZenith, date, false))
    }

    fun computeSunsetCalendar(solarZenith: Zenith, date: Calendar): Calendar? {
        return getLocalTimeAsCalendar(computeSolarEventTime(solarZenith, date, false), date)
    }

    private fun computeSolarEventTime(
        solarZenith: Zenith,
        date: Calendar,
        isSunrise: Boolean
    ): BigDecimal? {
        date.timeZone = timeZone
        val longitudeHour = getLongitudeHour(date, isSunrise)
        val sunTrueLong = getSunTrueLongitude(getMeanAnomaly(longitudeHour))
        val cosineSunLocalHour = getCosineSunLocalHour(sunTrueLong, solarZenith)
        if (cosineSunLocalHour.toDouble() < -1.0 || cosineSunLocalHour.toDouble() > 1.0) {
            return null
        }
        return getLocalTime(
            getLocalMeanTime(
                sunTrueLong,
                longitudeHour,
                getSunLocalHour(cosineSunLocalHour, isSunrise)
            ), date
        )
    }

    private val baseLongitudeHour: BigDecimal
        get() = divideBy(longitude, BigDecimal.valueOf(15))

    private fun getLongitudeHour(date: Calendar, isSunrise: Boolean): BigDecimal {
        var offset = 18
        if (isSunrise) {
            offset = 6
        }
        return setScale(
            getDayOfYear(date).add(
                divideBy(
                    BigDecimal.valueOf(offset.toLong()).subtract(
                        baseLongitudeHour
                    ), BigDecimal.valueOf(24)
                )
            )
        )
    }

    private fun getMeanAnomaly(longitudeHour: BigDecimal): BigDecimal {
        return setScale(
            multiplyBy(
                BigDecimal("0.9856"),
                longitudeHour
            ).subtract(BigDecimal("3.289"))
        )
    }

    private fun getSunTrueLongitude(meanAnomaly: BigDecimal): BigDecimal {
        var trueLongitude = meanAnomaly.add(
            multiplyBy(
                BigDecimal.valueOf(sin(convertDegreesToRadians(meanAnomaly).toDouble())),
                BigDecimal("1.916")
            )
        ).add(
            multiplyBy(
                BigDecimal.valueOf(
                    sin(
                        multiplyBy(
                            convertDegreesToRadians(meanAnomaly),
                            BigDecimal.valueOf(2)
                        ).toDouble()
                    )
                ), BigDecimal("0.020")
            ).add(BigDecimal("282.634"))
        )
        if (trueLongitude.toDouble() > 360.0) {
            trueLongitude = trueLongitude.subtract(BigDecimal.valueOf(360))
        }
        return setScale(trueLongitude)
    }

    private fun getRightAscension(sunTrueLong: BigDecimal): BigDecimal {
        var rightAscension = setScale(
            convertRadiansToDegrees(
                BigDecimal.valueOf(
                    atan(
                        convertDegreesToRadians(
                            multiplyBy(
                                convertRadiansToDegrees(
                                    BigDecimal.valueOf(tan(convertDegreesToRadians(sunTrueLong).toDouble()))
                                ), BigDecimal("0.91764")
                            )
                        ).toDouble()
                    )
                )
            )
        )
        if (rightAscension.toDouble() < 0.0) {
            rightAscension = rightAscension.add(BigDecimal.valueOf(360))
        } else if (rightAscension.toDouble() > 360.0) {
            rightAscension = rightAscension.subtract(BigDecimal.valueOf(360))
        }
        val ninety = BigDecimal.valueOf(90)
        return divideBy(
            rightAscension.add(
                sunTrueLong.divide(ninety, 0, RoundingMode.FLOOR).multiply(ninety)
                    .subtract(rightAscension.divide(ninety, 0, RoundingMode.FLOOR).multiply(ninety))
            ), BigDecimal.valueOf(15)
        )
    }

    private fun getCosineSunLocalHour(sunTrueLong: BigDecimal, zenith: Zenith): BigDecimal {
        val sinSunDeclination = getSinOfSunDeclination(sunTrueLong)
        val cosineSunDeclination = getCosineOfSunDeclination(sinSunDeclination)
        return setScale(
            divideBy(
                BigDecimal.valueOf(cos(convertDegreesToRadians(zenith.degrees()).toDouble()))
                    .subtract(
                        sinSunDeclination.multiply(
                            BigDecimal.valueOf(sin(convertDegreesToRadians(latitude).toDouble()))
                        )
                    ), cosineSunDeclination.multiply(
                    BigDecimal.valueOf(cos(convertDegreesToRadians(latitude).toDouble()))
                )
            )
        )
    }

    private fun getSinOfSunDeclination(sunTrueLong: BigDecimal): BigDecimal {
        return setScale(
            BigDecimal.valueOf(sin(convertDegreesToRadians(sunTrueLong).toDouble())).multiply(
                BigDecimal("0.39782")
            )
        )
    }

    private fun getCosineOfSunDeclination(sinSunDeclination: BigDecimal): BigDecimal {
        return setScale(
            BigDecimal.valueOf(
                cos(
                    BigDecimal.valueOf(asin(sinSunDeclination.toDouble())).toDouble()
                )
            )
        )
    }

    private fun getSunLocalHour(cosineSunLocalHour: BigDecimal, isSunrise: Boolean): BigDecimal {
        var localHour = convertRadiansToDegrees(getArcCosineFor(cosineSunLocalHour))
        if (isSunrise) {
            localHour = BigDecimal.valueOf(360).subtract(localHour)
        }
        return divideBy(localHour, BigDecimal.valueOf(15))
    }

    private fun getLocalMeanTime(
        sunTrueLong: BigDecimal,
        longitudeHour: BigDecimal,
        sunLocalHour: BigDecimal
    ): BigDecimal {
        val rightAscension = getRightAscension(sunTrueLong)
        var localMeanTime = sunLocalHour.add(rightAscension).subtract(
            longitudeHour.multiply(
                BigDecimal("0.06571")
            )
        ).subtract(BigDecimal("6.622"))
        if (localMeanTime.toDouble() < 0.0) {
            localMeanTime = localMeanTime.add(BigDecimal.valueOf(24))
        } else if (localMeanTime.toDouble() > 24.0) {
            localMeanTime = localMeanTime.subtract(BigDecimal.valueOf(24))
        }
        return setScale(localMeanTime)
    }

    private fun getLocalTime(localMeanTime: BigDecimal, date: Calendar): BigDecimal {
        return adjustForDST(localMeanTime.subtract(baseLongitudeHour).add(getUTCOffSet(date)), date)
    }

    private fun adjustForDST(localMeanTime: BigDecimal, date: Calendar): BigDecimal {
        var localTime = localMeanTime
        if (timeZone.inDaylightTime(date.time)) {
            localTime = localTime.add(BigDecimal.ONE)
        }
        if (localTime.toDouble() > 24.0) {
            return localTime.subtract(BigDecimal.valueOf(24))
        }
        return localTime
    }

    private fun getLocalTimeAsString(localTimeParam: BigDecimal?): String {
        if (localTimeParam == null) {
            return "99:99"
        }
        var localTime: BigDecimal = localTimeParam
        if (localTime.compareTo(BigDecimal.ZERO) == -1) {
            localTime = localTime.add(BigDecimal.valueOf(24.0))
        }
        val timeComponents =
            localTime.toPlainString().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        var hour = timeComponents[0].toInt()
        var minutes = BigDecimal("0." + timeComponents[1]).multiply(BigDecimal.valueOf(60))
            .setScale(0, RoundingMode.HALF_EVEN)
        if (minutes.toInt() == 60) {
            minutes = BigDecimal.ZERO
            hour++
        }
        if (hour == 24) {
            hour = 0
        }
        return (if (hour < 10) "0$hour" else hour.toString()) + ":" + (if (minutes.toInt() < 10) "0" + minutes.toPlainString() else minutes.toPlainString())
    }

    protected fun getLocalTimeAsCalendar(localTimeParam: BigDecimal?, date: Calendar): Calendar? {
        if (localTimeParam == null) {
            return null
        }
        val resultTime = date.clone() as Calendar
        var localTime: BigDecimal = localTimeParam
        if (localTime.compareTo(BigDecimal.ZERO) == -1) {
            localTime = localTime.add(BigDecimal.valueOf(24.0))
            resultTime.add(11, -24)
        }
        val timeComponents =
            localTime.toPlainString().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        var hour = timeComponents[0].toInt()
        var minutes = BigDecimal("0." + timeComponents[1]).multiply(BigDecimal.valueOf(60))
            .setScale(0, RoundingMode.HALF_EVEN)
        if (minutes.toInt() == 60) {
            minutes = BigDecimal.ZERO
            hour++
        }
        if (hour == 24) {
            hour = 0
        }
        resultTime[11] = hour
        resultTime[12] = minutes.toInt()
        resultTime[13] = 0
        resultTime.timeZone = date.timeZone
        return resultTime
    }

    private fun getDayOfYear(date: Calendar): BigDecimal {
        return BigDecimal(date[6])
    }

    private fun getUTCOffSet(date: Calendar): BigDecimal {
        return BigDecimal(date[15] / 3600000).setScale(0, RoundingMode.HALF_EVEN)
    }

    private fun getArcCosineFor(radians: BigDecimal): BigDecimal {
        return setScale(BigDecimal.valueOf(acos(radians.toDouble())))
    }

    private fun convertRadiansToDegrees(radians: BigDecimal): BigDecimal {
        return multiplyBy(radians, BigDecimal("57.29577951308232"))
    }

    private fun convertDegreesToRadians(degrees: BigDecimal?): BigDecimal {
        return multiplyBy(degrees, BigDecimal.valueOf(0.017453292519943295))
    }

    private fun multiplyBy(multiplicand: BigDecimal?, multiplier: BigDecimal): BigDecimal {
        return setScale(multiplicand!!.multiply(multiplier))
    }

    private fun divideBy(dividend: BigDecimal, divisor: BigDecimal): BigDecimal {
        return dividend.divide(divisor, 4, RoundingMode.HALF_EVEN)
    }

    private fun setScale(number: BigDecimal): BigDecimal {
        return number.setScale(4, RoundingMode.HALF_EVEN)
    }
}
