package dev.aleksrychkov.geoghost.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

private fun Project.versionCatalog(): VersionCatalog =
    extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

private fun VersionCatalog.intVersion(alias: String) =
    findVersion(alias).get().requiredVersion.toInt()

private fun VersionCatalog.version(alias: String) =
    findVersion(alias).get().requiredVersion

fun Project.projectCompileSdk(): Int = versionCatalog().intVersion("compileSdk")
fun Project.projectMinSdk(): Int = versionCatalog().intVersion("minSdk")
fun Project.projectTargetSdk(): Int = versionCatalog().intVersion("targetSdk")
fun Project.projectVersionName(): String = versionCatalog().version("applicationVersionName")
fun Project.projectVersionCode(): Int = versionCatalog().intVersion("applicationVersionCode")

fun Project.setKotlinCompileOptions() {
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(JAVA_VERSION.toString()))
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
            freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }
}
