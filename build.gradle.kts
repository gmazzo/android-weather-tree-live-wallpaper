plugins {
    alias(libs.plugins.android) apply false
    id("com.google.firebase.testlab") apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.test.aggregation.coverage)
    base
}
