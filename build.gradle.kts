// Top-level build file
plugins {
    // 1. Android Application
    alias(libs.plugins.android.application) apply false

    // 2. Kotlin Android
    // 对应 TOML 中的 [kotlin-android] -> libs.plugins.kotlin.android
    alias(libs.plugins.kotlin.android) apply false

    // 3. Compose Compiler
    // 对应 TOML 中的 [compose-compiler] -> libs.plugins.compose.compiler
    alias(libs.plugins.compose.compiler) apply false

    // 4. Hilt
    // 对应 TOML 中的 [hilt-android] -> libs.plugins.hilt.android
    alias(libs.plugins.hilt.android) apply false
}