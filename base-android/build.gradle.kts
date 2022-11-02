import com.bernaferrari.buildsrc.Libs2

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}
apply {
    from("../core_dependencies.gradle")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":base"))

    // Google
    implementation(Libs2.AndroidX.Lifecycle.liveDataKtx)
    implementation(Libs2.AndroidX.Lifecycle.viewModel)

    implementation(Libs2.RxJava.rxRelay)
}
