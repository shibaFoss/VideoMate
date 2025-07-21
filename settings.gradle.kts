// Top-level build settings configuration for the Expensee project
// Configures plugin management and dependency resolution

@file:Suppress("UnstableApiUsage") // Suppresses warnings for experimental Gradle APIs

/**
 * Plugin Management Configuration
 * Defines where to find plugins and which repositories to use
 */
pluginManagement {
    repositories {
        // Google's Maven repository for Android plugins
        google {
            content {
                // Only include Android-related packages to improve resolution speed
                includeGroupByRegex("com\\.android.*")     // Android Gradle plugins
                includeGroupByRegex("com\\.google.*")      // Google libraries
                includeGroupByRegex("androidx.*")          // AndroidX components
            }
        }

        // Standard repositories for plugin resolution
        mavenCentral()          // Main Maven repository
        gradlePluginPortal()    // Gradle's official plugin portal
        maven("https://jitpack.io")  // JitPack for community plugins
    }
}

/**
 * Dependency Resolution Configuration
 * Defines where to find project dependencies
 */
dependencyResolutionManagement {
    // Enforce repository consistency across all modules
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()               // Primary repository for Android libraries
        mavenCentral()         // Main repository for Java/Kotlin libraries
        maven("https://jitpack.io")  // For GitHub-hosted libraries
    }
}

// Project structure configuration
rootProject.name = "VideoMate"  // Sets the root project name
include(":app")                // Includes the main application module