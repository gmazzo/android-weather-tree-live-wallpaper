package io.github.gmazzo.android.livewallpaper.weather.api

import io.github.gmazzo.android.livewallpaper.weather.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.Locale

private object UserAgentInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain) = chain.proceed(
        chain.request().newBuilder()
            .header("Accept-Language", Locale.getDefault().language)
            .header("User-Agent", "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME}-${BuildConfig.VERSION_CODE}")
            .build()
    )

}

private val json by lazy { Json { ignoreUnknownKeys = true } }

val retrofit by lazy {
    Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                .addInterceptor(UserAgentInterceptor)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .baseUrl(BuildConfig.FORECAST_ENDPOINT)
        .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
        .build()
}
