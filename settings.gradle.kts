pluginManagement{
    repositories {
        gradlePluginPortal()
        google()
        maven("https://maven.pkg.github.com/gmazzo/slackhq-keeper") {
            credentials {
                username = ""
                password = providers.environmentVariable("GITHUB_TOKEN").orNull
            }
            content {
                includeGroup("com.slack.keeper")
            }
        }
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

include("app")
