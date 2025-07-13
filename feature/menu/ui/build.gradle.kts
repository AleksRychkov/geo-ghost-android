plugins {
    id("build-logic.android-library")
    alias(libs.plugins.compose)
}

android {
    namespace = "dev.aleksrychkov.geoghost.feature.menu.ui"
    buildFeatures {
        compose = true
    }
}

dependencies {
    // core
    implementation(projects.core.model)
    implementation(projects.core.designSystem)

    // feature
    implementation(projects.feature.bookmark.ui)
    implementation(projects.feature.query.city.ui)
}

dependencies {
    implementation(libs.android.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.lifecycle.viewmodel)
    implementation(libs.androidx.compose.material3)
}
