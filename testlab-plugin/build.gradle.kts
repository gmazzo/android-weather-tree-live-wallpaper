import org.gradle.api.artifacts.type.ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE

plugins {
    alias(libs.plugins.repackager)
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

val repackagedImplementation by configurations.creating

dependencies {
    fun plugin(plugin: Provider<PluginDependency>) =
        plugin.get().run { "$pluginId:$pluginId.gradle.plugin:$version" }

    repackagedImplementation(plugin(libs.plugins.firebase.testlab)) {
        exclude(group = "org.jetbrains.kotlin")
    }
}

dependencyRepackager {
    configuration = repackagedImplementation
    relocations.put("com.google.api", "repackaged.com.google.api")
    remapStrings = true
    removeEmptyDirs = true
}

tasks.jar {
    from(repackagedImplementation.incoming.artifactView {
        attributes {
            attribute(ARTIFACT_TYPE_ATTRIBUTE, "repackaged-jar-${repackagedImplementation.name}")
        }
    }.files.map(::zipTree))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
