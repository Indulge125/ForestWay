package com.example.carbonroadapplication.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.OverlayOptions
import com.baidu.mapapi.map.PolylineOptions
import com.baidu.mapapi.model.LatLng

@Composable
fun EcoMapView(
    modifier: Modifier = Modifier,
    pathPoints: List<LatLng>,
    onMapLoaded: (BaiduMap) -> Unit = {},
    onSnapshotAvailable: ((MapView) -> Unit)? = null // 【新增】截图回调
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            showZoomControls(false)
            showScaleControl(false)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = {
            val baiduMap = mapView.map
            baiduMap.isMyLocationEnabled = true
            onMapLoaded(baiduMap)
            onSnapshotAvailable?.invoke(mapView) // 【新增】传递mapView用于截图
            mapView
        },
        modifier = modifier,
        update = { view ->
            val baiduMap = view.map

            // 每次数据更新时，只重绘折线，尽量不动其他图层
            baiduMap.clear() // 注意：clear会清除覆盖物，但不会清除 MyLocation 图层

            if (pathPoints.size >= 2) {
                val overlayOptions: OverlayOptions = PolylineOptions()
                    .width(14)
                    .color(0xFF2E7D32.toInt()) // 绿色轨迹
                    .points(pathPoints)
                    .dottedLine(false)

                baiduMap.addOverlay(overlayOptions)
            }
        }
    )
}