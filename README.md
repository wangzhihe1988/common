implementation("com.github.wangzhihe1988.common:use:1.0.1")
zh.isDebug = true
zh.log("fs")

implementation("com.github.wangzhihe1988.common:runtime:1.0.1")
RunTimeLogWriter.init(context)
val r2 = WZHRunTime.trace("通过反射调用 fakeWork") { fakeWork() }
