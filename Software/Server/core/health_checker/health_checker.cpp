#include "health_checker.h"
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
