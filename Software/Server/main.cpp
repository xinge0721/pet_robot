#include <drogon/drogon.h>
#include "Core/Application/Application.h"

using namespace drogon;
using namespace PetRobot;

int main() {
    try {
        // 加载配置文件
        app().loadConfigFile("./config/config.json");

        // 初始化应用程序
        Application::getInstance().init();

        // 启动Drogon框架
        LOG_INFO << "Starting Pet Robot Server...";
        app().run();

    } catch (const std::exception& e) {
        LOG_ERROR << "Fatal error: " << e.what();
        return 1;
    }

    return 0;
}
