plugins {
    id("build-logic.android-library")
}

android {
    namespace = "dev.aleksrychkov.geoghost.feature.map.provider.api"
}

dependencies {
    api(projects.core.model)

    implementation(projects.core.designSystem)
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.ktx)
}
