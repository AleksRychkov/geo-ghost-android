plugins {
    id("geoghost-library")
}

dependencies {
    api(projects.core.model)
}

dependencies {
    api(libs.kotlinx.coroutines.core)
}
