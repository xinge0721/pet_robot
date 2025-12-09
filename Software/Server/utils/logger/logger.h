#ifndef LOG_H  // 防止头文件被重复包含（预处理指令）
#define LOG_H  // 定义宏LOG_H，标识头文件已包含

#include <spdlog/spdlog.h>  // 包含spdlog库的头文件，使用其日志功能


#include <iostream>
#include <fstream>

class logger
{
private:

public:
    logger(const std::string &path); // 配置文件路径

    
};



#endif // LOG_H  // 结束头文件保护宏
