plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}
apply {
    from("../core_dependencies.gradle")
}

android {
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    namespace = "com.bernaferrari.ui"
}

dependencies {
    implementation(project(":base"))

    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.rxrelay)
}
