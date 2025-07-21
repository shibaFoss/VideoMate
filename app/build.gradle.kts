@file:Suppress("UnstableApiUsage")

/**
 * Module-level build configuration for the Android application.
 *
 * This script defines:
 * - Android-specific build settings (e.g., SDK versions, ABI splits)
 * - Compiler options for Java and Kotlin
 * - Build types and ProGuard settings
 * - App bundle and packaging options
 * - Dependency declarations
 */

plugins {
    // Core Android application plugin (required for Android apps)
    alias(libs.plugins.android.application)

    // Kotlin Android plugin for Kotlin support
    alias(libs.plugins.kotlin.android)

    // Kotlin Android Extensions plugin (used for synthetic views and more)
    id("kotlin-android")

    // Kotlin Annotation Processor (needed for libraries like Room or ObjectBox)
    id("kotlin-kapt")

    // ObjectBox plugin for database generation
    id("io.objectbox")
}

android {
    // Base package name used for namespacing
    namespace = "net.base"

    // Target SDK version to compile against
    compileSdk = 36

    /**
     * Default configuration applied to all build variants.
     */
    defaultConfig {
        // Unique application ID (used on Google Play Store)
        applicationId = "in.videomate"

        // Minimum Android version supported by the app
        minSdk = 28

        // Target Android version the app is optimized for
        targetSdk = 36

        // Internal version code for updates
        versionCode = 250802

        // Human-readable version name shown to users
        versionName = "25.08.01"

        // Enables support library for vector drawables
        vectorDrawables {
            useSupportLibrary = true
        }

        // Define supported native CPU architectures for native libraries
        ndk {
            abiFilters.add("x86")          // Intel 32-bit (emulator)
            abiFilters.add("x86_64")       // Intel 64-bit (emulator)
            abiFilters.add("armeabi-v7a")  // ARM 32-bit (older devices)
            abiFilters.add("arm64-v8a")    // ARM 64-bit (modern devices)
        }

        // Enables MultiDex for apps with >64K methods
        multiDexEnabled = true
    }

    /**
     * Configure APK splits for different CPU architectures.
     * This allows Play Store to deliver device-specific APKs, reducing size.
     */
    splits {
        abi {
            isEnable = true                     // Enable ABI splits
            reset()                             // Clear default ABIs
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a") // Target ABIs
            isUniversalApk = true               // Also generate a universal APK
        }
    }

    /**
     * Native build settings specific to release builds.
     */
    buildTypes {
        release {
            // Include full native debug symbols (useful for native crash analysis)
            ndk {
                debugSymbolLevel = "FULL" // Options: NONE, SYMBOL_TABLE, FULL
            }
        }
    }

    /**
     * Controls packaging options during APK/AAB generation.
     */
    packaging {
        resources {
            // Exclude unnecessary resource files from APK
            excludes += setOf("META-INF/LICENSE", "META-INF/NOTICE")
        }

        jniLibs {
            // Use legacy JNI packaging (required for certain older libraries)
            useLegacyPackaging = true
        }
    }

    /**
     * Configure Android App Bundle behavior.
     * This improves delivery efficiency via Google Play Dynamic Delivery.
     */
    bundle {
        // Enable screen density split for optimized asset delivery
        density {
            enableSplit = true
        }

        // Enable ABI split for optimized native libraries per device
        abi {
            enableSplit = true
        }

        // Disable language split (delivers all languages together)
        language {
            enableSplit = false
        }
    }

    /**
     * Defines additional build types and their configurations.
     */
    buildTypes {
        release {
            // Minify code using R8/ProGuard
            isMinifyEnabled = false

            // Shrink unused resources
            isShrinkResources = false

            // Disable debugging for release builds
            isDebuggable = false

            // Apply ProGuard rules for obfuscation and optimization
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    /**
     * Exclude dependency metadata from both APK and AAB to reduce size.
     */
    dependenciesInfo {
        includeInBundle = false
        includeInApk = false
    }

    /**
     * Configure Java compiler options.
     */
    compileOptions {
        // Use Java 17 for source and target compatibility
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    /**
     * Kotlin-specific compiler options.
     */
    kotlin {
        jvmToolchain(17)

        // JVM target version for Kotlin compiler
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

/**
 * Dependency block for managing all libraries and external modules.
 */
dependencies {
    // Include all .jar files inside the libs/ directory
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin and Android core libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines)
    implementation(libs.androidx.lifecycle.process)

    // Android UI libraries
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.circle.imageview)
    implementation(libs.circle.progressbar)
    implementation(libs.androidx.webkit)

    // Utility and helper libraries
    implementation(libs.saf.storage)
    implementation(libs.permissionx)
    implementation(libs.lottie)
    implementation(libs.glide.image.loader)

    // Networking (e.g., OkHttp)
    implementation(libs.okhttp.connection)
    implementation( libs.jsoup)

    //Youtube-dl android wrapper
    implementation(libs.ytdlp.library)
    implementation(libs.ytdlp.ffmpeg)

    //Android native media player
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.datasource)
    implementation(libs.media3.ui)
    implementation(libs.media3.session)

}