package com.example.carbonroadapplication

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.example.carbonroadapplication.ui.navigation.Screen
import com.example.carbonroadapplication.ui.navigation.bottomNavItems
import com.example.carbonroadapplication.ui.screens.*
import com.example.carbonroadapplication.utils.SessionManager
import com.example.carbonroadapplication.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    // 基础定位权限请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.any { it.value }) {
            Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 只检查定位权限，不再强制检查 POST_NOTIFICATIONS
        if (!checkPermission()) {
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF2E7D32),
                    secondary = Color(0xFF69F0AE),
                    background = Color(0xFFF5F5F5)
                )
            ) {
                MainApp(sessionManager)
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun MainApp(sessionManager: SessionManager) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val context = LocalContext.current

    // === 初始化定位服务 (保留静态前台服务以保活) ===
    val locationClient = remember {
        LocationClient.setAgreePrivacy(true)
        val client = LocationClient(context)

        val option = LocationClientOption()
        option.isOpenGps = true
        option.setCoorType("bd09ll")
        option.setScanSpan(1000)
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        client.locOption = option

        // 创建一个固定的通知，不再实时更新
        val notification = createForegroundNotification(context)
        client.enableLocInForeground(1001, notification)

        client
    }

    DisposableEffect(Unit) {
        val listener = object : BDAbstractLocationListener() {
            override fun onReceiveLocation(location: BDLocation?) {
                if (location == null) return
                homeViewModel.updateLocation(location)
            }
        }
        locationClient.registerLocationListener(listener)
        locationClient.start()
        onDispose {
            locationClient.unRegisterLocationListener(listener)
            locationClient.disableLocInForeground(true)
            locationClient.stop()
        }
    }

    val startRoute = if (sessionManager.isLoggedIn()) Screen.Home.route else Screen.Login.route

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            if (currentRoute != Screen.Login.route) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color(0xFF2E7D32)
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = { Icon(screen.icon, null) },
                            label = { Text(screen.title) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF2E7D32),
                                selectedTextColor = Color(0xFF2E7D32),
                                indicatorColor = Color(0xFFE8F5E9),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) { LoginScreen(navController) }
            composable(Screen.Home.route) { HomeScreen(viewModel = homeViewModel) }
            composable(Screen.Stats.route) { StatsScreen() }
            composable(Screen.Profile.route) { ProfileScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
            composable(Screen.AboutUs.route) { AboutScreen(navController) }
            composable(Screen.UserAgreement.route) { UserAgreementScreen(navController) }
        }
    }
}

// 恢复为简单的静态通知创建函数
fun createForegroundNotification(context: Context): Notification {
    val channelId = "eco_travel_loc_channel"
    val channelName = "行程记录服务"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        channel.setSound(null, null)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(context, channelId)
    } else {
        Notification.Builder(context)
    }

    return builder
        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
        .setContentTitle("森途")
        .setContentText("正在记录您的低碳轨迹...")
        .setOngoing(true)
        .build()
}