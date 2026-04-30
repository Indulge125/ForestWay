package com.example.carbonroadapplication.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 导航路由定义
 * 遵循 Material 3 设计规范，打造清爽、现代、绿色的 UI 框架
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    // 底部导航栏页面
    object Home : Screen("home", "出行", Icons.Rounded.Map)
    object Stats : Screen("stats", "统计", Icons.Rounded.BarChart)
    object Profile : Screen("profile", "我的", Icons.Rounded.Person)

    // 【新增】子页面 (图标仅作占位，底部栏不显示)
    object Settings : Screen("settings", "设置", Icons.Rounded.Settings)
    object AboutUs : Screen("about_us", "关于我们", Icons.Rounded.Info)
    object UserAgreement : Screen("user_agreement", "用户协议", Icons.Rounded.Description)
    
    // 【新增】登录页
    object Login : Screen("login", "登录", Icons.Rounded.Login)
}

/**
 * 底部导航栏项目列表
 * 方便遍历生成底部按钮
 */
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Stats,
    Screen.Profile
)