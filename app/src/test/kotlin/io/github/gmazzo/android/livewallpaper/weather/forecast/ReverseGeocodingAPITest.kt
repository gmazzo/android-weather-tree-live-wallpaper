package io.github.gmazzo.android.livewallpaper.weather.forecast

import io.github.gmazzo.android.livewallpaper.weather.api.HttpModule
import io.github.gmazzo.android.livewallpaper.weather.api.ReverseGeocodingAPI
import io.github.gmazzo.android.livewallpaper.weather.api.ReverseGeocodingAPI.Companion.findCity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ReverseGeocodingAPITest {

    private val api by lazy { HttpModule.reverseGeocoding(
        HttpModule.retrofit(
            HttpModule.okHttpClient(),
            HttpModule.json()
        )
    ) }

    @Test
    fun `api call returns expected city`() = runTest {
        val response = api.findCity(41.3825, 2.176944, "es")

        assertEquals(ReverseGeocodingAPI.City(
            name = "Barcelona",
            locality = "Barrio Gótico",
            country = "España",
            countryCode = "ES",
            continent = "Europa",
            continentCode = "EU",
            region = "Cataluña"
        ), response)
    }

}
