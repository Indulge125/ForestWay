package com.example.carbonroadapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.carbonroadapplication.ui.navigation.Screen
// 【注意】这里不再需要 SettingsViewModel，直接使用 LoginViewModel 处理退出即可，或者直接在 onClick 处理
import com.example.carbonroadapplication.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    // 复用 LoginViewModel 来处理退出逻辑（因为它有 logout 方法）
    viewModel: LoginViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // === 设置组 ===
            SettingsGroupTitle("通用")
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                SettingsItem(Icons.Rounded.Description, "用户协议") {
                    navController.navigate(Screen.UserAgreement.route)
                }

                HorizontalDivider(color = Color(0xFFF5F5F5))

                SettingsItem(Icons.Rounded.Info, "版本信息", "v1.0.0") {}
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 退出登录按钮
            Button(
                onClick = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("退出登录")
            }
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, endText: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f))
        if (endText != null) {
            Text(endText, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = Color.LightGray)
    }
}

@Composable
fun SettingsGroupTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        color = Color.Gray,
        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
    )
}