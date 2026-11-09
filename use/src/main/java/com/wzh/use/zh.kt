package com.wzh.use


import android.content.Context
import android.util.Log

object zh {

    fun init(context: Context,isdebug: Boolean = false) {
        this.context = context
        this.isDebug = isdebug
    }


    var isDebug: Boolean = false

    var context: Context? = null


    fun log(str: String,tag: String = "wzh"){
        if (isDebug)
            Log.e(tag,str)
    }
}