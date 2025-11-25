#include "exception_handler.h"
#include "../logger/logger.h"

void ExceptionHandler::safe_execute(std::function<void()> func, const std::string& moduleName) {
    try {
        func();
    } catch (const std::exception& e) {
        Logger::error("[{}] 异常: {}", moduleName, e.what());
    } catch (...) {
        Logger::error("[{}] 未知异常", moduleName);
    }
}
