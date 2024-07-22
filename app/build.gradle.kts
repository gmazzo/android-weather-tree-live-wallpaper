plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.seriazliation)
    alias(libs.plugins.hilt)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

android {
    namespace = "io.github.gmazzo.android.livewallpaper.weather"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34

        versionCode = 1
        versionName = "1.0"

        buildConfigField("boolean", "DEMO_MODE", "DEBUG")
        buildConfigField("String", "FORECAST_ENDPOINT", "\"https://api.met.no/weatherapi/\"")
    }

    buildTypes {
        release {
            signingConfig = signingConfigs["debug"]
        }
    }

    buildFeatures.buildConfig = true
}

kapt.correctErrorTypes = true

dependencies {
    kapt(libs.hilt.compiler)

    implementation(libs.androidx.appcompat)
    implementation(libs.hilt)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.retrofit)

    testImplementation(libs.junit)
}
