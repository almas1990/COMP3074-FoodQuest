plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.comp3074.foodquest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.comp3074.foodquest"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1-ui-prototype"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}

