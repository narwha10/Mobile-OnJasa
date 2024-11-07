plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.onjasa"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.onjasa"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation (libs.osmdroid.android)
    implementation (libs.okhttp)
    implementation (libs.volley)
    implementation(libs.volley)
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation (libs.firebase.database.ktx)
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.midtrans:uikit:2.3.0-SANDBOX")
    implementation ("com.midtrans:uikit:2.0.0")
    implementation ("com.google.android.gms:play-services-maps:19.0.0")
    implementation(libs.billing)
    implementation(libs.androidx.preference.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
