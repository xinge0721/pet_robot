#include "client_manager.h"
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
