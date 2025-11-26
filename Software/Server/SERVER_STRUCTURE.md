# 宠物项圈服务器端项目结构文档（C++）

## 项目概述
基于C++ WebSocket的轻量级服务器，负责Android客户端与硬件设备之间的通信转发、用户认证管理。

---

## 技术栈
- **语言**: C++17
- **WebSocket**: uWebSockets / Boost.Beast
- **JSON**: nlohmann/json
- **数据库**: SQLite3
- **构建**: CMake 3.15+
- **线程**: std::thread

---

## 目录结构

```
Server/
├── CMakeLists.txt                  # CMake构建配置
├── README.md                       # 项目说明
├── main.cpp                        # 程序入口
│
├── core/                           # 核心模块
│   ├── websocket_server/
│   │   ├── websocket_server.h      # WebSocket服务器头文件
│   │   └── websocket_server.cpp    # WebSocket服务器实现
│   │       ├── 监听8081端口
│   │       ├── 处理连接/断开
│   │       ├── 接收/发送消息
│   │       └── 区分客户端类型（Android/硬件）
│   │
│   ├── client_manager/
│   │   ├── client_manager.h        # 客户端管理头文件
│   │   └── client_manager.cpp      # 客户端管理实现
│   │       ├── 维护Android客户端列表
│   │       ├── 维护硬件客户端连接
│   │       ├── 广播消息到Android
│   │       └── 发送消息到硬件
│   │
│   └── health_checker/
│       ├── health_checker.h        # 健康检查头文件
│       └── health_checker.cpp      # 健康检查实现
│           ├── 独立线程运行
│           ├── 每30秒检查连接
│           ├── 发送心跳包
│           └── 超时断开连接
│
├── message/                        # 消息处理模块
│   ├── message_router/
│   │   ├── message_router.h        # 消息路由头文件
│   │   └── message_router.cpp      # 消息路由实现
│   │       ├── 解析JSON消息
│   │       ├── 识别消息类型
│   │       └── 分发到对应Handler
│   │
│   ├── auth_handler/
│   │   ├── auth_handler.h          # 认证处理头文件
│   │   └── auth_handler.cpp        # 认证处理实现
│   │       ├── handleLogin()       # 处理登录请求
│   │       ├── handleRegister()    # 处理注册请求
│   │
│   ├── gps_handler/
│   │   ├── gps_handler.h           # GPS处理头文件
│   │   └── gps_handler.cpp         # GPS处理实现
│   │       ├── 接收硬件GPS数据
│   │       ├── 转发给Android客户端
│   │
│   ├── video_handler/
│   │   ├── video_handler.h         # 视频处理头文件
│   │   └── video_handler.cpp       # 视频处理实现
│   │       ├── 接收硬件视频帧
│   │       ├── 转发给Android客户端
│   │
│   └── control_handler/
│       ├── control_handler.h       # 控制处理头文件
│       └── control_handler.cpp     # 控制处理实现
│           ├── 接收Android控制指令
│           ├── 转发给硬件设备
│           └── 可扩展：宏命令、定时任务
│
├── database/                       # 数据库模块
│   ├── user_manager/
│   │   ├── user_manager.h          # 用户管理头文件
│   │   └── user_manager.cpp        # 用户管理实现
│   │       ├── SQLite数据库操作
│   │       ├── registerUser()      # 注册用户
│   │       ├── verifyUser()        # 验证登录
│   │       └── hashPassword()      # 密码哈希
│   │
│   └── token_manager/
│       ├── token_manager.h         # Token管理头文件
│       └── token_manager.cpp       # Token管理实现
│           ├── generateToken()     # 生成Token
│           ├── verifyToken()       # 验证Token
│           └── removeToken()       # 移除Token
│
├── utils/                          # 工具模块
│   ├── logger/
│   │   ├── logger.h                # 日志系统头文件
│   │   └── logger.cpp              # 日志系统实现
│   │       ├── 分级日志（DEBUG/INFO/WARN/ERROR/FATAL）
│   │       ├── 按日期分文件
│   │       ├── 错误单独记录
│   │       ├── 崩溃日志记录
│   │       └── 自动轮转（保留30天）
│   │
│   ├── exception_handler/
│   │   ├── exception_handler.h     # 异常处理头文件
│   │   └── exception_handler.cpp   # 异常处理实现
│   │       ├── safe_execute()      # 安全执行包装
│   │       ├── 捕获异常记录日志
│   │       ├── 隔离模块防止连锁崩溃
│   │       └── 自动重连机制
│   │
│   └── common/
│       ├── common.h                # 通用工具头文件
│       └── common.cpp              # 通用工具实现
│           ├── base64编解码
│           ├── 字符串处理
│           └── 时间工具
│
├── data/                           # 数据文件目录
│   ├── users.db                    # SQLite用户数据库
│   ├── config.json                 # 服务器配置文件
│   └── logs/                       # 日志目录
│       ├── server_2025-11-25.log   # 服务器日志
│       ├── error_2025-11-25.log    # 错误日志
│       └── crash_2025-11-25.log    # 崩溃日志
│
├── third_party/                    # 第三方库
│   ├── json.hpp                    # nlohmann/json
│   └── uWebSockets/                # WebSocket库
│
└── build/                          # 编译输出目录
```

---

## 模块说明

### 1. 核心模块 (core/)

#### WebSocket服务器 (websocket_server/)
- 监听8081端口
- 处理客户端连接/断开
- 接收/发送WebSocket消息
- 区分Android客户端和硬件客户端

#### 客户端管理 (client_manager/)
- 维护所有连接的客户端列表
- Android客户端：`map<userId, WebSocket*>`
- 硬件客户端：`WebSocket* hardwareClient_`
- 提供广播和单播功能

#### 健康检查 (health_checker/)
- 独立线程运行
- 每30秒检查所有连接
- 发送心跳包，检测超时
- 自动断开失效连接

---

### 2. 消息处理模块 (message/)

#### 消息路由 (message_router/)
- 解析JSON消息
- 根据`type`字段分发到对应Handler
- 统一的消息入口

#### 认证处理 (auth_handler/)
- 处理登录请求：验证用户名密码，生成Token
- 处理注册请求：创建新用户
- 可扩展：找回密码、修改密码、第三方登录

#### GPS处理 (gps_handler/)
- 接收硬件发送的GPS数据
- 转发给所有Android客户端
- 可扩展：轨迹记录、地理围栏报警

#### 视频处理 (video_handler/)
- 接收硬件发送的视频帧（MJPEG）
- 转发给所有Android客户端
- 可扩展：视频录制、截图、滤镜处理

#### 控制处理 (control_handler/)
- 接收Android发送的控制指令
- 转发给硬件设备
- 可扩展：宏命令、定时任务、权限控制

---

### 3. 数据库模块 (database/)

#### 用户管理 (user_manager/)
- SQLite数据库操作
- 用户注册：插入users表
- 用户登录：查询验证
- 密码哈希：安全存储

**数据库表结构**：
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Token管理 (token_manager/)
- 生成Token：随机字符串或JWT
- 验证Token：查询缓存或数据库
- Token缓存：`map<token, userId>`
- Token过期管理

---

### 4. 工具模块 (utils/)

#### 日志系统 (logger/)
- **分级日志**：DEBUG, INFO, WARN, ERROR, FATAL
- **按日期分文件**：`server_YYYY-MM-DD.log`
- **错误单独记录**：`error_YYYY-MM-DD.log`
- **崩溃日志**：`crash_YYYY-MM-DD.log`
- **自动轮转**：保留最近30天

**使用示例**：
```cpp
Logger::info("客户端连接: {}", clientId);
Logger::error("数据库错误: {}", e.what());
Logger::fatal("服务器崩溃: {}", reason);
```

#### 异常处理 (exception_handler/)
- **安全执行包装**：`safe_execute(func, moduleName)`
- **异常捕获**：捕获所有异常并记录
- **模块隔离**：一个模块崩溃不影响其他
- **自动恢复**：记录错误后继续运行

**使用示例**：
```cpp
ExceptionHandler::safe_execute([&]() {
    // 可能抛出异常的代码
}, "模块名称");
```

#### 通用工具 (common/)
- Base64编解码
- 字符串处理
- 时间格式化
- 随机数生成

---

## 线程架构

```
主线程
    ├── WebSocket监听线程
    │   └── 接收消息 → 投递到消息队列
    │
    ├── 消息处理线程池 (4个线程)
    │   └── 从队列取消息 → 调用Handler → 返回结果
    │
    ├── 健康检查线程
    │   └── 每30秒检查所有连接 → 发送心跳 → 超时断开
    │
    └── 日志写入线程
        └── 异步写入日志文件
```

---

## 数据流设计

### 场景1: 用户登录
```
Android → WebSocket → {"type":"login", "username":"xxx", "password":"xxx"}
    ↓
WebSocketServer接收
    ↓
MessageRouter解析type="login"
    ↓
AuthHandler::handleLogin()
    ↓
UserManager::verifyUser() → 查询数据库
    ↓
TokenManager::generateToken() → 生成Token
    ↓
返回 → {"type":"login_response", "code":200, "token":"xxx"}
    ↓
ClientManager::addAndroidClient() → 记录连接
```

### 场景2: GPS数据转发
```
硬件 → WebSocket → {"type":"gps", "lat":39.9, "lng":116.4}
    ↓
WebSocketServer接收
    ↓
MessageRouter解析type="gps"
    ↓
GpsHandler::handle()
    ↓
ClientManager::broadcastToAndroid() → 转发给所有Android
    ↓
Android收到GPS数据 → 更新地图
```

### 场景3: 控制指令转发
```
Android → WebSocket → {"type":"control", "command":"UP"}
    ↓
WebSocketServer接收
    ↓
MessageRouter解析type="control"
    ↓
ControlHandler::handle()
    ↓
ClientManager::sendToHardware() → 转发给硬件
    ↓
硬件收到指令 → 执行动作
```

### 场景4: 视频流转发
```
硬件 → WebSocket → {"type":"video", "frame":"base64..."}
    ↓
WebSocketServer接收
    ↓
MessageRouter解析type="video"
    ↓
VideoHandler::handle()
    ↓
ClientManager::broadcastToAndroid() → 转发给所有Android
    ↓
Android收到视频帧 → 解码显示
```

---

## 异常处理机制

### 模块隔离
```
消息到达
    ↓
try {
    MessageRouter解析
        ↓
    分发到Handler
        ↓
    Handler处理
        ↓
    返回结果
}
catch (Exception e) {
    Logger::error("模块错误: {}", e.what())
    记录到crash.log
    返回错误消息
    其他模块继续运行
}
```

### 崩溃恢复
- 每个Handler独立运行
- Handler崩溃 → 记录日志 → 返回错误 → 其他Handler继续
- WebSocket连接断开 → 自动清理 → 等待重连
- 数据库错误 → 记录日志 → 返回错误 → 不影响其他操作

---

## 配置文件 (data/config.json)

```json
{
    "server": {
        "port": 8081,
        "max_connections": 1000,
        "heartbeat_interval": 30
    },
    "database": {
        "path": "data/users.db"
    },
    "log": {
        "level": "INFO",
        "path": "data/logs/",
        "max_days": 30
    }
}
```

---

## WebSocket消息协议

### Android → 服务器

#### 登录
```json
{
    "type": "login",
    "username": "user123",
    "password": "pass123"
}
```

#### 注册
```json
{
    "type": "register",
    "username": "user123",
    "password": "pass123"
}
```

#### 控制指令
```json
{
    "type": "control",
    "command": "UP"
}
```

### 服务器 → Android

#### 登录响应
```json
{
    "type": "login_response",
    "code": 200,
    "message": "登录成功",
    "token": "xxx",
    "userId": "xxx"
}
```

#### GPS数据
```json
{
    "type": "gps",
    "lat": 39.9042,
    "lng": 116.4074,
    "time": 1234567890
}
```

#### 视频帧
```json
{
    "type": "video",
    "frame": "base64编码的JPEG"
}
```

### 硬件 → 服务器

#### GPS数据
```json
{
    "type": "gps",
    "lat": 39.9042,
    "lng": 116.4074,
    "time": 1234567890
}
```

#### 视频帧
```json
{
    "type": "video",
    "frame": "base64编码的JPEG"
}
```

### 服务器 → 硬件

#### 控制指令
```json
{
    "type": "control",
    "command": "UP"
}
```

---

## 编译与运行

### 1. 安装依赖
```bash
# Ubuntu/Debian
sudo apt-get install cmake g++ libsqlite3-dev

# macOS
brew install cmake sqlite3

# Windows (vcpkg)
vcpkg install sqlite3
```

### 2. 下载第三方库
```bash
# nlohmann/json
wget https://raw.githubusercontent.com/nlohmann/json/develop/single_include/nlohmann/json.hpp -O third_party/json.hpp
```

### 3. 编译
```bash
mkdir build
cd build
cmake ..
make
```

### 4. 运行
```bash
./pet_collar_server
```

---

## 性能优化

1. **连接池管理**：限制最大连接数1000
2. **消息队列**：缓冲高频消息，避免阻塞
3. **线程池**：4个工作线程处理消息
4. **Token缓存**：内存缓存减少数据库查询
5. **日志异步写入**：独立线程写日志文件

---

## 安全建议

1. **密码加密**：使用bcrypt或SHA256+盐
2. **Token过期**：设置24小时有效期
3. **限流**：防止暴力破解（登录失败5次锁定）
4. **输入验证**：严格验证所有输入数据
5. **WSS加密**：生产环境使用WSS（WebSocket Secure）

---

## 待实现功能

### 已完成 ✅
- [x] 项目结构搭建
- [x] 所有模块头文件和源文件框架
- [x] CMakeLists.txt配置
- [x] 日志系统框架
- [x] 异常处理框架

### 待实现 ⏳
- [ ] WebSocket服务器实现（集成uWebSockets）
- [ ] SQLite数据库初始化
- [ ] 用户注册/登录逻辑
- [ ] Token生成与验证
- [ ] 消息路由完整实现
- [ ] 各Handler具体逻辑
- [ ] 健康检查心跳机制
- [ ] 日志文件写入
- [ ] 配置文件解析
- [ ] 单元测试

---


