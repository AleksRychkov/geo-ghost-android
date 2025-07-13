plugins {
    id("build-logic.android-library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "dev.aleksrychkov.geoghost.feature.locationmanager"
}

dependencies {
    // feature
    implementation(projects.feature.locationManager.api)
    implementation(projects.feature.permission.handler.api)
}

dependencies {
    implementation(libs.androidx.startup)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.play.services.location)
}
