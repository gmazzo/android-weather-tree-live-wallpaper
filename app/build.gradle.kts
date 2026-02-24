import com.android.build.api.artifact.SingleArtifact
import com.android.build.gradle.internal.tasks.DeviceProviderInstrumentTestTask
import com.android.build.gradle.internal.tasks.ManagedDeviceInstrumentationTestTask
import com.android.build.gradle.internal.tasks.ManagedDeviceTestTask
import com.android.build.gradle.tasks.MergeSourceSetFolders
import com.android.build.gradle.tasks.PackageAndroidArtifact
import com.slack.keeper.optInToKeeper
import org.gradle.internal.impldep.org.joda.time.format.ISODateTimeFormat
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.gitVersion)
    alias(libs.plugins.googlePlayPublish)
    alias(libs.plugins.hilt)
    alias(libs.plugins.keeper)
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
            excludes += "META-INF/**/MANIFEST.MF"
        }
    }
}

keeper.automaticR8RepoManagement = false
androidComponents {
    beforeVariants { if (it.isMinifyEnabled) it.optInToKeeper() }
}

val firebaseTestLabCheck by tasks.registering {
    group = "verification"
    description = "Runs the tests in Firebase Test Lab. Make sure to run 'gcloud auth login' before running this task."
}

val firebaseBucketFolder = providers
    .gradleProperty("firebaseBucketFolder")
    .getOrElse("local-${SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'").format(Date())}")

androidComponents.onVariants { variant ->
    if (variant.androidTest != null) {
        val testLabVariantTask = tasks.register<GCloudFirebaseTest>("${variant.name}FirebaseTestLab") {
            applicationAPK.from(variant.artifacts.get(SingleArtifact.APK))
            testAPK.from(variant.androidTest!!.artifacts.get(SingleArtifact.APK))
            device.set("model=husky,version=34,orientation=portrait") // Pixel 8 Pro (physical)
            resultsBucket.set("test-lab-x50ujfd79y0nq-y4b15w5ykx92s")
            resultsBucketDir.set("firebase-test-lab/$firebaseBucketFolder/${variant.name}")
            resultsLocalDir.set(layout.buildDirectory.dir("test-results/firebase-testlab/${variant.name}"))
        }

        firebaseTestLabCheck {
            dependsOn(testLabVariantTask)
        }
    }
}

dependencies {
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.hilt.compiler)
    ksp(libs.kotlin.metadata)
    kspAndroidTest(libs.hilt.compiler)

    implementation(platform(libs.kotlin.bom))
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
    implementation(platform(libs.kotlin.coroutines))
    implementation(libs.kotlin.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.solarevents)

    testImplementation(libs.hilt.testing)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)

    screenshotTestImplementation(libs.androidx.compose.uiTooling)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.espresso)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.work.test)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.hilt.testing)
    androidTestImplementation(libs.kotlin.coroutines.test)
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
