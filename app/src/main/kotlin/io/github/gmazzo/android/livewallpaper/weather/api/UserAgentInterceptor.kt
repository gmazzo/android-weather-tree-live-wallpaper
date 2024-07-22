package io.github.gmazzo.android.livewallpaper.weather.api

import io.github.gmazzo.android.livewallpaper.weather.BuildConfig
import okhttp3.Interceptor
import java.util.Locale

internal object UserAgentInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain) = chain.proceed(
        chain.request().newBuilder()
            .header("Accept-Language", Locale.getDefault().language)
            .header("User-Agent", "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME}-${BuildConfig.VERSION_CODE}")
            .build()
    )

}
