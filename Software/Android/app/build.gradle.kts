plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // WebSocket通信库 - OkHttp是一个强大的HTTP和WebSocket客户端库
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // EventBus - 用于模块间的消息传递（事件总线）
    // 这是一个轻量级的发布/订阅事件总线，让不同模块之间可以方便地通信
    implementation("org.greenrobot:eventbus:3.3.1")

    // Gson - 用于JSON数据的解析和生成
    // 可以方便地把Java对象转换成JSON字符串，或者把JSON字符串转换成Java对象
    implementation("com.google.code.gson:gson:2.10.1")
}