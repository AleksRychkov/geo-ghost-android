plugins {
    id("geoghost-android-library")
    alias(libs.plugins.compose)
}

android {
    namespace = "dev.aleksrychkov.geoghost.feature.query.city.ui"
    buildFeatures {
        compose = true
    }
}

dependencies {
    // core
    implementation(projects.core.designSystem)

    // feature
    implementation(projects.feature.query.city.api)
}

dependencies {
    implementation(libs.android.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.lifecycle.viewmodel)
    implementation(libs.androidx.compose.material3)
}
