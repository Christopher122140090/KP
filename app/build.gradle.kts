plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.ksp)

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.hadiyarajesh.composetemplate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hadiyarajesh.composetemplate"
        minSdk = 23 // dinaikkan dari 21 ke 23 agar kompatibel dengan Firebase Auth
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.bundles.lifecycle)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose.ui.impl)
    implementation(libs.material3)
    implementation(libs.navigation.compose)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.firebase.database.ktx)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.material.icons.extended) // atau versi Compose kamu

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    implementation(libs.bundles.retrofit)
    implementation(libs.okhttp.interceptor.logging)

    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

    implementation(libs.bundles.coil) {
        because("An image loading library for Android backed by Kotlin Coroutines")
    }

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))

    // Add the dependencies for Firebase products you want to use
    implementation("com.google.firebase:firebase-analytics")
    // Firebase Auth
    implementation("com.google.firebase:firebase-auth-ktx")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.bundles.compose.ui.debug)

    implementation (libs.androidx.material3)
    implementation (libs.androidx.ui)
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.androidx.navigation.compose.v280)
    implementation (libs.androidx.material.icons.extended.v170) // Untuk ikon
    implementation (libs.kotlinx.coroutines.core.v180)
    implementation (libs.androidx.datastore.preferences.v111)
    implementation (libs.navigation.compose)
    implementation (libs.androidx.material.icons.extended)
    implementation (libs.androidx.datastore.preferences)
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation ("androidx.compose:compose-bom:2024.10.00")
    implementation ("androidx.compose.material3:material3")
    implementation ("androidx.compose.ui:ui")
    implementation ("androidx.compose.ui:ui-tooling-preview")
    debugImplementation ("androidx.compose.ui:ui-tooling")
    implementation ("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
}

// Pass options to Room ksp processor
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.generateKotlin", "true")
}
