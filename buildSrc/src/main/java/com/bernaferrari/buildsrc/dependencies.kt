package com.bernaferrari.buildsrc

object Libs2 {
    val materialDialogs = "com.afollestad.material-dialogs:core:3.3.0"

    val logger = "com.orhanobut:logger:2.2.0"

    val notify = "io.karn:notify:1.4.0"
    val timeAgo = "com.github.marlonlom:timeago:4.0.3"

    val stetho = "com.facebook.stetho:stetho:1.6.0"

    val junit = "junit:junit:4.13.2"
    val mockitoCore = "org.mockito:mockito-core:3.12.4"
    val mockitoKotlin = "org.mockito.kotlin:mockito-kotlin:2.2.11"

    val indicatorFastScroll = "com.github.reddit:IndicatorFastScroll:1.4.0"

    object Google {
        val material = "com.google.android.material:material:1.12.0"
    }

    object Kotlin {
        private const val version = "2.0.21"
        val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
        val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
        val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }

    object Coroutines {
        private const val version = "1.9.0"
        val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        val rx2 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:$version"
        val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    }

    object AndroidX {
        val appcompat = "androidx.appcompat:appcompat:1.7.0"
        val paletteKtx = "androidx.palette:palette-ktx:1.0.0"
        val recyclerview = "androidx.recyclerview:recyclerview:1.3.2"

        object Navigation {
            private const val version = "2.8.2"
            val navigationUi = "androidx.navigation:navigation-ui-ktx:$version"
            val navigationFragment = "androidx.navigation:navigation-fragment-ktx:$version"
        }

        val fragmentKtx = "androidx.fragment:fragment-ktx:1.8.4"

        object Paging {
            private const val version = "3.3.2"
            val common = "androidx.paging:paging-common:$version"
            val runtime = "androidx.paging:paging-runtime:$version"
            val runtimeKtx = "androidx.paging:paging-runtime-ktx:$version"
            val rxjava2 = "androidx.paging:paging-rxjava2:$version"
        }

        val constraintlayout = "androidx.constraintlayout:constraintlayout:2.1.4"

        val coreKtx = "androidx.core:core-ktx:1.13.1"

        object Lifecycle {
            private const val version = "2.8.6"
            val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            val compiler = "androidx.lifecycle:lifecycle-compiler:$version"
        }

        object Room {
            private const val version = "2.6.1"
            val common = "androidx.room:room-common:$version"
            val runtime = "androidx.room:room-runtime:$version"
            val roomktx = "androidx.room:room-ktx:$version"
            val rxjava2 = "androidx.room:room-rxjava2:$version"
            val compiler = "androidx.room:room-compiler:$version"
        }

        object Work {
            private const val version = "2.9.1"
            val runtimeKtx = "androidx.work:work-runtime-ktx:$version"
            val rxJava = "androidx.work:work-rxjava2:$version"
            val testing = "androidx.work:work-testing:$version"
        }
    }

    object RxJava {
        val rxJava = "io.reactivex.rxjava2:rxjava:2.2.21"
        val rxKotlin = "io.reactivex.rxjava2:rxkotlin:2.4.0"
        val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
        val rxRelay = "com.jakewharton.rxrelay2:rxrelay:2.1.1"
        val rxkPrefs = "com.afollestad:rxkprefs:1.2.5"
    }

    object Dagger {
        private const val version = "2.52"
        val dagger = "com.google.dagger:dagger:$version"
        val androidSupport = "com.google.dagger:dagger-android-support:$version"
        val compiler = "com.google.dagger:dagger-compiler:$version"
        val androidProcessor = "com.google.dagger:dagger-android-processor:$version"
    }

    object Glide {
        private const val version = "4.16.0"
        val glide = "com.github.bumptech.glide:glide:$version"
        val compiler = "com.github.bumptech.glide:compiler:$version"
    }

    object LeakCanary {
        private const val version = "1.6.3"
        val main = "com.squareup.leakcanary:leakcanary-android:$version"
        val no_op = "com.squareup.leakcanary:leakcanary-android-no-op:$version"
        val support = "com.squareup.leakcanary:leakcanary-support-fragment:$version"
    }

    object Mavericks {
        private const val version = "3.0.9"
        val main = "com.airbnb.android:mavericks:$version"
        val rxjava2 = "com.airbnb.android:mavericks-rxjava2:$version"
        val testing = "com.airbnb.android:mavericks-testing:$version"
    }

    object Epoxy {
        private const val version = "5.1.4"
        val epoxy = "com.airbnb.android:epoxy:$version"
        val paging = "com.airbnb.android:epoxy-paging3:$version"
        val dataBinding = "com.airbnb.android:epoxy-databinding:$version"
        val processor = "com.airbnb.android:epoxy-processor:$version"
    }
}
