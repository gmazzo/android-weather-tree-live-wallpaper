plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
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

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

kapt.correctErrorTypes = true

dependencies {
    kapt(libs.androidx.hilt.compiler)
    kapt(libs.hilt.compiler)
    kaptTest(libs.hilt.compiler)

    implementation(platform(libs.androidx.compose))
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.uiToolingPreview)
    debugImplementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.hilt.workManager)
    implementation(libs.androidx.startUp)
    implementation(libs.androidx.workManager)
    implementation(libs.hilt)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.retrofit)

    testImplementation(libs.hilt.testing)
    testImplementation(libs.junit)
}
