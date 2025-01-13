plugins {
    // Firebase function
    id("com.android.application")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.souvenir"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.souvenir"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        applicationId = "com.example.souvenir"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Add Firebase and Google Sign-In dependencies if needed
    implementation("com.google.firebase:firebase-auth:22.0.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-bom:32.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.gms:google-services:4.3.15")

    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-auth")

    // 네이버 지도 SDK
    implementation("com.naver.maps:map-sdk:3.18.0")

    implementation ("com.google.android.gms:play-services-location:18.0.0")

    // Retorofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")

    // 3D 모델링 가져오기 위해서 필요함
    implementation ("androidx.webkit:webkit:1.5.0")

    // OkHttp의 Kotlin 확장 라이브러리
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    implementation ("mysql:mysql-connector-java:8.0.28")
}