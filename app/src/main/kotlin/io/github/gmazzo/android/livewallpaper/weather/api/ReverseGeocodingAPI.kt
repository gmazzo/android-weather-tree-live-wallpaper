// Source:
// https://www.bigdatacloud.com/free-api/free-reverse-geocode-to-city-api
// Example API:
//  https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=41.4054054054054&longitude=2.23408&localityLanguage=es
package io.github.gmazzo.android.livewallpaper.weather.api

import io.github.gmazzo.android.livewallpaper.weather.BuildConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeocodingAPI {

    @GET("reverse-geocode")
    suspend fun findCityServer(
        @Query("key") key: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("localityLanguage") language: String? = null,
    ): City

    @GET("reverse-geocode-client")
    suspend fun findCityClient(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("localityLanguage") language: String? = null,
    ): City

    @Serializable
    data class City(
        @SerialName("city") val name: String,
        val locality: String,
        @SerialName("principalSubdivision") val region: String,
        val countryCode: String,
        @SerialName("countryName") val country: String,
        val continent: String,
        val continentCode: String,
    )

    companion object {

        suspend fun ReverseGeocodingAPI.findCity(
            @Query("latitude") latitude: Double,
            @Query("longitude") longitude: Double,
            @Query("localityLanguage") language: String? = null,
        ): City =
            if (BuildConfig.REVERSE_GEOCODING_KEY != null)
                findCityServer(
                    BuildConfig.REVERSE_GEOCODING_KEY,
                    latitude,
                    longitude,
                    language
                )
            else findCityClient(
                latitude,
                longitude,
                language
            )

    }

}
