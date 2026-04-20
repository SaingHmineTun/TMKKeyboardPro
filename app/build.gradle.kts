plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "it.saimao.tmkkeyboardpro"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "it.saimao.tmkkeyboardpro"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("com.github.SaingHmineTun:ShanLanguageTools:1.0.1")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.github.yukuku:ambilwarna:2.0.1")
    implementation("com.github.yalantis:ucrop:2.2.8")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}