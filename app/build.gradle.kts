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
    namespace = "com.hadiyarajesh.admin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rosaliscagroup.admin"
        minSdk = 23
        // dinaikkan dari 21 ke 23 agar kompatibel dengan Firebase Auth
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
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.runtime.livedata)
    ksp(libs.hilt.android.compiler)
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
    implementation(libs.bundles.retrofit)
    implementation(libs.okhttp.interceptor.logging)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.bundles.coil)
    implementation("io.coil-kt:coil-compose:2.6.0")
    // Material Icons Extended for additional icons (ViewInAr, AddAPhoto, etc.)
    implementation("androidx.compose.material:material-icons-extended")
    // Accompanist System UI Controller for rememberSystemUiController
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    // Add the dependencies for Firebase products you want to use
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.firebase:firebase-firestore-ktx")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.bundles.compose.ui.debug)
}

// Pass options to Room ksp processor
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.generateKotlin", "true")
}
