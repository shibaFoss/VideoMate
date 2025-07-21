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
    // ObjectBox database version (shared across modules)
    val objectboxVersion by extra("4.3.0")

    repositories {
        // Primary repository for project dependencies
        mavenCentral()
    }

    dependencies {
        // Android Gradle Plugin (AGP) for build tools
        classpath("com.android.tools.build:gradle:8.11.1")

        // ObjectBox database Gradle plugin
        classpath("io.objectbox:objectbox-gradle-plugin:$objectboxVersion")
    }
}