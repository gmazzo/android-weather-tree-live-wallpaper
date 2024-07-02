plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.kotlin)
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

        buildConfigField("boolean", "DEMO_MODE", "${false}")
    }

    buildFeatures.buildConfig = true
}

dependencies {
    implementation(libs.androidx.app)
}
