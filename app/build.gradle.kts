plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.hd.eecfate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hd.eecfate"
        minSdk = 21
        targetSdk = 35
        versionCode = 5
        versionName = "5.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom)) // BOM version management for Compose
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.foundation:foundation:1.6.0") // For swipe-to-refresh and other utilities
    implementation("androidx.compose.material3:material3:1.0.0") // Material3 components
    implementation("androidx.compose.runtime:runtime:1.6.0")
    implementation("com.google.accompanist:accompanist-pager:0.27.1")
    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("org.conscrypt:conscrypt-android:2.5.2")
    implementation("org.openjsse:openjsse:1.1.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.material) // Core runtime

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debugging libraries
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
