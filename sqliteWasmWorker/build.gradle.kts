@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    wasmJs {
        browser()
        useEsModules()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.androidx.sqlite.web)
                implementation(
                    npm("sqlite-wasm-worker", layout.projectDirectory.dir("worker").asFile),
                )
            }
        }
        val wasmJsMain by getting {
            dependencies {
                implementation(libs.kotlinx.browser)
            }
        }
    }
}
