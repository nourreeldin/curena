plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.fueians.medicationapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fueians.medicationapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    val room_version = "2.8.3"
    implementation("androidx.room:room-runtime:${room_version}")
    ksp("androidx.room:room-compiler:$room_version")

    // ----------------------------------------------------
    // START: RxJava 3 Dependencies
    // ----------------------------------------------------
    val rxjava_version = "3.1.8"
    // 1. Core RxJava 3 library
    implementation("io.reactivex.rxjava3:rxjava:$rxjava_version")
    // 2. Android-specific scheduler (e.g., AndroidSchedulers.mainThread())
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    // 3. Room support for returning RxJava 3 types (Single, Completable, Flowable)
    implementation("androidx.room:room-rxjava3:$room_version")
    // ----------------------------------------------------
    // END: RxJava 3 Dependencies
    // ----------------------------------------------------
    // Koin Dependencies
    val koin_version = "3.5.0" // Use a recent stable version
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-android:$koin_version")
    implementation("at.favre.lib:bcrypt:0.10.2")

    // ... other dependencies
    val moshi_version = "1.15.0"
    implementation("com.squareup.moshi:moshi:$moshi_version")
    implementation("com.squareup.moshi:moshi-kotlin:$moshi_version") // Kotlin extensions
    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshi_version") // For code generation
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}