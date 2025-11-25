#include "gps_handler.h"
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
