buildscript {

    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.0-beta02")
        classpath(kotlin("gradle-plugin", version = "1.3.31"))
        classpath("com.squareup.sqldelight:gradle-plugin:1.1.3")
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
