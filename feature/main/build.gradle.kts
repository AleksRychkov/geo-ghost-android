plugins {
    id("geoghost-android-library")
    alias(libs.plugins.compose)
}

android {
    namespace = "dev.aleksrychkov.geoghost.feature.main"
    buildFeatures {
        compose = true
    }

    defaultConfig {
        vectorDrawables.useSupportLibrary = true
    }
}

dependencies {
    // core
    implementation(projects.core.buildConfig)
    implementation(projects.core.designSystem)

    // feature
    implementation(projects.feature.map.ui)
}

dependencies {
    implementation(libs.android.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.splashscreen)
}
