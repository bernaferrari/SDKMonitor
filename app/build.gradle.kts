import org.jetbrains.kotlin.config.KotlinCompilerVersion
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

androidExtensions {
    isExperimental = true
}

android {
    signingConfigs {
        register("release") {

            val keystorePropertiesFile = file("../upload-keystore.properties")

            if (!keystorePropertiesFile.exists()) {
                logger.warn("Release builds may not work: signing config not found.")
                return@register
            }

            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))

            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "com.bernaferrari.sdkmonitor"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 4
        versionName = "0.4"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        named("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    kapt.correctErrorTypes = true
    lintOptions.isAbortOnError = false
    dataBinding.isEnabled = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    // Jetpack
    implementation("com.google.android.material:material:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.0.0")
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.palette:palette:1.0.0")
    implementation("androidx.core:core:1.0.1")
    implementation("androidx.core:core-ktx:1.0.1")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")

    val nav_version = "1.0.0-alpha11"
    implementation("android.arch.navigation:navigation-fragment-ktx:$nav_version")
    implementation("android.arch.navigation:navigation-ui-ktx:$nav_version")

    val room_version = "2.0.0"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-rxjava2:$room_version")

    val lifecycle_version = "2.0.0"
    implementation("androidx.lifecycle:lifecycle-extensions:$lifecycle_version")

    val paging_version = "2.1.0"
    implementation("androidx.paging:paging-runtime-ktx:$paging_version")
    implementation("androidx.paging:paging-rxjava2-ktx:$paging_version")

    val work_version = "1.0.0-beta01"
    implementation("android.arch.work:work-runtime-ktx:$work_version")

    // Dagger
    val dagger = "2.20"
    implementation("com.google.dagger:dagger:$dagger")
    kapt("com.google.dagger:dagger-compiler:$dagger")

    // Epoxy
    val epoxy = "3.1.0"
    implementation("com.airbnb.android:epoxy:$epoxy")
    implementation("com.airbnb.android:epoxy-databinding:$epoxy")
    kapt("com.airbnb.android:epoxy-processor:$epoxy")
    implementation("com.airbnb.android:mvrx:0.6.0")

    // Groupie
    val groupie = "2.3.0"
    implementation("com.xwray:groupie:$groupie")
    implementation("com.xwray:groupie-kotlin-android-extensions:$groupie")

    // Coroutines
    val coroutines_version = "1.1.0"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")

    // RxJava
    implementation("io.reactivex.rxjava2:rxjava:2.2.5")
    implementation("io.reactivex.rxjava2:rxkotlin:2.3.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")
    implementation("com.jakewharton.rxrelay2:rxrelay:2.0.0")

    // Iconics
    implementation("com.mikepenz:iconics-core:3.1.0@aar")
    implementation("com.mikepenz:community-material-typeface:2.0.46.1@aar")
    implementation("com.mikepenz:google-material-typeface:3.0.1.2.original@aar")

    // UI
    implementation("io.karn:notify:1.1.0")
    implementation("com.reddit:indicator-fast-scroll:1.0.1")
    implementation("com.afollestad.material-dialogs:core:2.0.0-rc7")

    // Time
    implementation("com.github.marlonlom:timeago:4.0.1")

    // Internal
    implementation("com.orhanobut:logger:2.2.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))

    // Debugging
    implementation("com.facebook.stetho:stetho:1.5.0")
}
