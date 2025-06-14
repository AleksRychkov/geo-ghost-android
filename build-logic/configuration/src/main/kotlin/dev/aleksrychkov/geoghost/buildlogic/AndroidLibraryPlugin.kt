package dev.aleksrychkov.geoghost.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

abstract class AndroidLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.library")
            apply("kotlin-android")
        }

        configure<LibraryExtension> {
            compileSdk = projectCompileSdk()

            setDefaultConfig(
                project = project,
            )
            setCompileOptions(
                project = project,
            )
        }
    }

    private fun LibraryExtension.setDefaultConfig(project: Project) {
        defaultConfig {
            minSdk = project.projectMinSdk()

            consumerProguardFiles("consumer-rules.pro")

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    private fun LibraryExtension.setCompileOptions(project: Project) {
        compileOptions {
            sourceCompatibility = JAVA_VERSION
            targetCompatibility = JAVA_VERSION
        }
        project.setKotlinCompileOptions()
    }
}
