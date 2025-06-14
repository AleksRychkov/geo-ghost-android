plugins {
    id("geoghost-library")
    alias(libs.plugins.serialization)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
