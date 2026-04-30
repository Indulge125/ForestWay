package com.example.carbonroadapplication.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("关于我们", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Logo 区域
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(10.dp, CircleShape)
                    .background(Color(0xFF2E7D32), CircleShape)
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_mylocation),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("森途", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            Text("ForestWay v1.0.0", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(40.dp))

            // 开发者寄语卡片
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Favorite, null, tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("致每一位环保行者", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 【修改 1】合并文案，去除多余换行，使其更紧凑
                    Text(
                        text = "Hi，很高兴遇见你！这不仅仅是一个 App，更是一个由个人开发者用心打造的“绿色梦想”。在这个快节奏的时代，哪怕只是一次步行、一次骑行，都是对地球的一份温柔。希望森途能记录下你的每一份善意，见证你的绿色成长。",
                        fontSize = 15.sp,
                        color = Color.DarkGray,
                        lineHeight = 24.sp // 增加行高，阅读更舒适
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("遇到问题或有新想法？", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(12.dp))

                    // 【修改 2】重写邮箱按钮布局，确保内容一定显示
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), // 浅绿色背景
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp) // 强制固定高度，防止被压缩
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:1156852317@qq.com")
                                        putExtra(Intent.EXTRA_SUBJECT, "森途App - 用户反馈")
                                    }
                                    context.startActivity(Intent.createChooser(intent, "发送邮件"))
                                } catch (e: Exception) {
                                    // 忽略
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize() // 填满 Card
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 左侧白色圆底图标
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Email,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // 中间文字
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "发送邮件给开发者",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "1156852317@qq.com",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32) // 强制深绿色
                                )
                            }

                            // 右侧箭头
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Text("Designed with ❤️ by 个人开发者", fontSize = 12.sp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}