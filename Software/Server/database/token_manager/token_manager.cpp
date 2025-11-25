#include "token_manager.h"
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
