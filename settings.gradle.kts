//settings.gradle.kts (Project Settings)
pluginManagement {

    includeBuild("build-logic")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // External plugins can be resolved in dependencies section
    }
    plugins {
        id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "gbr"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS") // Enable usage like implementation(projects.core.designsystem) in gradle

include(":app")
include(":core:designsystem")
include(":core:common")
include(":core:data")
include(":core:network")
include(":core:model")
