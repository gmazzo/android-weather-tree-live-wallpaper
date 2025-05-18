plugins {
    alias(libs.plugins.embeddedDependencies)
    `java-gradle-plugin`
}

/**
 * FIXME this is an ugly workaround to overcome the incompatibility between the two plugins
 *  interacting with Google APIs:
 *  - `com.github.triplet.play`, for publishing to Play Store
 *  - `com.google.firebase.testlab`, for running tests on Firebase Test Lab
 *  The second is still in alpha and using a very old dependencies set of Google APIs.
 *  Having both in the classpath breaks one or the other
 *  We are repackaging the `com.google.firebase.testlab` plugin to avoid the conflict
 */

gradlePlugin {
    plugins.create("testLabRepackaged") {
        id = "com.google.firebase.testlab"
        implementationClass = "com.android.tools.firebase.testlab.gradle.TestLabGradlePlugin"
    }
}

dependencies {
    fun plugin(plugin: Provider<PluginDependency>) =
        plugin.get().run { "$pluginId:$pluginId.gradle.plugin:$version" }

    embedded(plugin(libs.plugins.firebase.testlab)) {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "com.google.guava")
    }
}

configurations.embedded.embedding {
    exclude("META-INF/gradle-plugins/com.google.firebase.testlab.properties")
    repackage("com.google.api", "repackaged.com.google.api")
    repackage("com.google.testing", "repackaged.com.google.testing")
}
