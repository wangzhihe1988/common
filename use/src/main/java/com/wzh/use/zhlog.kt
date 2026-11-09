package com.wzh.use

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object zhlog {
    private const val MAX_FILE_SIZE_BYTES = 512*1024



    private val lock = Any()

    fun init(fileName : String): File?{
        var context = zh.context
        if (fileName.isEmpty() || context==null) return null
        var file: File ? = null
        val dir = context.getExternalFilesDir(null)
            ?: context.filesDir
        if (!dir.exists()) dir.mkdirs()
        val f = File(dir, fileName+".txt")
        if (f.exists() && f.length() > MAX_FILE_SIZE_BYTES){
            val backup = File(dir,"${fileName}.bak")
            if (backup.exists()) backup.delete()
            f.renameTo(backup)
        }
        file = f
        return file
    }


    fun logSave(name: String = "log", line: String){
        zh.log(line,name)
        val f = init(name)?: return
        val wallTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS:  ", Locale.US).format(Date())
        var logLine = buildString {
            append(wallTime)
            append(line)
            append("\n\n")
        }
        synchronized(lock){
            try {
                PrintWriter(FileWriter(f,true)).use { it.println(logLine) }
            }catch (_: Throwable){}
        }
    }

    fun clear(name: String = "log")=synchronized(lock){
        var file = init(name)
        file?.delete()
    }

    fun readAll(name: String = "log"): String{

        val f= init(name)?:return ""
        synchronized(lock){
            return try {
                f.readText()
            }catch (_: Throwable){""}
        }
    }

    fun currentPath(name: String = "log"): String = init(name)?.absolutePath ?:""

    fun String.save(name: String = "log") = logSave(line = this, name = name)
}