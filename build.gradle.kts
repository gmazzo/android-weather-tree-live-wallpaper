buildscript {
    configurations.classpath {
        /**
         * TODO forced version to allow both plugins to work together
         * `googlePlayPublish` uses latest version of `google-api-client:2.+`
         * `firebase.testlab` is using very old google apis sdk, which required up to `google-api-client:1.x`
         */
        resolutionStrategy.force("com.google.api-client:google-api-client:1.22.0")
    }
}

plugins {
    alias(libs.plugins.android) apply false
    alias(libs.plugins.firebase.testlab) apply false
    alias(libs.plugins.googlePlayPublish) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.test.aggregation.coverage)
    base
}

// ensures Git LFS is installed
providers.exec { commandLine("git", "lfs", "install") }.result.get().assertNormalExitValue()
