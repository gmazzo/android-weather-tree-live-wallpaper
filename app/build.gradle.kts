import com.android.build.gradle.tasks.PackageAndroidArtifact

plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.dropshots)
    alias(libs.plugins.gitVersion)
    alias(libs.plugins.googlePlayPublish)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.seriazliation)
    alias(libs.plugins.screenshot)
    alias(libs.plugins.hilt)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

val versionTagsCount = gitVersion.provider {
    command(
        "git", "log", "--tags", "--format=oneline", "--simplify-by-decoration",
        "--decorate-refs=refs/tags/${parameters.tagPrefix.getOrElse("")}*"
    )!!.lines().count().toString()
}.map { it.toInt() }

android {
    namespace = "io.github.gmazzo.android.livewallpaper.weather"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        targetSdk = compileSdk
        versionCode = versionTagsCount.get() + 60
        versionName = gitVersion.toString()

        buildConfigField("String", "FORECAST_ENDPOINT", "\"https://api.met.no/weatherapi/\"")
        buildConfigField(
            "String",
            "REVERSE_GEOCODING_ENDPOINT",
            "\"https://api.bigdatacloud.net/data/\""
        )
        buildConfigField(
            "String",
            "REVERSE_GEOCODING_KEY",
            providers.gradleProperty("reverseGeocodingKey").map { "\"$it\"" }.orNull.toString()
        )

        testInstrumentationRunner = "io.github.gmazzo.android.livewallpaper.weather.HiltJUnitRunner"
    }

    providers.gradleProperty("signingPassword").orNull?.takeUnless { it.isBlank() }
        ?.let { signingPassword ->
            buildTypes {
                release {
                    signingConfig = signingConfigs.create("release") {
                        storeFile = file("release.keystore")
                        storePassword = signingPassword
                        keyAlias = "tree-wallpaper"
                        keyPassword = signingPassword
                    }
                }
            }
        }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isEmbedMicroApp
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    testOptions.managedDevices.localDevices.register("emulator") {
        device = "Pixel 6 Pro"
        apiLevel = 33
        systemImageSource = "aosp-atd"
    }

    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

dependencies {
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.hilt.compiler)
    kspAndroidTest(libs.hilt.compiler)

    implementation(platform(libs.androidx.compose))
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.constraintLayout)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.uiToolingPreview)
    debugImplementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.hilt.workManager)
    implementation(libs.androidx.startUp)
    implementation(libs.androidx.tracing)
    implementation(libs.androidx.work)
    implementation(libs.composable.icons)
    implementation(libs.hilt)
    implementation(platform(libs.kotlinx.coroutines))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.solarevents)

    testImplementation(libs.hilt.testing)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    screenshotTestImplementation(libs.androidx.compose.uiTooling)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.espresso)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.work.test)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.hilt.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}

tasks.check {
    dependsOn(
        tasks.withType<PackageAndroidArtifact>(),
        "validateScreenshotTest",
        "emulatorCheck",
    )
}
