package com.wzh.runtime

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

object RunTimeLogWriter {
    private const val MAX_FILE_SIZE_BYTES = 512*1024

    private var file: File ? = null

    private val lock = Any()

    fun init(context: Context){
        if (file!=null) return
        val dir = RunTimeTracer.logDir
            ?.let { File(it) }
            ?: context.getExternalFilesDir(null)
            ?: context.filesDir
        if (!dir.exists()) dir.mkdirs()
        val f = File(dir, RunTimeTracer.fileName)
        if (f.exists() && f.length() > MAX_FILE_SIZE_BYTES){
            val backup = File(dir,"${RunTimeTracer.fileName}.bak")
            if (backup.exists()) backup.delete()
            f.renameTo(backup)
        }

        file =f
    }


    fun writeLine(line: String){
        val f = file?: return
        synchronized(lock){
            try {
                PrintWriter(FileWriter(f,true)).use { it.println(line) }
            }catch (_: Throwable){}
        }
    }

    fun clear()=synchronized(lock){file?.delete()}

    fun readAll(): String{
        val f= file?:return ""
        synchronized(lock){
            return try {
                f.readText()
            }catch (_: Throwable){""}
        }
    }

    fun currentPath(): String = file?.absolutePath ?:""
}