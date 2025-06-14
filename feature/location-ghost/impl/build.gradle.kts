plugins {
    id("geoghost-android-library")
}

android {
    namespace = "dev.aleksrychkov.geoghost.feature.locationghost"
}

dependencies {
    implementation(projects.feature.locationGhost.api)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.startup)
}
