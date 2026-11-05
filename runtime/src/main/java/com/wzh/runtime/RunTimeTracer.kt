package com.wzh.runtime

import android.nfc.Tag
import android.os.SystemClock
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 耗时追踪管理（纯数据处理，不含反射调用）。
 */
object RunTimeTracer {
    private const val TAG = "WZHRunTime"

    /** 全局开关，生产环境置 false 关闭所有追踪 */
    val enabled: Boolean = true

    /** 自定义 log.txt 目录（留空则用 externalFilesDir） */
    var logDir: String? = null
    /** 日志文件名，默认 log.txt */
    var fileName: String = "time.txt"

    /***
     * 记录一次好事
     */
    fun log(lable: String,elapsendMs: Double,methodName: String =""){
        if (!enabled) return

        val wallTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
        val upTimeMs = SystemClock.elapsedRealtime()

        val logcatMsg = buildString {
            append("[WZHRunTime] ")
            append("lable=").append(lable)
            append(" time=").append(elapsendMs).append("ms")
            append(" wall").append(wallTime)
            append(" uptime").append(upTimeMs).append("ms")
            if (methodName.isNotBlank()) append(" method=").append(methodName)
        }
        Log.e(TAG,logcatMsg)
        RunTimeLogWriter.writeLine(logcatMsg)
    }
}