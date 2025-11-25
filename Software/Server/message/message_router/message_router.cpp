#include "message_router.h"
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
