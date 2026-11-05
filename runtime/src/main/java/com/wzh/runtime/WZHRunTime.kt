package com.wzh.runtime


annotation class WZHRunTime (
    val label: String = "",
    val enable: Boolean = true
){
    companion object{
        /**
         * inline 函数的 fallback 方案：代码块包裹（零反射、零字节码依赖）。
         *
         * 用法： val data = WZHRunTime.trace("请求") { api.fetch() }
         */
        inline fun <T>trace(label: String,crossinline block:()->T):T{
            val start = System.nanoTime()
            return try {
                block()
            }finally {
                RunTimeTracer.log(label,(System.nanoTime()-start)/1_000_000.0)
            }
        }
    }
}