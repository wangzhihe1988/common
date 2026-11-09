package com.wzh.use

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

object zhimg{
    /** Otsu 大津法：自动求全局最佳二值化阈值 */
    private fun otsuThreshold(gray: IntArray): Int {
        val hist = IntArray(256)
        for (v in gray) hist[v]++
        val total = gray.size
        var sum = 0.0
        for (i in 0..255) sum += i * hist[i]

        var sumB = 0.0
        var wB = 0
        var maxVar = 0.0
        var threshold = 0
        for (t in 0..255) {
            wB += hist[t]
            if (wB == 0) continue
            val wF = total - wB
            if (wF == 0) break
            sumB += t * hist[t]
            val mB = sumB / wB
            val mF = (sum - sumB) / wF
            val between = wB.toDouble() * wF * (mB - mF) * (mB - mF)
            if (between > maxVar) {
                maxVar = between
                threshold = t
            }
        }
        return threshold
    }

    /** @param threshold 灰度 > threshold 变白，否则变黑；阈值越高，灰色越容易被判黑 */
    fun binarize(src: Bitmap, threshold: Int = 220): Bitmap {
        val w = src.width
        val h = src.height
        val pixels = IntArray(w * h)
        src.getPixels(pixels, 0, w, 0, 0, w, h)

        for (i in pixels.indices) {
            val c = pixels[i]
            val r = (c shr 16) and 0xFF
            val g = (c shr 8) and 0xFF
            val b = c and 0xFF
            val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()

            // 只有接近纯白才保留白，其余（含灰色）全部变黑
            val v = if (gray > threshold) 255 else 0
            pixels[i] = Color.rgb(v, v, v)
        }
        return Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888)
    }



    /**
     * 保存 Bitmap 到系统相册（Pictures 目录）
     * @return 是否成功
     */
    fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        fileName: String = "ocr_${System.currentTimeMillis()}.png",
        quality: Int = 100
    ): Boolean {
        val mimeType = "image/png"
        val relativePath = "${Environment.DIRECTORY_PICTURES}/MyOcrApp"

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+：通过 MediaStore 写入，无需权限
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                    put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
                    put(MediaStore.Images.Media.IS_PENDING, 1) // 写入期间对其他 App 隐藏
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: return false

                resolver.openOutputStream(uri)?.use { out ->
                    // PNG 无损，quality 参数被忽略；JPEG 时 quality 生效
                    bitmap.compress(Bitmap.CompressFormat.PNG, quality, out)
                }

                // 写入完成，解除 pending 状态，相册中可见
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                true
            } else {
                // Android 9 及以下：需要 WRITE_EXTERNAL_STORAGE 权限
                val dir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "MyOcrApp"
                )
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, fileName)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, quality, out)
                }
                // 通知相册刷新（旧系统必须）
                MediaStore.Images.Media.insertImage(
                    context.contentResolver, file.absolutePath, fileName, null
                )
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    fun Bitmap.saveToGallery(){
        var context = zh.context ?: return
        saveBitmapToGallery(context,this)
    }

    fun Bitmap.binarize(threshold: Int = 220): Bitmap = binarize(this,threshold)
}