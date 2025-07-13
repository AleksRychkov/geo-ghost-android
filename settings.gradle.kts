@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("android-build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "geo-ghost-android"
include(":app")

// core
include(":core:build-config")
include(":core:design-system")
include(":core:model")
include(":core:network")

// feature
include(":feature:bookmark:api")
include(":feature:bookmark:impl")
include(":feature:bookmark:ui")


include(":feature:location-ghost:api")
include(":feature:location-ghost:impl")

include(":feature:location-manager:api")
include(":feature:location-manager:impl")


include(":feature:main")

include(":feature:map:ui")
include(":feature:map:provider:api")
include(":feature:map:provider:libre")

include(":feature:menu:ui")

include(":feature:permission:handler:api")
include(":feature:permission:handler:impl")
include(":feature:permission:ui")

include(":feature:query:city:api")
include(":feature:query:city:impl")
include(":feature:query:city:ui")
