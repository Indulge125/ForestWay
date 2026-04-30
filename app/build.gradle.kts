plugins {
    alias(libs.plugins.android.application)

    // 【修正点】这里去掉了 .jetbrains，对应 TOML 里修改后的 [kotlin-android]
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.compose.compiler)
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.carbonroadapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.carbonroadapplication"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 【关键配置】NDK 架构过滤
        // 百度地图全量包(AllNavi)体积很大，必须过滤。
        // 真机通常是 arm64-v8a，模拟器调试可能需要 x86_64
        ndk {
            abiFilters.add("arm64-v8a")
            // abiFilters.add("armeabi-v7a") // 如需兼容旧设备可开启
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // 【修正】buildFeatures 必须在 android{} 根节点下，不能在 defaultConfig 中
    buildFeatures {
        compose = true
        buildConfig = true // 开启 BuildConfig (用于存放 API Key 等)
    }
}

dependencies {
    // ==================== 基础核心 (KTX) ====================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ==================== UI 框架 (Jetpack Compose) ====================
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // ==================== 依赖注入 (Hilt) ====================
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // ==================== 百度地图 SDK (指定版本) ====================
    // 定位全量包
    implementation("com.baidu.lbsyun:BaiduMapSDK_Location_All:9.6.6")
    // 地图+导航全量包
    implementation("com.baidu.lbsyun:BaiduMapSDK_Map-AllNavi:7.6.4")

    // ==================== 新增依赖 ====================
    // 【新增】Navigation Compose 核心导航库 (解决 hierarchy, findStartDestination 报错)
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // 【新增】Material 扩展图标库 (解决 ShowChart/BarChart 图标找不到的问题)
    // 注意：这个库有点大，第一次 Sync 可能需要几十秒下载
    implementation("androidx.compose.material:material-icons-extended:1.7.0")

    // 【新增】Room 数据库 (用于保存历史记录)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version") // 支持 Kotlin Flow
    kapt("androidx.room:room-compiler:$room_version")

    // 【新增】Coil 图片加载库 (用于显示相册选择的头像)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // ==================== 测试相关 ====================
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.google.android.material:material:1.12.0")
}