plugins {
    id("build-logic.android-library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "dev.aleksrychkov.geoghost.feature.query.city"
}

dependencies {
    // core
    implementation(projects.core.buildConfig)
    implementation(projects.core.model)
    implementation(projects.core.network)

    // feature
    implementation(projects.feature.query.city.api)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.startup)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp.client)

    implementation(libs.kotlinx.serialization.json)
}
