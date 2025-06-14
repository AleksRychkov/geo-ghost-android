plugins {
    id("geoghost-android-library")
}

android {
    namespace = "dev.aleksrychkov.geoghost.feature.map.provider.libre"
}

dependencies {
    // core
    implementation(projects.core.designSystem)

    // feature
    api(projects.feature.map.provider.api)
    implementation(projects.feature.locationManager.api)
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.startup)

    implementation(libs.maplibre)
}
