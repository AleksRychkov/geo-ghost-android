plugins {
    id("build-logic.library")
}

dependencies {
    api(projects.core.model)
}

dependencies {
    api(libs.kotlinx.coroutines.core)
}
