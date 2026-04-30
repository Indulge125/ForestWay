package com.example.carbonroadapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Bitmap
import android.widget.Toast
import com.baidu.mapapi.map.MapView
import androidx.hilt.navigation.compose.hiltViewModel
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MyLocationData
import com.example.carbonroadapplication.ui.components.EcoMapView
import com.example.carbonroadapplication.ui.components.TripSummaryDialog
import com.example.carbonroadapplication.utils.ImageStorageUtils
import com.example.carbonroadapplication.utils.ShareUtils
import com.example.carbonroadapplication.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var baiduMapState by remember { mutableStateOf<BaiduMap?>(null) }
    var mapViewState by remember { mutableStateOf<MapView?>(null) }
    var isFirstLocate by remember { mutableStateOf(true) }

    // === 逻辑 1: 实时更新地图上的蓝点 (MyLocation) ===
    LaunchedEffect(uiState.currentLatLng) {
        val latLng = uiState.currentLatLng
        val map = baiduMapState
        if (latLng != null && map != null) {
            val locData = MyLocationData.Builder()
                .accuracy(uiState.accuracy)
                .direction(uiState.direction)
                .latitude(latLng.latitude)
                .longitude(latLng.longitude)
                .build()
            map.setMyLocationData(locData)

            // 如果是第一次进入，自动飞过去
            if (isFirstLocate) {
                isFirstLocate = false
                val update = MapStatusUpdateFactory.newLatLngZoom(latLng, 19f)
                map.animateMapStatus(update)
            }
        }
    }

    // === 逻辑 2: 镜头控制 (导航模式 & 强制复位) ===
    // 监听 currentLatLng 变化来实现跟随；监听 recenterTrigger 来实现复位
    LaunchedEffect(uiState.currentLatLng, uiState.recenterTrigger) {
        val map = baiduMapState
        val latLng = uiState.currentLatLng

        if (map != null && latLng != null) {
            // 情况 A: 正在录制中 -> 强制锁定镜头 (导航模式)
            if (uiState.isRecording) {
                val update = MapStatusUpdateFactory.newLatLng(latLng)
                map.animateMapStatus(update)
            }
            // 情况 B: 收到复位信号 (刚点击开始 或 刚关闭弹窗)
            // 我们通过检测 recenterTrigger 的变化来触发
        }
    }

    // === 逻辑 3: 专门监听复位信号 ===
    LaunchedEffect(uiState.recenterTrigger) {
        if (uiState.recenterTrigger > 0 && baiduMapState != null && uiState.currentLatLng != null) {
            val update = MapStatusUpdateFactory.newLatLngZoom(uiState.currentLatLng, 19f)
            baiduMapState?.animateMapStatus(update)
        }
    }

    if (uiState.showSummaryDialog && uiState.lastTripResult != null) {
        TripSummaryDialog(
            trip = uiState.lastTripResult!!,
            onDismiss = { viewModel.dismissDialog() },
            onShare = {
                // 【核心逻辑】点击分享时执行：
                if (baiduMapState != null) {
                    // 1. 调用百度地图截图 API
                    baiduMapState!!.snapshot { bitmap ->
                        if (bitmap != null) {
                            // 2. 调用工具类进行合成与分享
                            ShareUtils.shareTripImage(context, bitmap, uiState.lastTripResult!!)
                        } else {
                            Toast.makeText(context, "地图截图失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        // 顶部卡片
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0xFF2E7D32))
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Text("今日减排 (kg)", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = String.format("%.3f", uiState.carbonSaved),
                    color = Color(0xFF2E7D32),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "里程: ${String.format("%.3f", uiState.totalDistanceMeters / 1000.0)} km",
                        color = Color.Gray, fontSize = 14.sp
                    )
                }
            }
            Button(
                onClick = {
                    if (uiState.isRecording) {
                        // === 停止录制流程 ===
                        if (baiduMapState != null) {
                            // 1. 截图
                            baiduMapState!!.snapshot { rawMapBitmap ->
                                if (rawMapBitmap != null) {
                                    // 2. 【关键修改】调用工具类，合成带卡片的最终图片
                                    val currentUser = "user" // 这里ViewModel内部会处理真实用户名，文件名只要唯一即可
                                    
                                    // 先构建一个临时的 TripEntity 用于画图
                                    val tempTrip = com.example.carbonroadapplication.data.TripEntity(
                                        timestamp = System.currentTimeMillis(),
                                        distanceMeters = uiState.totalDistanceMeters,
                                        carbonSaved = uiState.carbonSaved,
                                        username = "" // 占位
                                    )

                                    // 合成图片！(这就是那张带白框、对勾的图)
                                    val finalBitmap = ShareUtils.generateTripImage(context, rawMapBitmap, tempTrip)

                                    // 3. 保存这张合成图到本地存储
                                    val savedPath = ImageStorageUtils.saveTripSnapshot(
                                        context,
                                        finalBitmap, // 保存的是合成后的图
                                        "record"
                                    )
                                    
                                    // 4. 存入数据库
                                    viewModel.stopRecording(savedPath)
                                } else {
                                    viewModel.stopRecording(null)
                                }
                            }
                        } else {
                            viewModel.stopRecording(null)
                        }
                    } else {
                        // === 开始录制流程 ===
                        viewModel.startRecording()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isRecording) Color(0xFFC62828) else Color(0xFF2E7D32)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.align(Alignment.CenterEnd).height(48.dp)
            ) {
                Text(if (uiState.isRecording) "停止" else "出发", fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(32.dp))
        ) {
            EcoMapView(
                modifier = Modifier.fillMaxSize(),
                pathPoints = viewModel.pathPoints,
                onMapLoaded = { map ->
                    baiduMapState = map
                    map.isMyLocationEnabled = true
                },
                onSnapshotAvailable = { mapView ->
                    mapViewState = mapView
                }
            )
            // 调试信息
            if (uiState.debugInfo.isNotEmpty()) {
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(bottomEnd = 16.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = uiState.debugInfo,
                        color = Color(0xFF69F0AE),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(12.dp),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}