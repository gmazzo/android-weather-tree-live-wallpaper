pluginManagement{
    repositories {
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.convention(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven("https://storage.googleapis.com/r8-releases/raw") {
            name = "R8"
            content { includeModule("com.android.tools", "r8") }
        }
    }
}

rootProject.name = "android-weather-tree-live-wallpaper"

includeBuild("testlab-plugin")
include("app")
