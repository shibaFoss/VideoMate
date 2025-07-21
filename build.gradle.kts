/**
 * Root project build configuration that defines:
 * - Common plugins applied to all subprojects
 * - Buildscript dependencies for the entire project
 * - Shared version configurations
 */
plugins {
    // Android application plugin (applied per-module)
    alias(libs.plugins.android.application) apply false

    // Kotlin Android plugin (applied per-module)
    alias(libs.plugins.kotlin.android) apply false
}

/**
 * Buildscript configuration for project-level dependencies and repositories.
 * These are available for all modules in the project.
 */
buildscript {
    repositories {
        // Primary repository for project dependencies
        mavenCentral()
    }

    dependencies {
        // Android Gradle Plugin (AGP) for build tools
        classpath(libs.gradle)

        // ObjectBox database Gradle plugin
        classpath(libs.objectbox.gradle.plugin)
    }
}