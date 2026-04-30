package com.example.carbonroadapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// 【修复】导入 AutoMirrored 图标，解决废弃警告
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// 【修复】导入 clip
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.carbonroadapplication.data.TripEntity
import com.example.carbonroadapplication.viewmodel.StatsViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val trips by viewModel.allTrips.collectAsState()
    val totalCarbon by viewModel.totalCarbon.collectAsState()

    var selectedImageTrip by remember { mutableStateOf<TripEntity?>(null) }

    // === 图片预览弹窗 (全屏感) ===
    if (selectedImageTrip != null) {
        Dialog(
            onDismissRequest = { selectedImageTrip = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {
                Column {
                    // 显示图片
                    if (selectedImageTrip!!.screenshotPath != null) {
                        AsyncImage(
                            model = File(selectedImageTrip!!.screenshotPath!!),
                            contentDescription = "行程截图",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.65f) // 竖长图比例
                                .background(Color.LightGray),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(modifier = Modifier.height(200.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("未找到图片文件", color = Color.Gray)
                        }
                    }

                    // 底部关闭按钮
                    Button(
                        onClick = { selectedImageTrip = null },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("关闭预览")
                    }
                }
            }
        }
    }

    // === 页面内容 ===
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // 顶部总计
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(120.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("累计减排 CO2", color = Color(0xFFE8F5E9), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${String.format(Locale.getDefault(), "%.2f", totalCarbon ?: 0.0)} kg",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("历史记录", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(trips) { trip ->
                TripItem(trip = trip, onClick = { selectedImageTrip = trip })
            }
        }
    }
}

@Composable
fun TripItem(trip: TripEntity, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE8F5E9),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // 【修复】使用 AutoMirrored 图标
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.DirectionsWalk,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault()).format(Date(trip.timestamp)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "里程: ${String.format(Locale.getDefault(), "%.2f", trip.distanceMeters / 1000.0)} km",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "+${String.format(Locale.getDefault(), "%.2f", trip.carbonSaved)}kg",
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}