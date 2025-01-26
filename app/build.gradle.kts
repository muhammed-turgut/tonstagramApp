plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.muhammedturgut.tonstagram"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.muhammedturgut.tonstagram"
        minSdk = 27
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
    // AndroidX Bağımlılıkları
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Test Bağımlılıkları
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase Bağımlılıkları
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // Google Material Design
    implementation("com.google.android.material:material:1.6.0")

    //Picaso
    implementation ("com.squareup.picasso:picasso:2.8")

    //yuvrarlak çerçeveli resimler için

    implementation ("com.makeramen:roundedimageview:2.3.0")

    dependencies {
        // Diğer bağımlılıklar
        implementation ("com.github.bumptech.glide:glide:4.12.0")
        annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

        // Bu profilFragment sayfasondaki slider bar navigation barın bağımlılıkları.
        implementation ("androidx.navigation:navigation-fragment-ktx:2.3.5")
        implementation ("androidx.navigation:navigation-ui-ktx:2.3.5")
    }
}