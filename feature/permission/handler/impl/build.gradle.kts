plugins {
    id("build-logic.android-library")
}

android {
    namespace = "dev.aleksrychkov.geoghost.feature.permission.handler"
}

dependencies {
    implementation(projects.feature.permission.handler.api)
}

dependencies {
    implementation(libs.androidx.activity)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.startup)
}
