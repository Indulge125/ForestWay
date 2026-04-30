package com.example.carbonroadapplication.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.carbonroadapplication.ui.navigation.Screen
import com.example.carbonroadapplication.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()
    val isSuccess by viewModel.loginSuccess.collectAsState()
    val isRegisterMode by viewModel.isRegisterMode.collectAsState()
    
    var passwordVisible by remember { mutableStateOf(false) }

    // 监听登录成功状态，跳转到主页
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            navController.navigate(Screen.Home.route) {
                // 清除回退栈，防止按返回键回到登录页
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. 背景 (森之呼吸风格)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1B5E20), Color(0xFF4CAF50), Color(0xFFF5F5F5)),
                    startY = 0f,
                    endY = size.height * 0.6f
                )
            )
            // 装饰圆
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = size.width * 0.8f,
                center = Offset(size.width, 0f)
            )
        }

        // 2. 内容卡片 
        Column( 
            modifier = Modifier 
                .fillMaxSize() 
                .padding(24.dp), 
            horizontalAlignment = Alignment.CenterHorizontally, 
            verticalArrangement = Arrangement.Center 
        ) { 
            Spacer(modifier = Modifier.height(60.dp)) 
            
            // Logo / 标题区域 
            Icon( 
                imageVector = Icons.Rounded.Person, // 或者用你生成的 App 图标 
                contentDescription = null, 
                tint = Color.White, 
                modifier = Modifier.size(80.dp) 
            ) 
            Spacer(modifier = Modifier.height(16.dp)) 
            Text( 
                text = if (isRegisterMode) "欢迎加入森途" else "欢迎回到森途", 
                fontSize = 28.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color.White 
            ) 
            Text( 
                text = "开启您的低碳之旅", 
                fontSize = 14.sp, 
                color = Color.White.copy(alpha = 0.8f) 
            ) 
 
            Spacer(modifier = Modifier.height(48.dp)) 
 
            // 输入区域卡片 
            Card( 
                colors = CardDefaults.cardColors(containerColor = Color.White), 
                elevation = CardDefaults.cardElevation(8.dp), 
                shape = RoundedCornerShape(24.dp), 
                modifier = Modifier.fillMaxWidth() 
            ) { 
                Column( 
                    modifier = Modifier.padding(24.dp), 
                    horizontalAlignment = Alignment.CenterHorizontally 
                ) { 
                    // 账号输入 
                    OutlinedTextField( 
                        value = username, 
                        onValueChange = { viewModel.onUsernameChange(it) }, 
                        label = { Text("账号") }, 
                        placeholder = { Text("请输入用户名") }, 
                        leadingIcon = { Icon(Icons.Rounded.Person, null, tint = Color(0xFF2E7D32)) }, 
                        modifier = Modifier.fillMaxWidth(), 
                        singleLine = true, 
                        colors = OutlinedTextFieldDefaults.colors( 
                            focusedBorderColor = Color(0xFF2E7D32), 
                            focusedLabelColor = Color(0xFF2E7D32) 
                        ) 
                    ) 
 
                    Spacer(modifier = Modifier.height(16.dp)) 
 
                    // 密码输入 
                    OutlinedTextField( 
                        value = password, 
                        onValueChange = { viewModel.onPasswordChange(it) }, 
                        label = { Text("密码") }, 
                        placeholder = { Text("请输入密码") }, 
                        leadingIcon = { Icon(Icons.Rounded.Lock, null, tint = Color(0xFF2E7D32)) }, 
                        trailingIcon = { 
                            val image = if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff 
                            IconButton(onClick = { passwordVisible = !passwordVisible }) { 
                                Icon(image, null) 
                            } 
                        }, 
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), 
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), // 改为通用密码类型
                        modifier = Modifier.fillMaxWidth(), 
                        singleLine = true, 
                        colors = OutlinedTextFieldDefaults.colors( 
                            focusedBorderColor = Color(0xFF2E7D32), 
                            focusedLabelColor = Color(0xFF2E7D32) 
                        ) 
                    ) 
 
                    // 错误提示 
                    if (errorMsg != null) { 
                        Spacer(modifier = Modifier.height(8.dp)) 
                        Text( 
                            text = errorMsg!!, 
                            color = Color.Red, 
                            fontSize = 12.sp, 
                            modifier = Modifier.align(Alignment.Start) 
                        ) 
                    } 
 
                    Spacer(modifier = Modifier.height(32.dp)) 
 
                    // 登录/注册按钮 
                    Button( 
                        onClick = { viewModel.submit() }, 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), 
                        modifier = Modifier 
                            .fillMaxWidth() 
                            .height(50.dp), 
                        shape = RoundedCornerShape(12.dp) 
                    ) { 
                        Text(
                            text = if (isRegisterMode) "注 册" else "登 录",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ) 
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 切换模式文本
                    Text(
                        text = if (isRegisterMode) "已有账号？去登录" else "没有账号？去注册",
                        color = Color(0xFF2E7D32),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { viewModel.toggleMode() }
                            .padding(8.dp)
                    )
                } 
            } 
        }
    }
}
