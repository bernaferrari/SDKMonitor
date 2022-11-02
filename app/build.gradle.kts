import com.bernaferrari.buildsrc.Libs2
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
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.bernaferrari.sdkmonitor"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
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
    lintOptions.isAbortOnError = false
    buildFeatures {
        dataBinding = true
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

    implementation(project(":base"))
    implementation(project(":base-android"))

    // Kotlin
    implementation(Libs2.Kotlin.stdlib)
    implementation(Libs2.Coroutines.core)
    implementation(Libs2.Coroutines.android)

    // Google
    implementation(Libs2.Google.material)
    implementation(Libs2.AndroidX.coreKtx)
    implementation(Libs2.AndroidX.constraintlayout)
    implementation(Libs2.AndroidX.appcompat)
    implementation(Libs2.AndroidX.recyclerview)
    implementation(Libs2.AndroidX.Fragment.fragmentKtx)

    implementation("androidx.palette:palette-ktx:1.0.0")

    // Navigation
    implementation(Libs2.AndroidX.Navigation.navigationUi)
    implementation(Libs2.AndroidX.Navigation.navigationFragment)

    // Room
    kapt(Libs2.AndroidX.Room.compiler)
    implementation(Libs2.AndroidX.Room.runtime)
    implementation(Libs2.AndroidX.Room.roomktx)
    implementation(Libs2.AndroidX.Room.rxjava2)

    // LiveData
    implementation(Libs2.AndroidX.Lifecycle.liveDataKtx)
    implementation(Libs2.AndroidX.Lifecycle.viewModel)

    // Paging
    implementation(Libs2.AndroidX.Paging.runtimeKtx)

    // Work
    implementation(Libs2.AndroidX.Work.runtimeKtx)

    // Dagger
    implementation(Libs2.Dagger.dagger)
    kapt(Libs2.Dagger.compiler)

    implementation(Libs2.Dagger.androidSupport)
    kapt(Libs2.Dagger.androidProcessor)

    compileOnly(Libs2.AssistedInject.annotationDagger2)
    kapt(Libs2.AssistedInject.processorDagger2)

    // Epoxy
    implementation(Libs2.Epoxy.epoxy)
    implementation(Libs2.Epoxy.dataBinding)
    implementation(Libs2.Epoxy.paging)
    kapt(Libs2.Epoxy.processor)

    implementation(Libs2.MvRx.main)
    testImplementation(Libs2.MvRx.testing)

    // RxJava
    implementation(Libs2.RxJava.rxJava)
    implementation(Libs2.RxJava.rxAndroid)
    implementation(Libs2.RxJava.rxKotlin)
    implementation(Libs2.RxJava.rxRelay)
    implementation(Libs2.RxJava.rxkPrefs)

    implementation(Libs2.materialDialogs)
    implementation(Libs2.stetho)
    implementation(Libs2.logger)

    implementation(Libs2.notify)

    debugImplementation(Libs2.LeakCanary.no_op)
//    debugImplementation(Libs2.LeakCanary.support)
    releaseImplementation(Libs2.LeakCanary.no_op)

    // UI
    implementation("com.reddit:indicator-fast-scroll:1.3.0")

    // Time
    implementation(Libs2.timeAgo)

    // Debugging
    implementation(Libs2.junit)
    testImplementation("org.mockito:mockito-core:3.4.6")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
}
