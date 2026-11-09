package com.wzh.common

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wzh.common.ui.theme.CommonTheme
import com.wzh.runtime.RunTimeLogWriter
import com.wzh.runtime.WZHRunTime
import com.wzh.use.zh
import com.wzh.use.zhlog.save

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        zh.init(this,true)
        RunTimeLogWriter.init(this)
        fakeWork()
        "ssff".save()
        setContent {
            CommonTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DebugPanel(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    "tett".save()
    zh.log("fsf")
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
@Composable
fun DebugPanel(modifier: Modifier = Modifier) {
    var logText by remember { mutableStateOf("点击按钮开始测试") }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = {
            // 方式一：trace{} 包裹（零织入依赖，立即可验证）
            val r1 = WZHRunTime.trace("trace-包裹测试") { fakeWorkInline() }

            // 方式二：注解 + 切面（需织入插件，见文末）
            val r2 = fakeWork()

            logText = "trace结果=$r1\n注解结果=$r2\n\n${com.wzh.runtime.RunTimeLogWriter.readAll()}"
        }) {
            Text("执行耗时测试")
        }

        Text(logText)
    }
}

private fun fakeWorkInline(): String {
    Thread.sleep(50)
    return "inline-done"
}
@WZHRunTime("模拟耗时操作")
private fun fakeWork(): String {
    Thread.sleep(120)
    return "done"
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CommonTheme {
        Greeting("Android")
    }
}