#ifndef CORE_APPLICATION_APPLICATION_H
#define CORE_APPLICATION_APPLICATION_H

// Application 模块头文件
// 负责应用程序的初始化和生命周期管理

namespace PetRobot {

class Application {
public:
    // 获取单例实例
    static Application& getInstance();

    // 初始化应用程序
    void init();

    // 禁止拷贝和赋值
    Application(const Application&) = delete;
    Application& operator=(const Application&) = delete;

private:
    // 私有构造函数（单例模式）
    Application();
    ~Application();

    // 是否已初始化
    bool initialized_ = false;
};

} // namespace PetRobot

#endif // CORE_APPLICATION_APPLICATION_H
