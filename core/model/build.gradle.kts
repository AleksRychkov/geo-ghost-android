plugins {
    id("build-logic.library")
    alias(libs.plugins.serialization)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
