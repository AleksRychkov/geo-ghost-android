package dev.aleksrychkov.geoghost.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

abstract class AndroidApplicationPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
        }

        configure<ApplicationExtension> {
            namespace = APPLICATION_ID
            compileSdk = projectCompileSdk()

            setDefaultConfig(
                project = project,
            )
            setBuildType(target)
            setCompilationOptions(
                project = project,
            )
            setBuildFeatures()
        }
    }

    private fun ApplicationExtension.setDefaultConfig(project: Project) {
        defaultConfig {
            applicationId = APPLICATION_ID
            minSdk = project.projectMinSdk()
            targetSdk = project.projectTargetSdk()
            versionCode = project.projectVersionCode()
            versionName = project.projectVersionName()

            testInstrumentationRunner = "androidx.test.runner.AndroidJUintRunner"
        }
    }

    private fun ApplicationExtension.setBuildType(project: Project) {
        signingConfigs {
            create("debug-geo-ghost-android") {
                val keystore =
                    project.layout.projectDirectory.file("geo-ghost-android").asFile
                storeFile = keystore
                storePassword = "geo-ghost-android"
                keyAlias = "geo-ghost-android"
                keyPassword = "geo-ghost-android"
            }
        }
        buildTypes {
            debug {
                applicationIdSuffix = ".debug"
            }
            release {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                signingConfig = signingConfigs.getByName("debug-geo-ghost-android")
            }
        }
    }

    private fun ApplicationExtension.setCompilationOptions(project: Project) {
        compileOptions {
            sourceCompatibility = JAVA_VERSION
            targetCompatibility = JAVA_VERSION
        }
        project.setKotlinCompileOptions()
    }

    private fun ApplicationExtension.setBuildFeatures() {
        buildFeatures {
            buildConfig = true
        }
    }
}
