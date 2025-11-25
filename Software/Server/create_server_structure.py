import os

BASE_PATH = r"c:\Users\xinge\Desktop\Wheeled-legged_robot\Software\Server"

# 目录结构
dirs = [
    "core/websocket_server",
    "core/client_manager",
    "core/health_checker",
    "message/message_router",
    "message/auth_handler",
    "message/gps_handler",
    "message/video_handler",
    "message/control_handler",
    "database/user_manager",
    "database/token_manager",
    "utils/logger",
    "utils/exception_handler",
    "utils/common",
    "data/logs",
    "third_party",
    "build",
]

# 文件模板
files = {
    # main.cpp
    "main.cpp": """#include <iostream>
#include <thread>
#include "core/websocket_server/websocket_server.h"
#include "core/health_checker/health_checker.h"
#include "utils/logger/logger.h"
#include "utils/exception_handler/exception_handler.h"

int main() {
    try {
        Logger::init("data/logs/");
        Logger::info("服务器启动中...");

        WebSocketServer server(8081);
        HealthChecker healthChecker;

        std::thread serverThread([&]() {
            server.start();
        });

        std::thread healthThread([&]() {
            healthChecker.start();
        });

        Logger::info("WebSocket服务器运行在: ws://0.0.0.0:8081");
        Logger::info("按Ctrl+C退出...");

        serverThread.join();
        healthThread.join();

    } catch (const std::exception& e) {
        Logger::fatal("服务器启动失败: {}", e.what());
        return 1;
    }

    return 0;
}
""",

    # WebSocket服务器
    "core/websocket_server/websocket_server.h": """#pragma once
#include <string>
#include <functional>

class WebSocketServer {
public:
    WebSocketServer(int port);
    ~WebSocketServer();

    void start();
    void stop();

    void broadcast(const std::string& message);
    void sendTo(const std::string& clientId, const std::string& message);

private:
    void onConnection(void* ws);
    void onMessage(void* ws, const std::string& message);
    void onClose(void* ws);

    int port_;
    bool running_;
};
""",

    "core/websocket_server/websocket_server.cpp": """#include "websocket_server.h"
#include "../../utils/logger/logger.h"
#include "../../utils/exception_handler/exception_handler.h"
#include "../../message/message_router/message_router.h"

WebSocketServer::WebSocketServer(int port) : port_(port), running_(false) {
    Logger::info("WebSocketServer初始化，端口: {}", port);
}

WebSocketServer::~WebSocketServer() {
    stop();
}

void WebSocketServer::start() {
    ExceptionHandler::safe_execute([&]() {
        running_ = true;
        Logger::info("WebSocket服务器启动成功");

        // TODO: 实现WebSocket监听逻辑

    }, "WebSocketServer::start");
}

void WebSocketServer::stop() {
    running_ = false;
    Logger::info("WebSocket服务器已停止");
}

void WebSocketServer::onMessage(void* ws, const std::string& message) {
    ExceptionHandler::safe_execute([&]() {
        Logger::info("收到消息: {}", message);
        MessageRouter::route(ws, message);
    }, "WebSocketServer::onMessage");
}
""",

    # 客户端管理
    "core/client_manager/client_manager.h": """#pragma once
#include <map>
#include <string>
#include <mutex>

class ClientManager {
public:
    static ClientManager& instance();

    void addAndroidClient(const std::string& userId, void* ws);
    void setHardwareClient(void* ws);
    void removeClient(void* ws);

    void broadcastToAndroid(const std::string& message);
    void sendToHardware(const std::string& message);

    bool isConnected(void* ws);

private:
    ClientManager() = default;

    std::map<std::string, void*> androidClients_;
    void* hardwareClient_ = nullptr;
    std::mutex mutex_;
};
""",

    "core/client_manager/client_manager.cpp": """#include "client_manager.h"
#include "../../utils/logger/logger.h"

ClientManager& ClientManager::instance() {
    static ClientManager instance;
    return instance;
}

void ClientManager::addAndroidClient(const std::string& userId, void* ws) {
    std::lock_guard<std::mutex> lock(mutex_);
    androidClients_[userId] = ws;
    Logger::info("Android客户端连接: {}", userId);
}

void ClientManager::setHardwareClient(void* ws) {
    std::lock_guard<std::mutex> lock(mutex_);
    hardwareClient_ = ws;
    Logger::info("硬件客户端已连接");
}

void ClientManager::removeClient(void* ws) {
    std::lock_guard<std::mutex> lock(mutex_);

    for (auto it = androidClients_.begin(); it != androidClients_.end(); ++it) {
        if (it->second == ws) {
            Logger::info("Android客户端断开: {}", it->first);
            androidClients_.erase(it);
            return;
        }
    }

    if (hardwareClient_ == ws) {
        Logger::warn("硬件客户端断开");
        hardwareClient_ = nullptr;
    }
}

void ClientManager::broadcastToAndroid(const std::string& message) {
    std::lock_guard<std::mutex> lock(mutex_);
    for (const auto& [userId, ws] : androidClients_) {
        // TODO: 发送消息到ws
    }
}

void ClientManager::sendToHardware(const std::string& message) {
    std::lock_guard<std::mutex> lock(mutex_);
    if (hardwareClient_) {
        // TODO: 发送消息到hardwareClient_
    }
}

bool ClientManager::isConnected(void* ws) {
    std::lock_guard<std::mutex> lock(mutex_);

    for (const auto& [userId, client] : androidClients_) {
        if (client == ws) return true;
    }

    return hardwareClient_ == ws;
}
""",

    # 健康检查
    "core/health_checker/health_checker.h": """#pragma once
#include <atomic>
#include <thread>

class HealthChecker {
public:
    HealthChecker();
    ~HealthChecker();

    void start();
    void stop();

private:
    void checkLoop();
    void checkClient(void* ws);

    std::atomic<bool> running_;
    std::thread thread_;
    int interval_ = 30; // 秒
};
""",

    "core/health_checker/health_checker.cpp": """#include "health_checker.h"
#include "../../utils/logger/logger.h"
#include "../../core/client_manager/client_manager.h"
#include <chrono>

HealthChecker::HealthChecker() : running_(false) {
    Logger::info("HealthChecker初始化，检查间隔: {}秒", interval_);
}

HealthChecker::~HealthChecker() {
    stop();
}

void HealthChecker::start() {
    running_ = true;
    thread_ = std::thread(&HealthChecker::checkLoop, this);
    Logger::info("健康检查线程已启动");
}

void HealthChecker::stop() {
    running_ = false;
    if (thread_.joinable()) {
        thread_.join();
    }
    Logger::info("健康检查线程已停止");
}

void HealthChecker::checkLoop() {
    while (running_) {
        Logger::debug("执行健康检查...");

        // TODO: 遍历所有客户端，发送心跳包

        std::this_thread::sleep_for(std::chrono::seconds(interval_));
    }
}
""",

    # 消息路由
    "message/message_router/message_router.h": """#pragma once
#include <string>

class MessageRouter {
public:
    static void route(void* ws, const std::string& message);

private:
    static void handleAuth(void* ws, const std::string& type, const std::string& data);
    static void handleGps(void* ws, const std::string& data);
    static void handleVideo(void* ws, const std::string& data);
    static void handleControl(void* ws, const std::string& data);
};
""",

    "message/message_router/message_router.cpp": """#include "message_router.h"
#include "../../utils/logger/logger.h"
#include "../../utils/exception_handler/exception_handler.h"
#include "../auth_handler/auth_handler.h"
#include "../gps_handler/gps_handler.h"
#include "../video_handler/video_handler.h"
#include "../control_handler/control_handler.h"

void MessageRouter::route(void* ws, const std::string& message) {
    ExceptionHandler::safe_execute([&]() {
        // TODO: 解析JSON，获取type字段
        std::string type = ""; // 从message解析

        if (type == "login" || type == "register") {
            handleAuth(ws, type, message);
        } else if (type == "gps") {
            handleGps(ws, message);
        } else if (type == "video") {
            handleVideo(ws, message);
        } else if (type == "control") {
            handleControl(ws, message);
        } else {
            Logger::warn("未知消息类型: {}", type);
        }
    }, "MessageRouter::route");
}

void MessageRouter::handleAuth(void* ws, const std::string& type, const std::string& data) {
    if (type == "login") {
        AuthHandler::handleLogin(ws, data);
    } else if (type == "register") {
        AuthHandler::handleRegister(ws, data);
    }
}

void MessageRouter::handleGps(void* ws, const std::string& data) {
    GpsHandler::handle(ws, data);
}

void MessageRouter::handleVideo(void* ws, const std::string& data) {
    VideoHandler::handle(ws, data);
}

void MessageRouter::handleControl(void* ws, const std::string& data) {
    ControlHandler::handle(ws, data);
}
""",

    # 认证处理
    "message/auth_handler/auth_handler.h": """#pragma once
#include <string>

class AuthHandler {
public:
    static void handleLogin(void* ws, const std::string& data);
    static void handleRegister(void* ws, const std::string& data);
};
""",

    "message/auth_handler/auth_handler.cpp": """#include "auth_handler.h"
#include "../../utils/logger/logger.h"
#include "../../utils/exception_handler/exception_handler.h"
#include "../../database/user_manager/user_manager.h"
#include "../../core/client_manager/client_manager.h"

void AuthHandler::handleLogin(void* ws, const std::string& data) {
    ExceptionHandler::safe_execute([&]() {
        Logger::info("处理登录请求");

        // TODO: 解析username和password
        // TODO: 调用UserManager验证
        // TODO: 生成token
        // TODO: 返回响应

    }, "AuthHandler::handleLogin");
}

void AuthHandler::handleRegister(void* ws, const std::string& data) {
    ExceptionHandler::safe_execute([&]() {
        Logger::info("处理注册请求");

        // TODO: 解析username和password
        // TODO: 调用UserManager创建用户
        // TODO: 返回响应

    }, "AuthHandler::handleRegister");
}
""",

    # GPS处理
    "message/gps_handler/gps_handler.h": """#pragma once
#include <string>

class GpsHandler {
public:
    static void handle(void* ws, const std::string& data);
};
""",

    "message/gps_handler/gps_handler.cpp": """#include "gps_handler.h"
#include "../../utils/logger/logger.h"
#include "../../utils/exception_handler/exception_handler.h"
#include "../../core/client_manager/client_manager.h"

void GpsHandler::handle(void* ws, const std::string& data) {
    ExceptionHandler::safe_execute([&]() {
        Logger::debug("处理GPS数据");

        // TODO: 解析GPS数据
        // TODO: 可选：存储到数据库
        // TODO: 转发给所有Android客户端
        ClientManager::instance().broadcastToAndroid(data);

    }, "GpsHandler::handle");
}
""",

    # 视频处理
    "message/video_handler/video_handler.h": """#pragma once
#include <string>

class VideoHandler {
public:
    static void handle(void* ws, const std::string& data);
};
""",

    "message/video_handler/video_handler.cpp": """#include "video_handler.h"
#include "../../utils/logger/logger.h"
#include "../../utils/exception_handler/exception_handler.h"
#include "../../core/client_manager/client_manager.h"

void VideoHandler::handle(void* ws, const std::string& data) {
    ExceptionHandler::safe_execute([&]() {
        Logger::debug("处理视频帧");

        // TODO: 可选：视频处理（压缩、滤镜等）
        // TODO: 转发给所有Android客户端
        ClientManager::instance().broadcastToAndroid(data);

    }, "VideoHandler::handle");
}
""",

    # 控制处理
    "message/control_handler/control_handler.h": """#pragma once
#include <string>

class ControlHandler {
public:
    static void handle(void* ws, const std::string& data);
};
""",

    "message/control_handler/control_handler.cpp": """#include "control_handler.h"
#include "../../utils/logger/logger.h"
#include "../../utils/exception_handler/exception_handler.h"
#include "../../core/client_manager/client_manager.h"

void ControlHandler::handle(void* ws, const std::string& data) {
    ExceptionHandler::safe_execute([&]() {
        Logger::info("处理控制指令");

        // TODO: 解析控制指令
        // TODO: 可选：指令验证、权限检查
        // TODO: 转发给硬件客户端
        ClientManager::instance().sendToHardware(data);

    }, "ControlHandler::handle");
}
""",

    # 用户管理
    "database/user_manager/user_manager.h": """#pragma once
#include <string>

class UserManager {
public:
    static UserManager& instance();

    bool registerUser(const std::string& username, const std::string& password);
    bool verifyUser(const std::string& username, const std::string& password, std::string& userId);

private:
    UserManager();
    void initDatabase();
    std::string hashPassword(const std::string& password);
};
""",

    "database/user_manager/user_manager.cpp": """#include "user_manager.h"
#include "../../utils/logger/logger.h"

UserManager& UserManager::instance() {
    static UserManager instance;
    return instance;
}

UserManager::UserManager() {
    initDatabase();
}

void UserManager::initDatabase() {
    Logger::info("初始化用户数据库");
    // TODO: 创建SQLite数据库和表
}

bool UserManager::registerUser(const std::string& username, const std::string& password) {
    Logger::info("注册用户: {}", username);
    // TODO: 插入用户到数据库
    return true;
}

bool UserManager::verifyUser(const std::string& username, const std::string& password, std::string& userId) {
    Logger::info("验证用户: {}", username);
    // TODO: 查询数据库验证
    return true;
}

std::string UserManager::hashPassword(const std::string& password) {
    // TODO: 实现密码哈希
    return password;
}
""",

    # Token管理
    "database/token_manager/token_manager.h": """#pragma once
#include <string>
#include <map>
#include <mutex>

class TokenManager {
public:
    static TokenManager& instance();

    std::string generateToken(const std::string& userId);
    bool verifyToken(const std::string& token, std::string& userId);
    void removeToken(const std::string& token);

private:
    TokenManager() = default;

    std::map<std::string, std::string> tokens_; // token -> userId
    std::mutex mutex_;
};
""",

    "database/token_manager/token_manager.cpp": """#include "token_manager.h"
#include "../../utils/logger/logger.h"
#include <random>
#include <sstream>
#include <iomanip>

TokenManager& TokenManager::instance() {
    static TokenManager instance;
    return instance;
}

std::string TokenManager::generateToken(const std::string& userId) {
    std::lock_guard<std::mutex> lock(mutex_);

    // TODO: 生成随机token
    std::string token = "token_" + userId;
    tokens_[token] = userId;

    Logger::info("生成Token: {} -> {}", token, userId);
    return token;
}

bool TokenManager::verifyToken(const std::string& token, std::string& userId) {
    std::lock_guard<std::mutex> lock(mutex_);

    auto it = tokens_.find(token);
    if (it != tokens_.end()) {
        userId = it->second;
        return true;
    }
    return false;
}

void TokenManager::removeToken(const std::string& token) {
    std::lock_guard<std::mutex> lock(mutex_);
    tokens_.erase(token);
    Logger::info("移除Token: {}", token);
}
""",

    # 日志系统
    "utils/logger/logger.h": """#pragma once
#include <string>
#include <fstream>
#include <mutex>

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL
};

class Logger {
public:
    static void init(const std::string& logDir);

    static void debug(const std::string& message);
    static void info(const std::string& message);
    static void warn(const std::string& message);
    static void error(const std::string& message);
    static void fatal(const std::string& message);

private:
    static void log(LogLevel level, const std::string& message);
    static std::string getCurrentTime();
    static std::string levelToString(LogLevel level);

    static std::string logDir_;
    static std::mutex mutex_;
};
""",

    "utils/logger/logger.cpp": """#include "logger.h"
#include <iostream>
#include <chrono>
#include <ctime>

std::string Logger::logDir_ = "data/logs/";
std::mutex Logger::mutex_;

void Logger::init(const std::string& logDir) {
    logDir_ = logDir;
    // TODO: 创建日志目录
}

void Logger::debug(const std::string& message) {
    log(LogLevel::DEBUG, message);
}

void Logger::info(const std::string& message) {
    log(LogLevel::INFO, message);
}

void Logger::warn(const std::string& message) {
    log(LogLevel::WARN, message);
}

void Logger::error(const std::string& message) {
    log(LogLevel::ERROR, message);
}

void Logger::fatal(const std::string& message) {
    log(LogLevel::FATAL, message);
}

void Logger::log(LogLevel level, const std::string& message) {
    std::lock_guard<std::mutex> lock(mutex_);

    std::string logMessage = "[" + getCurrentTime() + "] [" + levelToString(level) + "] " + message;

    // 输出到控制台
    std::cout << logMessage << std::endl;

    // TODO: 写入文件
}

std::string Logger::getCurrentTime() {
    auto now = std::chrono::system_clock::now();
    auto time = std::chrono::system_clock::to_time_t(now);
    char buffer[100];
    std::strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", std::localtime(&time));
    return buffer;
}

std::string Logger::levelToString(LogLevel level) {
    switch (level) {
        case LogLevel::DEBUG: return "DEBUG";
        case LogLevel::INFO:  return "INFO";
        case LogLevel::WARN:  return "WARN";
        case LogLevel::ERROR: return "ERROR";
        case LogLevel::FATAL: return "FATAL";
        default: return "UNKNOWN";
    }
}
""",

    # 异常处理
    "utils/exception_handler/exception_handler.h": """#pragma once
#include <functional>
#include <string>

class ExceptionHandler {
public:
    static void safe_execute(std::function<void()> func, const std::string& moduleName);
};
""",

    "utils/exception_handler/exception_handler.cpp": """#include "exception_handler.h"
#include "../logger/logger.h"

void ExceptionHandler::safe_execute(std::function<void()> func, const std::string& moduleName) {
    try {
        func();
    } catch (const std::exception& e) {
        Logger::error("[{}] 异常: {}", moduleName, e.what());
    } catch (...) {
        Logger::error("[{}] 未知异常", moduleName);
    }
}
""",

    # 通用工具
    "utils/common/common.h": """#pragma once
#include <string>
#include <vector>

class Common {
public:
    static std::string base64Encode(const std::vector<uint8_t>& data);
    static std::vector<uint8_t> base64Decode(const std::string& encoded);
};
""",

    "utils/common/common.cpp": """#include "common.h"

std::string Common::base64Encode(const std::vector<uint8_t>& data) {
    // TODO: 实现base64编码
    return "";
}

std::vector<uint8_t> Common::base64Decode(const std::string& encoded) {
    // TODO: 实现base64解码
    return {};
}
""",

    # CMakeLists.txt
    "CMakeLists.txt": """cmake_minimum_required(VERSION 3.15)
project(PetCollarServer)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 查找依赖
find_package(Threads REQUIRED)

# 包含目录
include_directories(
    ${PROJECT_SOURCE_DIR}
    ${PROJECT_SOURCE_DIR}/third_party
)

# 源文件
file(GLOB_RECURSE SOURCES
    "main.cpp"
    "core/**/*.cpp"
    "message/**/*.cpp"
    "database/**/*.cpp"
    "utils/**/*.cpp"
)

# 可执行文件
add_executable(pet_collar_server ${SOURCES})

# 链接库
target_link_libraries(pet_collar_server
    Threads::Threads
)

# 安装
install(TARGETS pet_collar_server DESTINATION bin)
""",

    # 配置文件
    "data/config.json": """{
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
""",

    # README
    "README.md": """# 宠物项圈服务器

## 编译
```bash
mkdir build
cd build
cmake ..
make
```

## 运行
```bash
./pet_collar_server
```

## 架构
- core/: 核心模块（WebSocket、客户端管理、健康检查）
- message/: 消息处理（路由、认证、GPS、视频、控制）
- database/: 数据库（用户管理、Token管理）
- utils/: 工具（日志、异常处理、通用工具）
- data/: 数据文件（数据库、配置、日志）
"""
}

def create_structure():
    print("开始创建服务器项目结构...")

    # 创建目录
    for d in dirs:
        path = os.path.join(BASE_PATH, d)
        os.makedirs(path, exist_ok=True)
    print("目录创建完成")

    # 创建文件
    for file_path, content in files.items():
        full_path = os.path.join(BASE_PATH, file_path)
        with open(full_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"创建: {file_path}")

    print("\n✅ 服务器项目结构创建完成！")
    print(f"项目路径: {BASE_PATH}")

if __name__ == "__main__":
    create_structure()
