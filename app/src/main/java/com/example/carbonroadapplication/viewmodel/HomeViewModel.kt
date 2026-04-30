package com.example.carbonroadapplication.viewmodel

import android.location.Location
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidu.location.BDLocation
import com.baidu.mapapi.model.LatLng
import com.example.carbonroadapplication.data.TripDao
import com.example.carbonroadapplication.data.TripEntity
import com.example.carbonroadapplication.utils.SessionManager // 【新增】导入 SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val totalDistanceMeters: Double = 0.0,
    val carbonSaved: Double = 0.0,
    val isRecording: Boolean = false,
    val debugInfo: String = "等待GPS...",
    val currentLatLng: LatLng? = null,
    val accuracy: Float = 0f,
    val direction: Float = 0f,
    val showSummaryDialog: Boolean = false,
    val lastTripResult: TripEntity? = null,
    // 【关键】使用时间戳作为“强制复位”的信号，每次变动都会触发 UI 响应
    val recenterTrigger: Long = 0L
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tripDao: TripDao,
    private val sessionManager: SessionManager // 【新增】注入 SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    // 轨迹列表
    val pathPoints = mutableStateListOf<LatLng>()
    private var lastPoint: LatLng? = null

    // 接收定位更新
    fun updateLocation(location: BDLocation) {
        // 1. 剔除无效 0.0 坐标和低精度坐标(精度>100米通常是基站定位，不准，跳过)
        if (location.latitude == 4.9E-324 || location.radius > 100) return

        val currentPoint = LatLng(location.latitude, location.longitude)
        var debugText = "Lat:${location.latitude}\n精度:${location.radius}米"

        var newTotalDistance = _uiState.value.totalDistanceMeters
        var newCarbon = _uiState.value.carbonSaved

        // 2. 只有录制中才进行距离计算
        if (_uiState.value.isRecording) {
            if (lastPoint != null) {
                val distance = calculateDistance(lastPoint!!, currentPoint)
                // 【防漂移过滤】
                // 1. 距离 > 2米 (过滤原地微颤)
                // 2. 距离 < 50米 (过滤瞬间漂移几公里的异常点)
                if (distance > 2.0 && distance < 50.0) {
                    newTotalDistance += distance
                    newCarbon = (newTotalDistance / 1000.0) * 0.2

                    // 加入轨迹列表
                    pathPoints.add(currentPoint)
                    // 更新上一个点
                    lastPoint = currentPoint

                    debugText = "录制中 (速度:${String.format("%.1f", location.speed)}m/s)\n$debugText"
                } else {
                    debugText = "录制中 (过滤噪点)\n$debugText"
                }
            } else {
                // 起点
                pathPoints.add(currentPoint)
                lastPoint = currentPoint
                debugText = "录制开始\n$debugText"
            }
        } else {
            debugText = "等待出发\n$debugText"
        }

        _uiState.value = _uiState.value.copy(
            debugInfo = debugText,
            currentLatLng = currentPoint,
            accuracy = location.radius,
            direction = location.direction,
            totalDistanceMeters = newTotalDistance,
            carbonSaved = newCarbon
        )
    }

    // 开始
    fun startRecording() {
        pathPoints.clear()
        lastPoint = null
        _uiState.value = _uiState.value.copy(
            isRecording = true,
            totalDistanceMeters = 0.0,
            carbonSaved = 0.0,
            debugInfo = "初始化...",
            // 触发一次地图回正
            recenterTrigger = System.currentTimeMillis()
        )
    }

    // 停止
    // 【修改】接收 screenshotPath 参数
    fun stopRecording(screenshotPath: String?) {
        if (!_uiState.value.isRecording) return

        val finalDistance = _uiState.value.totalDistanceMeters
        val finalCarbon = _uiState.value.carbonSaved
        val timestamp = System.currentTimeMillis()
        
        // 获取当前用户名，如果没有则默认为空字符串（理论上不可能，因为不登录进不来）
        val currentUser = sessionManager.getUsername() ?: ""

        // 构建实体时存入路径
        val trip = TripEntity(
            timestamp = timestamp,
            distanceMeters = finalDistance,
            carbonSaved = finalCarbon,
            username = currentUser,
            screenshotPath = screenshotPath // 【新增】
        )

        viewModelScope.launch {
            tripDao.insertTrip(trip)
        }

        _uiState.value = _uiState.value.copy(
            isRecording = false,
            showSummaryDialog = true,
            lastTripResult = trip
        )
        lastPoint = null
    }

    // 关闭弹窗并复位
    fun dismissDialog() {
        // 清空轨迹
        pathPoints.clear()
        // 重置所有状态
        _uiState.value = _uiState.value.copy(
            showSummaryDialog = false,
            totalDistanceMeters = 0.0,
            carbonSaved = 0.0,
            lastTripResult = null,
            // 【关键】更新 Trigger，强制 HomeScreen 监听到变化并复位视角
            recenterTrigger = System.currentTimeMillis()
        )
    }

    private fun calculateDistance(p1: LatLng, p2: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results)
        return results[0].toDouble()
    }
}