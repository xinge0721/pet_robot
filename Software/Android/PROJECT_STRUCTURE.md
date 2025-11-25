# 宠物项圈Android端项目结构文档

## 项目概述
基于WebSocket的宠物项圈Android客户端，实现GPS定位显示、视频监控、远程控制功能。

---

## 技术栈
- **语言**: Java
- **架构**: MVVM
- **通信**: WebSocket (OkHttp)
- **地图**: 高德地图SDK
- **视频**: MJPEG (ImageView显示)
- **异步**: ExecutorService + Handler

---

## 目录结构

```
app/src/main/
├── java/com/example/myapplication/
│   │
│   ├── data/                          # 数据层
│   │   ├── model/                     # 数据模型
│   │   │   ├── User.java              # 用户信息模型
│   │   │   ├── LoginRequest.java      # 登录请求模型
│   │   │   ├── LoginResponse.java     # 登录响应模型
│   │   │   ├── GpsData.java           # GPS数据模型
│   │   │   └── ControlCommand.java    # 控制指令枚举
│   │   │
│   │   ├── network/                   # 网络层
│   │   │   ├── AuthApi.java           # 登录/注册API
│   │   │   └── WebSocketClient.java   # WebSocket客户端
│   │   │
│   │   └── prefs/                     # 本地存储
│   │       └── UserPreferences.java   # 用户信息存储（SharedPreferences）
│   │
│   ├── ui/                            # UI层
│   │   ├── auth/                      # 认证模块
│   │   │   ├── SplashActivity.java    # 启动页（检查登录状态）
│   │   │   ├── LoginActivity.java     # 登录页
│   │   │   └── RegisterActivity.java  # 注册页
│   │   │
│   │   ├── main/                      # 主界面模块
│   │   │   ├── MainActivity.java      # 主界面（容器）
│   │   │   └── MainViewModel.java     # 主界面ViewModel
│   │   │
│   │   ├── map/                       # 地图模块
│   │   │   ├── MapFragment.java       # 地图Fragment
│   │   │   └── MapViewModel.java      # 地图ViewModel
│   │   │
│   │   ├── video/                     # 视频模块
│   │   │   ├── VideoFragment.java     # 视频Fragment
│   │   │   └── VideoViewModel.java    # 视频ViewModel
│   │   │
│   │   └── widget/                    # 自定义控件
│   │       └── DirectionControlView.java  # 方向控制键
│   │
│   └── utils/                         # 工具类
│       ├── Constants.java             # 常量定义
│       └── ThreadUtils.java           # 线程工具
│
└── res/                               # 资源文件
    ├── layout/                        # 布局文件
    │   ├── activity_splash.xml        # 启动页布局
    │   ├── activity_login.xml         # 登录页布局
    │   ├── activity_register.xml      # 注册页布局
    │   ├── activity_main.xml          # 主界面布局
    │   ├── fragment_map.xml           # 地图Fragment布局
    │   └── fragment_video.xml         # 视频Fragment布局
    │
    ├── values/                        # 值资源
    │   ├── strings.xml                # 字符串资源
    │   ├── colors.xml                 # 颜色资源
    │   └── themes.xml                 # 主题资源
    │
    └── drawable/                      # 图片资源
```

---

## 模块说明

### 1. 数据层 (data/)

#### 数据模型 (model/)
- **User.java**: 用户信息（userId, username, token）
- **LoginRequest.java**: 登录请求体
- **LoginResponse.java**: 登录响应体
- **GpsData.java**: GPS数据（经纬度、时间戳、精度）
- **ControlCommand.java**: 控制指令枚举（UP/DOWN/LEFT/RIGHT/STOP）

#### 网络层 (network/)
- **HttpClient.java**: HTTP请求工具类（已存在，用于登录/注册）
- **AuthApi.java**: 认证API（登录/注册接口封装）
- **WebSocketClient.java**: WebSocket客户端（接收GPS/视频，发送控制指令）

#### 本地存储 (prefs/)
- **UserPreferences.java**: 用户信息本地存储（token、userId等）

---

### 2. UI层 (ui/)

#### 认证模块 (auth/)
- **SplashActivity**: 启动页，检查登录状态，跳转到登录页或主界面
- **LoginActivity**: 登录页，输入用户名密码，调用登录API
- **RegisterActivity**: 注册页，输入用户名密码，调用注册API

#### 主界面模块 (main/)
- **MainActivity**: 主界面容器，包含TabLayout切换地图/视频，底部显示方向控制
- **MainViewModel**: 主界面ViewModel，管理WebSocket连接

#### 地图模块 (map/)
- **MapFragment**: 地图显示Fragment，集成高德地图
- **MapViewModel**: 地图ViewModel，接收GPS数据，更新地图标记

#### 视频模块 (video/)
- **VideoFragment**: 视频显示Fragment，显示MJPEG视频流
- **VideoViewModel**: 视频ViewModel，接收视频帧数据

#### 自定义控件 (widget/)
- **DirectionControlView**: 自定义方向控制键（上下左右停止）

---

### 3. 工具类 (utils/)
- **Constants.java**: 常量定义（服务器地址、端口等）
- **ThreadUtils.java**: 线程工具（后台线程、主线程切换）

---

## 界面流程

```
启动App
    ↓
SplashActivity（检查登录状态）
    ├─→ 已登录 → MainActivity
    │              ├─ 连接WebSocket
    │              ├─ TabLayout切换
    │              │   ├─ MapFragment（显示GPS位置）
    │              │   └─ VideoFragment（显示视频流）
    │              └─ DirectionControlView（发送控制指令）
    │
    └─→ 未登录 → LoginActivity
                    ├─ 登录成功 → MainActivity
                    └─ 注册链接 → RegisterActivity
```

---

## 数据流

### WebSocket通信协议

#### 服务器 → Android
```json
// GPS数据
{
    "type": "gps",
    "lat": 39.9042,
    "lng": 116.4074,
    "time": 1234567890
}

// 视频帧（MJPEG）
{
    "type": "video",
    "frame": "base64编码的JPEG图片"
}
```

#### Android → 服务器
```json
// 控制指令
{
    "type": "control",
    "command": "UP"  // UP/DOWN/LEFT/RIGHT/STOP
}
```

### HTTP API接口

#### 登录接口
```
POST /api/login
Content-Type: application/json

Request:
{
    "username": "user123",
    "password": "pass123"
}

Response:
{
    "code": 200,
    "message": "登录成功",
    "token": "xxx",
    "userId": "xxx"
}
```

#### 注册接口
```
POST /api/register
Content-Type: application/json

Request:
{
    "username": "user123",
    "password": "pass123"
}

Response:
{
    "code": 200,
    "message": "注册成功"
}
```

---

## 待实现功能清单

### 已完成 ✅
- [x] 项目结构搭建
- [x] 数据模型类
- [x] 用户信息存储
- [x] 登录/注册API
- [x] WebSocket客户端
- [x] 启动页
- [x] 登录页
- [x] 注册页
- [x] 基础布局文件

### 待实现 ⏳
- [ ] MainActivity实现
- [ ] MapFragment实现（集成高德地图）
- [ ] VideoFragment实现（MJPEG播放）
- [ ] DirectionControlView实现（自定义方向键）
- [ ] MainViewModel实现
- [ ] MapViewModel实现
- [ ] VideoViewModel实现
- [ ] Constants常量定义
- [ ] ThreadUtils工具类
- [ ] build.gradle依赖配置
- [ ] AndroidManifest.xml配置

---

## 依赖库（待添加到build.gradle）

```gradle
dependencies {
    // Android基础
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    // MVVM组件
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.6.2'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.6.2'

    // 高德地图
    implementation 'com.amap.api:map3d:9.7.0'
    implementation 'com.amap.api:location:6.3.0'

    // 网络通信
    implementation 'com.squareup.okhttp3:okhttp:4.11.0'

    // JSON解析
    implementation 'com.google.code.gson:gson:2.10.1'

    // ViewPager2
    implementation 'androidx.viewpager2:viewpager2:1.0.0'
}
```

---

## 权限配置（AndroidManifest.xml）

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

---

## 开发建议

1. **先完成登录流程**：确保用户认证功能正常
2. **再实现WebSocket连接**：测试与服务器的通信
3. **然后集成地图**：显示GPS位置
4. **最后实现视频和控制**：完成核心功能

---

## 服务器端要求

### WebSocket服务器
- 地址：`ws://server-ip:port?token=xxx`
- 支持token验证
- 推送GPS数据和视频帧
- 接收控制指令

### HTTP API服务器
- 提供登录接口：`POST /api/login`
- 提供注册接口：`POST /api/register`
- 返回JWT token

---

## 注意事项

1. **网络安全**：生产环境使用HTTPS和WSS
2. **权限申请**：Android 6.0+需要动态申请定位权限
3. **内存管理**：及时释放Bitmap，避免内存泄漏
4. **线程安全**：UI更新必须在主线程
5. **异常处理**：网络异常、解析异常要有友好提示

---

**文档版本**: v1.0
**更新日期**: 2025-11-25
