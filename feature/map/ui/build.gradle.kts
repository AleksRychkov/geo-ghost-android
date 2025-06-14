plugins {
    id("geoghost-android-library")
    alias(libs.plugins.compose)
}

android {
    namespace = "dev.aleksrychkov.geoghost.feature.map.ui"
    buildFeatures {
        compose = true
    }
}

dependencies {
    // core
    implementation(projects.core.buildConfig)
    implementation(projects.core.designSystem)
    implementation(projects.core.model)

    // feature
    implementation(projects.feature.bookmark.ui)

    implementation(projects.feature.locationGhost.api)
    implementation(projects.feature.map.provider.libre)

    implementation(projects.feature.menu.ui)

    implementation(projects.feature.permission.handler.api)
    implementation(projects.feature.permission.ui)
}

dependencies {
    implementation(libs.android.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.lifecycle.viewmodel)
    implementation(libs.androidx.compose.material3)
}
