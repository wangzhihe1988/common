plugins {
    alias(libs.plugins.android.library)
    `maven-publish`   // 添加这一行
}

android {
    namespace = "com.wzh.use"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // 关键：显式告诉 AGP 注册 "release" 这个软件组件
    publishing {
        singleVariant("release") {
            // 强烈建议加上这行，它会自动生成并附带源码 JAR
            withSourcesJar()
        }
    }
}
afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.wangzhihe1988"   // 必须严格按此格式
                artifactId = "use"                 // 通常与模块名一致
                version = "1.0.4"                        // 与后续 GitHub Release 的 Tag 对应
            }
        }
    }
}
dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}