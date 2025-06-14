plugins {
    id("geoghost-library")
    alias(libs.plugins.serialization)
}

dependencies {
    implementation(projects.core.model)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
