plugins {
    id("geoghost-application")
}

dependencies {
    implementation(projects.core.buildConfig)
    implementation(projects.feature.main)

    // to hook startup initializer
    implementation(projects.feature.bookmark.impl)
    implementation(projects.feature.locationGhost.impl)
    implementation(projects.feature.locationManager.impl)
    implementation(projects.feature.permission.handler.impl)
    implementation(projects.feature.query.city.impl)
}
