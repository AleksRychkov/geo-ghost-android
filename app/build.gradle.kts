plugins {
    id("build-logic.application")
}

android {
    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("debug-geo-ghost-android") {
            val keystore =
                project.layout.projectDirectory.file("geo-ghost-android").asFile
            storeFile = keystore
            storePassword = "geo-ghost-android"
            keyAlias = "geo-ghost-android"
            keyPassword = "geo-ghost-android"
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug-geo-ghost-android")
        }
    }
}

dependencies {
    implementation(projects.core.buildConfig)
    implementation(projects.feature.main)

    // to hook startup initializer
    implementation(projects.feature.bookmark.impl)
    implementation(projects.feature.locationGhost.impl)
    implementation(projects.feature.locationManager.impl)
    implementation(projects.feature.permission.handler.impl)
    implementation(projects.feature.query.city.impl)
}
