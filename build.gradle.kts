buildscript {

    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
        maven("https://kotlin.bintray.com/kotlinx")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.0-beta02")
        classpath(kotlin("gradle-plugin", version = "1.3.20"))
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
        maven("https://kotlin.bintray.com/kotlinx")
    }
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}
