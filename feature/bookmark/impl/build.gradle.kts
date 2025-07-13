plugins {
    id("build-logic.android-library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "dev.aleksrychkov.geoghost.feature.bookmark"
}

dependencies {
    // core
    implementation(projects.core.model)
    implementation(projects.feature.bookmark.api)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.kotlinx.serialization.json)
}
