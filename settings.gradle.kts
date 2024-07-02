pluginManagement{
    repositories {
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.convention(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "android-weather-tree-live-wallpaper"

include("app")
