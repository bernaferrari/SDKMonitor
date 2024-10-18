import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
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

    defaultConfig {
        applicationId = "com.bernaferrari.sdkmonitor"
        versionCode = 13
        versionName = "0.99"
        multiDexEnabled = true
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
//        named("debug") {
//            applicationIdSuffix = ".debug"
//        }
    }

    kapt.correctErrorTypes = true
    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
    namespace = "com.bernaferrari.sdkmonitor"
    lint {
        abortOnError = false
    }
}

dependencies {

    implementation(project(":base"))
    implementation(project(":base-android"))

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Google
    implementation(libs.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.androidx.palette.ktx)

    // Navigation
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)

    // Room
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.rxjava2)

    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Paging
    implementation(libs.androidx.paging.runtime.ktx)

    // Work
    implementation(libs.androidx.work.runtime.ktx)

    // Dagger
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    implementation(libs.dagger.androidSupport)
    kapt(libs.dagger.androidProcessor)

    // Epoxy
    implementation(libs.epoxy)
    implementation(libs.epoxy.databinding)
    implementation(libs.epoxy.paging3)
    kapt(libs.epoxy.processor)

    implementation(libs.mavericks)
    implementation(libs.mavericks.rxjava2)
    testImplementation(libs.mavericks.testing)

    // RxJava
    implementation(libs.rxjava2.rxjava)
    implementation(libs.rxjava2.rxandroid)
    implementation(libs.rxjava2.rxkotlin)
    implementation(libs.rxrelay)
    implementation(libs.rxkprefs)

    implementation(libs.material.dialogs)
    implementation(libs.stetho)
    implementation(libs.logger)

    implementation(libs.notify)

    debugImplementation(libs.leakcanary)
//    debugImplementation(libs.leakcanary.support)

    // UI
    implementation(libs.indicatorfastscroll)

    // Time
    implementation(libs.timeago)

    // Debugging
    implementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
}
