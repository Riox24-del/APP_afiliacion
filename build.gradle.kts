// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

buildscript {

    repositories {
        mavenCentral()
        mavenLocal()
        google()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.7.0")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")

    }
}
dependencies {
    implementation(kotlin("script-runtime"))
}

fun implementation(kotlin: Any) {

}
