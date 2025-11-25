#include "video_handler.h"
#include "../../utils/logger/logger.h"
#include "../../utils/exception_handler/exception_handler.h"
#include "../../core/client_manager/client_manager.h"

void VideoHandler::handle(void* ws, const std::string& data) {
    ExceptionHandler::safe_execute([&]() {
        Logger::debug("处理视频帧");

        // TODO: 可选：视频处理（压缩、滤镜等）
        // TODO: 转发给所有Android客户端
        ClientManager::instance().broadcastToAndroid(data);

    }, "VideoHandler::handle");
}
