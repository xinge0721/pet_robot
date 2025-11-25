#include "auth_handler.h"
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
