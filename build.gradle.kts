buildscript {
    val kotlin_version = "1.8.0"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.4.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")

    }
}
plugins {

    id("com.google.gms.google-services") version "4.4.1" apply false

}
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

