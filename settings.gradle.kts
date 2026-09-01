pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // explicit central URL as a fallback for CI environments
        maven { url = uri("https://repo1.maven.org/maven2/") }
    }
}

rootProject.name = "ShareTheWork"
include(":app")
// the Android app module lives under android/app in this repo
project(":app").projectDir = file("android/app")
