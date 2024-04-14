plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.kotlin)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

android {
    namespace = "gs.weather"
    compileSdk = 34

    defaultConfig {
        minSdk = 14
        targetSdk = 33

        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures.buildConfig = true
}

dependencies {
    implementation(libs.androidx.app)
    implementation(libs.androidx.constraintLayout)
    implementation(libs.androidx.core)
}
