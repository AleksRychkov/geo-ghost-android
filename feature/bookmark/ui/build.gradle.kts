plugins {
    id("build-logic.android-library")
    alias(libs.plugins.compose)
}

android {
    namespace = "dev.aleksrychkov.geoghost.bookmark.ui"
    buildFeatures {
        compose = true
    }
}

dependencies {
    // core
    implementation(projects.core.designSystem)
    implementation(projects.core.model)

    // feature
    implementation(projects.feature.bookmark.api)
}

dependencies {
    implementation(libs.android.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.lifecycle.viewmodel)
    implementation(libs.androidx.compose.material3)
}
