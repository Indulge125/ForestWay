package com.example.carbonroadapplication.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.carbonroadapplication.ui.navigation.Screen
import com.example.carbonroadapplication.viewmodel.BadgeUI
import com.example.carbonroadapplication.viewmodel.LevelDef
import com.example.carbonroadapplication.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userState by viewModel.uiState.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) viewModel.updateAvatar(uri) }
    )

    // 勋章弹窗
    if (userState.showBadgeDialog && userState.selectedBadge != null) {
        BadgeDetailDialog(badge = userState.selectedBadge!!, onDismiss = { viewModel.dismissBadgeDialog() })
    }

    // 等级规则弹窗
    if (userState.showLevelDialog) {
        LevelRulesDialog(
            currentLevel = userState.level,
            rules = viewModel.levelRules,
            onDismiss = { viewModel.dismissLevelDialog() }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        // 1. 头部区域
        item {
            Box(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                // 背景
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(brush = Brush.linearGradient(colors = listOf(Color(0xFF1B5E20), Color(0xFF4CAF50)), start = Offset(0f, 0f), end = Offset(size.width, size.height)))
                    drawCircle(color = Color.White.copy(alpha = 0.1f), radius = size.width * 0.6f, center = Offset(size.width, 0f))
                    drawCircle(color = Color.White.copy(alpha = 0.05f), radius = size.width * 0.4f, center = Offset(0f, size.height * 0.8f))
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 32.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 头像
                    Box(contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(110.dp).background(Color.White.copy(alpha = 0.2f), CircleShape))
                        Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.White).border(2.dp, Color.White, CircleShape).clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, contentAlignment = Alignment.Center) {
                            if (userState.avatarUri != null) AsyncImage(model = userState.avatarUri, contentDescription = "Avatar", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            else Icon(Icons.Rounded.Person, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(64.dp))
                        }
                        Box(modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-4).dp, y = (-4).dp).size(28.dp).background(Color(0xFF2E7D32), CircleShape).border(2.dp, Color.White, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Edit, null, modifier = Modifier.size(14.dp), tint = Color.White) }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = userState.userName, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))

                    // 称号
                    Surface(color = Color.Black.copy(alpha = 0.15f), shape = RoundedCornerShape(50), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Icon(Icons.Rounded.EmojiEvents, null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = userState.levelTitle, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 等级进度条区域
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).background(Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA000))), CircleShape).border(2.dp, Color.White, CircleShape), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Lv", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold, lineHeight = 10.sp)
                                Text("${userState.level}", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Black, lineHeight = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("成长值", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    // 快捷入口
                                    Icon(
                                        imageVector = Icons.Rounded.Info,
                                        contentDescription = "info",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp).clickable { viewModel.showLevelRules() }
                                    )
                                }
                                Text("${userState.currentExp} / ${userState.maxExp}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            val progress = if (userState.maxExp > 0) userState.currentExp.toFloat() / userState.maxExp else 0f
                            Box(modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)).background(Color.Black.copy(alpha = 0.2f))) {
                                Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().clip(RoundedCornerShape(5.dp)).background(Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFEA00)))))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "再获 ${userState.maxExp - userState.currentExp} 经验升级", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // 2. 核心数据
        item {
            Box(modifier = Modifier.offset(y = (-60).dp).padding(horizontal = 16.dp)) {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(10.dp), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(vertical = 28.dp, horizontal = 24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                        StatItem("${userState.totalTrees}", "种植真树", Icons.Rounded.Forest, Color(0xFF2E7D32))
                        HorizontalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray)
                        StatItem(String.format("%.1f", userState.totalCarbonKg), "减排 (kg)", Icons.Rounded.Co2, Color(0xFF1565C0))
                    }
                }
            }
        }

        // 3. 虚拟苗圃
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .offset(y = (-40).dp)
            ) {
                SectionTitle("我的虚拟苗圃")
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Info, null, Modifier.size(14.dp), Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("每减少 10kg 碳排放，点亮一棵树", fontSize = 12.sp, color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            repeat(5) { index ->
                                val isUnlocked = index < userState.totalTrees
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Rounded.Park,
                                        contentDescription = null,
                                        tint = if (isUnlocked) Color(0xFF2E7D32) else Color.LightGray.copy(alpha = 0.3f),
                                        modifier = Modifier.size(40.dp)
                                    )
                                    if (!isUnlocked) {
                                        Icon(Icons.Rounded.Lock, null, Modifier.size(12.dp), tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. 勋章墙
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp).offset(y = (-20).dp)) {
                SectionTitle("成就勋章")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(end = 16.dp)) {
                    items(userState.badges) { badge -> BadgeItem(badge = badge, onClick = { viewModel.showBadgeDetails(badge) }) }
                }
            }
        }

        // 5. 菜单 (已添加等级说明)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp).offset(y = (-10).dp)) {
                MenuOptionItem(
                    icon = Icons.Rounded.Settings,
                    title = "设置",
                    onClick = { navController.navigate(Screen.Settings.route) }
                )
                // 【新增】等级说明入口
                MenuOptionItem(
                    icon = Icons.Rounded.AutoGraph, // 使用图表图标代表等级/成长
                    title = "等级说明",
                    onClick = { viewModel.showLevelRules() }
                )
                MenuOptionItem(
                    icon = Icons.Rounded.Info,
                    title = "关于我们",
                    onClick = { navController.navigate(Screen.AboutUs.route) }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// === 下方组件保持不变 ===

@Composable
fun LevelRulesDialog(currentLevel: Int, rules: List<LevelDef>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("等级说明", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(rules) { rule ->
                        val isReached = rule.level <= currentLevel
                        val isCurrent = rule.level == currentLevel
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if(isCurrent) Color(0xFFE8F5E9) else Color.Transparent, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp).background(if(isReached) Color(0xFFFFD700) else Color.LightGray, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${rule.level}", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row {
                                    Text(rule.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${rule.reqExp} 经验", color = Color(0xFF2E7D32), fontSize = 12.sp)
                                }
                                Text(rule.desc, fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                    Text("我知道了")
                }
            }
        }
    }
}

@Composable
fun BadgeDetailDialog(badge: BadgeUI, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            if(badge.isUnlocked) Color(badge.color) else Color.Gray,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if(badge.isUnlocked) Icons.Rounded.EmojiEvents else Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(badge.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if(badge.isUnlocked) "已解锁" else "未解锁",
                    color = if(badge.isUnlocked) Color(0xFF2E7D32) else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(badge.desc, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("关闭")
                }
            }
        }
    }
}

@Composable
fun BadgeItem(badge: BadgeUI, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    if (badge.isUnlocked) Color(badge.color) else Color(0xFFEEEEEE),
                    CircleShape
                )
                .border(2.dp, if(badge.isUnlocked) Color(0xFFFFD700) else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if(badge.isUnlocked) Icons.Rounded.EmojiEvents else Icons.Rounded.Lock,
                contentDescription = null,
                tint = if(badge.isUnlocked) Color.White else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = badge.name, fontSize = 12.sp, color = if(badge.isUnlocked) Color.Black else Color.Gray)
    }
}

@Composable
fun MenuOptionItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp, modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun ContainerLabel(text: String) {
    Surface(
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatItem(value: String, label: String, icon: ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
    )
}