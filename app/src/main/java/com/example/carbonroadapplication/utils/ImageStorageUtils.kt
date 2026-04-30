package com.example.carbonroadapplication.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageStorageUtils {

    // 保存截图到内部存储，返回绝对路径
    fun saveTripSnapshot(context: Context, bitmap: Bitmap, username: String): String? {
        return try {
            // 1. 创建专属目录
            val directory = File(context.filesDir, "trip_records")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // 2. 生成唯一文件名 (时间戳 + 用户名，确保不重复)
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "TRIP_${username}_${timeStamp}.jpg"
            val file = File(directory, fileName)

            // 3. 写入文件
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream) // 80% 质量压缩，节省空间
            stream.flush()
            stream.close()

            // 4. 返回路径
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}