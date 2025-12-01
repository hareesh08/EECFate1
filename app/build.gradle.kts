plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("app/keystore/HarDev1.jks")
            storePassword = "HarDev1"
            keyAlias = "HarDev1"
            keyPassword = "HarDev1"
        }
        create("release") {
            storeFile = file("app/keystore/HarDev1.jks")
            storePassword = "HarDev1"
            keyAlias = "HarDev1"
            keyPassword = "HarDev1"
        }
        splits {
            abi {
                isEnable = true
                reset()
                include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
                isUniversalApk = true
            }
        }
    }

    namespace = "com.hd.eecfate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hd.eecfate"
        minSdk = 21
        targetSdk = 35
        versionCode = 8
        versionName = "7.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true

        // Vector drawables support
        vectorDrawables.useSupportLibrary = true

        // Exclude unnecessary native libraries
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
        
        // Optimize rendering
        renderscriptTargetApi = 21
        renderscriptSupportModeEnabled = false
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
            
            // Speed up debug builds
            isDebuggable = true
            isJniDebuggable = false
            isPseudoLocalesEnabled = false
            isCrunchPngs = false
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Maximum release optimizations
            isDebuggable = false
            isJniDebuggable = false
            renderscriptOptimLevel = 3
            isCrunchPngs = true
            
            // Enable aggressive optimizations
            ndk {
                debugSymbolLevel = "NONE"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all",
            "-Xbackend-threads=4"
        )
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/LICENSE*",
                "/META-INF/NOTICE*",
                "/META-INF/DEPENDENCIES",
                "/META-INF/*.kotlin_module",
                "META-INF/INDEX.LIST",
                "META-INF/io.netty.versions.properties"
            )
        }

        jniLibs {
            useLegacyPackaging = false
        }
    }

    // Optimize dex options
    dexOptions {
        preDexLibraries = true
        maxProcessCount = 4
        javaMaxHeapSize = "2g"
    }
    
    // Optimize resource processing
    androidResources {
        noCompress += listOf("txt", "json")
        ignoreAssetsPattern = "!.svn:!.git:.*:!CVS:!thumbs.db:!picasa.ini:!*.scc:*~"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.foundation:foundation:1.6.0")
    implementation("androidx.compose.material3:material3:1.0.0")
    implementation("androidx.compose.material:material:1.4.1")
    implementation("androidx.compose.runtime:runtime:1.6.0")
    implementation("com.google.accompanist:accompanist-pager:0.27.1")
    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("org.conscrypt:conscrypt-android:2.5.2")
    implementation("org.openjsse:openjsse:1.1.0")
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}