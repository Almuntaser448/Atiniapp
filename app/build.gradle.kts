plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace ="com.rassam.atiniapp"
    compileSdkVersion(33)
    defaultConfig {
        applicationId = "com.rassam.atiniapp"
        minSdkVersion(21)
        targetSdkVersion(33)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    implementation(libs.firebase.common)
    implementation(libs.firebase.firestore)
    implementation("com.google.android.material:material:1.5.0")
    implementation(libs.firebase.storage) // Updated Kotlin version
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
