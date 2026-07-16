plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room3)
}

kotlin {
    android {
        namespace = "com.bernaferrari.sdkmonitor.shared"
        compileSdk = 37
        minSdk = 28
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
        androidResources {
            enable = true
        }
    }

    jvm("desktop")

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.library()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(libs.compose.adaptive)
                implementation(libs.compose.adaptive.layout)
                implementation(libs.compose.adaptive.navigation)
                implementation(libs.compose.adaptive.navigation.suite)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.nappier)
                // Room 3: entities / DAOs / AppDatabase in commonMain
                implementation(libs.androidx.room3.runtime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.sqlite.bundled)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.androidx.sqlite.bundled)
            }
        }
        val wasmJsMain by getting {
            dependencies {
                implementation(libs.androidx.sqlite.web)
                implementation(project(":sqliteWasmWorker"))
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room3.compiler)
    add("kspDesktop", libs.androidx.room3.compiler)
    add("kspWasmJs", libs.androidx.room3.compiler)
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.bernaferrari.sdkmonitor.shared.resources"
}

compose.desktop {
    application {
        mainClass = "com.bernaferrari.sdkmonitor.desktop.MainKt"
    }
}
