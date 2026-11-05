package com.wzh.use


import android.util.Log

class zh {

    companion object{
        @JvmStatic
        var isDebug: Boolean = false


        @JvmStatic
        fun log(str: String,tag: String = "wzh"){
            if (isDebug)
                Log.e(tag,str)
        }
    }
}