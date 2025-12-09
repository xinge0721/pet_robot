#ifndef logger_H  // 防止头文件被重复包含（预处理指令）
#define logger_H  // 定义宏LOG_H，标识头文件已包含

#include <spdlog/spdlog.h>  // 包含spdlog库的头文件，使用其日志功能


#include <iostream>
#include <fstream>

class logger
{
private:
    std::shared_ptr<spdlog::logger>  Logger;
public:
    logger(const std::string &path); // 配置文件路径

    // 析构函数：确保程序退出前所有日志都写入文件
    ~logger()
    {

    }

    void set_level(uint8_t RestrictionLevel)
    {
        this->Logger->set_level(spdlog::level::level_enum(RestrictionLevel));

    }


    // 日志输出接口（trace级别）
    // 模板参数Args...：支持可变参数（如日志中包含的变量）
    // 参数：fmt - 日志格式字符串（如"x = {}"）；args... - 格式字符串中的占位符对应的值
    template<typename... Args>
    void trace(const char* fmt, const Args&... args) {
        Logger->trace(fmt, args...);  // 调用spdlog的trace级别日志输出
    }

    // 日志输出接口（info级别），参数含义同上
    template<typename... Args>
    void info(const char* fmt, const Args&... args) {
        Logger->info(fmt, args...);   // 调用spdlog的info级别日志输出
    }

    // 日志输出接口（debug级别），参数含义同上
    template<typename... Args>
    void debug(const char* fmt, const Args&... args) {
        Logger->debug(fmt, args...);  // 调用spdlog的debug级别日志输出
    }

    // 日志输出接口（warn级别），参数含义同上
    template<typename... Args>
    void warn(const char* fmt, const Args&... args) {
        Logger->warn(fmt, args...);   // 调用spdlog的warn级别日志输出
    }

    // 日志输出接口（error级别），参数含义同上
    template<typename... Args>
    void error(const char* fmt, const Args&... args) {
        Logger->error(fmt, args...);  // 调用spdlog的error级别日志输出
    }

    // 日志输出接口（critical级别），参数含义同上
    template<typename... Args>
    void critical(const char* fmt, const Args&... args) {
        Logger->critical(fmt, args...);  // 调用spdlog的critical级别日志输出
    }

};

// =========== 全局logger工具 ===========
// 声明全局logger指针，使用extern关键字表示该变量在其他文件中定义
// 这样所有包含此头文件的源文件都可以访问同一个全局logger实例
extern logger* Logger;

// 全局logger初始化函数
// 参数：configPath - 日志配置文件的路径
// 功能：创建全局logger实例并进行初始化配置
// 注意：此函数应该在程序启动时（如main函数开始处）调用一次
void initLogger(const std::string &configPath);



#endif // LOG_H  // 结束头文件保护宏
