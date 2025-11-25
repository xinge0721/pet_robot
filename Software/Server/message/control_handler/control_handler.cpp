#include "control_handler.h"
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
