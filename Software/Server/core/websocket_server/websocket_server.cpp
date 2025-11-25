#include "websocket_server.h"
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
