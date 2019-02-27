buildscript {

    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.0-beta05")
        classpath(kotlin("gradle-plugin", version = "1.3.20"))
        classpath("com.squareup.sqldelight:gradle-plugin:1.0.3")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}
