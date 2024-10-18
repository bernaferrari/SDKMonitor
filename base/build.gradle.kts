plugins {
    id("com.android.library")
    id("kotlin-android")
}

apply {
    from("../core_dependencies.gradle")
}

android {
    namespace = "com.bernaferrari.base"
}
