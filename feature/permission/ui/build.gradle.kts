plugins {
    id("geoghost-android-library")
    alias(libs.plugins.compose)
}

android {
    namespace = "dev.aleksrychkov.geoghost.feature.permission.ui"
    buildFeatures {
        compose = true
    }
}

dependencies {
    // core
    implementation(projects.core.buildConfig)
    implementation(projects.core.designSystem)

    // feature
    implementation(projects.feature.permission.handler.api)
}

dependencies {
    implementation(libs.android.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.lifecycle.viewmodel)
    implementation(libs.androidx.compose.material3)
}
