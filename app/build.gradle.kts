plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

val buildToolsVersion = "30.0.3"
val minSdkVersion = 21
val compileSdkVersion = 30
val targetSdkVersion = 30
val kotlinVersion = "1.5.10"

android {
    compileSdkVersion(compileSdkVersion)
    buildToolsVersion(buildToolsVersion)

    defaultConfig {
        applicationId = "com.example.atiniapp"
        minSdkVersion(minSdkVersion)
        targetSdkVersion(targetSdkVersion)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.recyclerview:recyclerview:1.2.0")

    // AWS SDK dependencies
    implementation("com.amazonaws:aws-android-sdk-core:2.22.5")
    implementation("com.amazonaws:aws-android-sdk-cognitoidentityprovider:2.22.5")
    implementation("com.amazonaws:aws-android-sdk-s3:2.22.5")
    implementation("com.amazonaws:aws-android-sdk-dynamodb:2.22.5")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
