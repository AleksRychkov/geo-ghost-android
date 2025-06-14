import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "dev.aleksrychkov.geoghost.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("geoghost-application") {
            id = "geoghost-application"
            implementationClass = "dev.aleksrychkov.geoghost.buildlogic.AndroidApplicationPlugin"
        }
        register("geoghost-android-library") {
            id = "geoghost-android-library"
            implementationClass = "dev.aleksrychkov.geoghost.buildlogic.AndroidLibraryPlugin"
        }
        register("geoghost-library") {
            id = "geoghost-library"
            implementationClass = "dev.aleksrychkov.geoghost.buildlogic.LibraryPlugin"
        }
    }
}
