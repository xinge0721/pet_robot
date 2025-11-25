#include <iostream>
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
