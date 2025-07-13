plugins {
    id("build-logic.android-library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "dev.aleksrychkov.geoghost.core.network"
}

dependencies {
    // core
    implementation(projects.core.buildConfig)
    implementation(projects.core.model)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup)

    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp.client)
    implementation(libs.okhttp.logging.interceptor)
}
