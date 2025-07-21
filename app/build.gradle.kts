@file:Suppress("UnstableApiUsage")

/**
 * Module-level build configuration for the Android application.
 * Defines:
 * - Android-specific build settings
 * - Dependencies for the application module
 * - Build types and product flavors
 * - Compilation options
 */
plugins {
    // Core Android application plugin
    alias(libs.plugins.android.application)
    // Kotlin Android support
    alias(libs.plugins.kotlin.android)
    // Kotlin Android extensions
    id("kotlin-android")
    // Kotlin annotation processing
    id("kotlin-kapt")
    // ObjectBox database plugin
    id("io.objectbox")
}

/**
 * Android-specific build configuration block.
 * Contains settings for application packaging, compilation, and runtime behavior.
 */
android {
    // Base package name for the application
    namespace = "net.base"
    // Target SDK compilation version
    compileSdk = 36

    /**
     * Default configuration applied to all build variants.
     */
    defaultConfig {
        // Unique application ID (used as the package name on Google Play)
        applicationId = "in.videomate"

        // Minimum Android version your app supports (Android 9 / Pie)
        minSdk = 28

        // Target Android version your app is tested against (Android 14)
        targetSdk = 36

        // Internal version code used for updates on the Play Store
        versionCode = 250802

        // Human-readable version name shown to users
        versionName = "25.08.01"

        // Enable support for vector drawables on older Android versions
        vectorDrawables {
            useSupportLibrary = true
        }

        // Specify supported native CPU architectures (for native libraries)
        ndk {
            abiFilters.add("x86")          // Intel 32-bit (for emulators)
            abiFilters.add("x86_64")       // Intel 64-bit (for emulators)
            abiFilters.add("armeabi-v7a")  // ARM 32-bit (common on older devices)
            abiFilters.add("arm64-v8a")    // ARM 64-bit (modern Android devices)
        }

        // Enable MultiDex support for apps with more than 64K methods
        multiDexEnabled = true
    }

    splits {
        abi {
            isEnable = true           // Enable ABI splits
            reset()                   // Clear previous ABI configurations
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a") // Targeted ABIs
            isUniversalApk = true     // Generate a universal APK that includes all ABIs
        }
    }

    android {
        buildTypes {
            release {
                // Include full native debug symbols in the release build
                // Useful for native crash analysis (e.g., with tools like Firebase Crashlytics)
                ndk {
                    debugSymbolLevel = "FULL" // Options: NONE, SYMBOL_TABLE, FULL
                }
            }
        }

        packaging {
            // Exclude unnecessary license and notice files from the APK
            // Helps reduce APK size and avoid duplicate file conflicts
            resources {
                excludes += setOf("META-INF/LICENSE", "META-INF/NOTICE")
            }

            jniLibs {
                // Use legacy JNI packaging (packs all native .so libraries into the APK as before Android Gradle Plugin 4.1)
                // Required if using libraries or setups that depend on the old structure
                useLegacyPackaging = true
            }
        }

        bundle {
            // Enable screen density splits in the app bundle (AAB)
            // Reduces app size by delivering only the required screen density resources to each device
            density {
                enableSplit = true
            }

            // Enable ABI splits in the app bundle (AAB)
            // Delivers only the required native libraries for each device's CPU architecture
            abi {
                enableSplit = true
            }
        }
    }

    /**
     * Build type configurations.
     */
    buildTypes {
        // Release build configuration
        release {
            // Enable code minification
            isMinifyEnabled = true
            // Enable resource shrinking
            isShrinkResources = true
            // Disable debugging
            isDebuggable = false
            // Proguard rules files
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Control dependency metadata inclusion
    dependenciesInfo {
        includeInBundle = false
        includeInApk = false
    }

    /**
     * Java compilation options.
     */
    compileOptions {
        // Source compatibility level
        sourceCompatibility = JavaVersion.VERSION_17
        // Target compatibility level
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    /**
     * Kotlin compilation options.
     */
    kotlinOptions {
        // Target JVM version
        jvmTarget = "17"
    }

    /**
     * Bundle configuration for Android App Bundles.
     */
    bundle {
        language {
            // Disable language splitting (single APK for all languages)
            enableSplit = false
        }
    }
}

/**
 * Dependency configurations for the application module.
 * All external libraries and modules are declared here.
 */
dependencies {
    // Local jar/lib dependencies
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin and AndroidX core libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines)
    implementation(libs.androidx.lifecycle.process)

    // UI components
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.circle.imageview)
    implementation(libs.circle.progressbar)

    // Utility libraries
    implementation(libs.saf.storage)
    implementation(libs.permissionx)
    implementation(libs.lottie)
    implementation(libs.glide.image.loader)

    // Networking
    implementation(libs.okhttp.connection)
}