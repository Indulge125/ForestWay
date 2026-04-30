package com.example.carbonroadapplication

import android.app.Application
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // 1. 必须加这个注解，Hilt 才能工作
class CarbonApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 2. 初始化百度地图 SDK
        // 【注意】在正式上架时，setAgreePrivacy 必须在用户点击隐私政策“同意”按钮后才能调用
        // 但在开发阶段，为了能先跑通，我们这里直接设置为 true
        SDKInitializer.setAgreePrivacy(this, true)

        SDKInitializer.initialize(this)

        // 3. 设置坐标类型为百度经纬度 (BD09LL)，国内定位更准
        SDKInitializer.setCoordType(CoordType.BD09LL)
    }
}