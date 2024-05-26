plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace ="com.rassam.atiniapp"
    compileSdkVersion(34)
    defaultConfig {
        applicationId = "com.rassam.atiniapp"
        minSdkVersion(23)
        targetSdkVersion(34)
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
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-auth")

//    implementation("com.amazonaws:aws-java-sdk-bom:1.12.730")
//    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.12.730")
//    implementation("com.amazonaws:aws-android-sdk-mobile-client:2.75.1")
//    implementation("com.amazonaws:aws-android-sdk-s3:2.75.1")
//    implementation("com.amazonaws:aws-android-sdk-ddb:2.75.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    implementation("com.google.android.material:material:1.5.0")

    implementation("com.google.firebase:firebase-common")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

