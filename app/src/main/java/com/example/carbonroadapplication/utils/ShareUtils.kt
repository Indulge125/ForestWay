package com.example.carbonroadapplication.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.carbonroadapplication.data.TripEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ShareUtils {

    /**
     * 生成"底部面板风格"的分享图
     * 地图在上，数据在下，互不遮挡
     */
    fun generateTripImage(context: Context, mapBitmap: Bitmap, trip: TripEntity): Bitmap {
        val width = mapBitmap.width
        val height = mapBitmap.height

        // 1. 创建画布
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        // 2. 绘制地图底图 (全屏绘制)
        canvas.drawBitmap(mapBitmap, 0f, 0f, null)

        // 3. 定义底部面板的高度 (占总高度的 30% ~ 35%)
        val panelHeight = height * 0.32f
        val panelTop = height - panelHeight

        val paint = Paint().apply { isAntiAlias = true }

        // --- 绘制底部白色背景 ---
        paint.color = Color.WHITE
        // 给顶部边缘加一点阴影，更有层次感
        paint.setShadowLayer(20f, 0f, -5f, Color.parseColor("#40000000"))

        // 绘制矩形 (左, 上, 右, 下)
        canvas.drawRect(0f, panelTop, width.toFloat(), height.toFloat(), paint)
        paint.clearShadowLayer()

        // --- 绘制装饰性绿色顶条 (可选，增加美感) ---
        paint.color = Color.parseColor("#2E7D32") // 森林绿
        canvas.drawRect(0f, panelTop, width.toFloat(), panelTop + 10f, paint)

        // --- 绘制内容 ---
        val centerX = width / 2f
        val contentStartY = panelTop + 80f // 内容起始 Y 坐标

        // 1. 标题 "森途 - 行程完成"
        paint.color = Color.BLACK
        paint.textSize = 50f
        paint.textAlign = Paint.Align.CENTER
        paint.isFakeBoldText = true
        canvas.drawText("森途 - 行程完成", centerX, contentStartY, paint)

        // 2. 日期
        paint.textSize = 30f
        paint.color = Color.GRAY
        paint.isFakeBoldText = false
        val dateStr = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(trip.timestamp))
        canvas.drawText(dateStr, centerX, contentStartY + 50f, paint)

        // 3. 核心数据 (左右布局)
        val dataY = contentStartY + 160f
        val colOffset = width * 0.25f // 左右各偏移 1/4

        // 左侧：里程 (通常大家更关心里程，放左边)
        // 数值
        paint.color = Color.parseColor("#2E7D32") // 绿字
        paint.textSize = 80f
        paint.isFakeBoldText = true
        canvas.drawText(String.format("%.2f", trip.distanceMeters / 1000.0), centerX - colOffset, dataY, paint)
        // 单位
        paint.textSize = 30f
        paint.isFakeBoldText = false
        canvas.drawText("km", centerX - colOffset + 110f, dataY, paint)
        // 标签
        paint.color = Color.GRAY
        paint.textSize = 32f
        canvas.drawText("里程", centerX - colOffset, dataY + 50f, paint)

        // 右侧：减排
        // 数值
        paint.color = Color.parseColor("#2E7D32")
        paint.textSize = 80f
        paint.isFakeBoldText = true
        canvas.drawText(String.format("%.3f", trip.carbonSaved), centerX + colOffset, dataY, paint)
        // 单位
        paint.textSize = 30f
        paint.isFakeBoldText = false
        canvas.drawText("kg", centerX + colOffset + 110f, dataY, paint)
        // 标签
        paint.color = Color.GRAY
        paint.textSize = 32f
        canvas.drawText("减排 CO2", centerX + colOffset, dataY + 50f, paint)

        // 4. 底部 Slogan
        paint.color = Color.LTGRAY
        paint.textSize = 24f
        paint.isFakeBoldText = false
        canvas.drawText("每一次出发，都是对地球的告白", centerX, height - 40f, paint)

        return result
    }

    /**
     * 分享功能
     */
    fun shareTripImage(context: Context, mapBitmap: Bitmap, trip: TripEntity) {
        val finalBitmap = generateTripImage(context, mapBitmap, trip)
        val uri = saveBitmapToCache(context, finalBitmap)

        if (uri != null) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "分享行程"))
        } else {
            Toast.makeText(context, "图片生成失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
        val imagesFolder = File(context.cacheDir, "images")
        if (!imagesFolder.exists()) imagesFolder.mkdirs()
        val file = File(imagesFolder, "share_${System.currentTimeMillis()}.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        stream.flush()
        stream.close()
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}