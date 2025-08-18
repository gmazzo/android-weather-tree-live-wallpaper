import com.android.build.gradle.internal.tasks.DeviceProviderInstrumentTestTask
import com.android.build.gradle.internal.tasks.ManagedDeviceInstrumentationTestTask
import com.android.build.gradle.internal.tasks.ManagedDeviceTestTask
import com.android.build.gradle.tasks.MergeSourceSetFolders
import com.android.build.gradle.tasks.PackageAndroidArtifact
import com.android.compose.screenshot.tasks.PreviewScreenshotValidationTask
import com.slack.keeper.optInToKeeper

plugins {
    alias(libs.plugins.android)
    id("com.google.firebase.testlab")
    alias(libs.plugins.gitVersion)
    alias(libs.plugins.googlePlayPublish)
    alias(libs.plugins.hilt)
    alias(libs.plugins.keeper)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.seriazliation)
    alias(libs.plugins.screenshot)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

android {
    namespace = "io.github.gmazzo.android.livewallpaper.weather"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        targetSdk = compileSdk
        versionCode = gitVersion.provider { (tagsCount() + 61).toString() }.get().toInt()
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
        configureEach {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            testProguardFile("proguard-rules-uitests.pro")

            isMinifyEnabled =
                providers.gradleProperty("minified").map(String::toBoolean).getOrElse(!isDebuggable)
            isShrinkResources = isMinifyEnabled
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    testOptions {
        screenshotTests.imageDifferenceThreshold = .01f

        managedDevices.localDevices.register("emulator") {
            device = "Pixel 2"
            apiLevel = 30
            systemImageSource = "aosp-atd"
        }
    }

    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    packaging {
        resources {
            excludes += "META-INF/LICENSE*"
        }
    }
}

keeper.automaticR8RepoManagement = false
androidComponents {
    beforeVariants { it.optInToKeeper() }
}

firebaseTestLab {
    serviceAccountCredentials = providers
        .environmentVariable("GOOGLE_APPLICATION_CREDENTIALS")
        .map(layout.projectDirectory::file)

    managedDevices {
        create("firebaseTestLab") {
            device = "husky" // Pixel 8 Pro (physical)
            apiLevel = 34
        }
    }

    testOptions.results {
        cloudStorageBucket = "weather-live-wallpaper-7b77b.appspot.com"
        directoriesToPull = listOf("/sdcard/Download/")
    }
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
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.work.test)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.hilt.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.bundles.screenshot.validation) {
        isTransitive = false
    }
}

tasks.addRule("collect snapshots", taskName@{
    if (this@taskName == "updateSnapshotTests") {
        val snapshotSources = files()

        tasks.register<Sync>(this@taskName) {
            from(snapshotSources.asFileTree.matching { include("**/screenshots/*.png") }.elements)
            into(layout.projectDirectory.dir("src/androidTest/assets/screenshots"))

            mustRunAfter(tasks.withType<MergeSourceSetFolders>())
        }

        tasks.withType<ManagedDeviceTestTask>().configureEach {
            ignoreFailures = true
            snapshotSources.from(getResultsDir(), getAdditionalTestOutputDir())
            finalizedBy(this@taskName)
        }
        tasks.withType<ManagedDeviceInstrumentationTestTask>().configureEach {
            ignoreFailures = true
            snapshotSources.from(getResultsDir(), getAdditionalTestOutputDir())
            finalizedBy(this@taskName)
        }
        tasks.withType<DeviceProviderInstrumentTestTask>().configureEach {
            ignoreFailures = true
            snapshotSources.from(resultsDir, additionalTestOutputDir)
            finalizedBy(this@taskName)
        }
    }
})

tasks.withType<Test>().matching { "Screenshot" in it.name }.configureEach {
    failOnNoDiscoveredTests = false
}

tasks.check {
    dependsOn(
        tasks.withType<PackageAndroidArtifact>(),
        tasks.validateScreenshotTest,
    )
}
