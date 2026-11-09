package com.wzh.use


import android.content.Context
import android.util.Log

class zh {

    companion object{
        fun init(context: Context,isdebug: Boolean = false) {
            this.context = Companion.context
            this.isDebug = isdebug
        }

        @JvmStatic
        var isDebug: Boolean = false
        @JvmStatic
        var context: Context? = null

        @JvmStatic
        fun log(str: String,tag: String = "wzh"){
            if (isDebug)
                Log.e(tag,str)
        }
    }
}