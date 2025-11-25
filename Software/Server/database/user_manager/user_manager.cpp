#include "user_manager.h"
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
