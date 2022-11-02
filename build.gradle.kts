buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
    }
}

plugins {
    id("com.github.ben-manes.versions") version "0.43.0"
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}
