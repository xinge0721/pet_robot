#include "logger.h"  // 包含自定义日志头文件
#include <spdlog/sinks/stdout_color_sinks.h>  // 包含带颜色的控制台输出器头文件
#include <spdlog/sinks/daily_file_sink.h>       // 包含文件输出器头文件

#include <jsoncpp/json/json.h>
#include <fstream>



logger::logger(const std::string &path)
{
    // =========== 读取配置文件 ===========
    std::ifstream file(path);  // 尝试打开配置文件
    Json::Reader reader;       // JSON解析器对象
    Json::Value root;          // 存储解析后的JSON数据

    // 检查文件是否成功打开
    if (!file.is_open()) {
        // 文件打开失败（文件不存在或无权限）
        std::cerr << "警告：无法打开配置文件 " << path << "，使用默认配置" << std::endl;
        root = Json::Value();  // 创建空的JSON对象，后续会使用默认值
    }
    // 尝试解析JSON文件
    else if (!reader.parse(file, root)) {
        // JSON格式错误，解析失败
        std::cerr << "警告：配置文件格式错误，使用默认配置" << std::endl;
        std::cerr << "错误详情：" << reader.getFormattedErrorMessages() << std::endl;
        root = Json::Value();  // 创建空的JSON对象，后续会使用默认值
    }

    // 读取配置项（如果键不存在，使用括号中的默认值）
    // get()方法：第一个参数是键名，第二个参数是默认值
    std::string log_path = root.get("log_path", "./logs/app.log").asString();  // 默认日志路径
    int hour   = root.get("hour", 0).asInt();                    // 默认每天0点创建新文件
    int minute = root.get("minute", 0).asInt();                  // 默认0分
    int RestrictionLevel = root.get("RestrictionLevel", 0).asInt();  // 默认trace级别（输出所有日志）
    int Refresh = root.get("Refresh", 3).asInt();                    // 默认每3秒刷新一次

    // =========== 配置日志文件 ===========
    // 多输出目标（控制台 + 文件）：sinks是输出器的集合
    std::vector<spdlog::sink_ptr> sinks;

    // 添加带颜色的控制台输出器（_mt表示多线程安全，st表示单线程）
    // spdlog::sinks::stdout_color_sink_mt：spdlog提供的带颜色的控制台输出器类
    // std::make_shared：创建智能指针（自动管理内存）
    sinks.push_back(std::make_shared<spdlog::sinks::stdout_color_sink_mt>());
    sinks.push_back(std::make_shared<spdlog::sinks::daily_file_sink_mt>(
            log_path,                // 文件路径（会自动添加日期）（实际文件名会是：daily_2025-12-09.txt）
            hour,                   // 每天几点创建新文件（小时）
            minute                  // 每天几分创建新文件（分钟）
        )
    );

    // 创建日志器（logger）：管理输出器的核心对象
    // 参数1：日志器名称（"main_logger"）；参数2-3：输出器集合的起止迭代器
    auto logger = std::make_shared<spdlog::logger>("main_logger", sinks.begin(), sinks.end());

    // 设置日志格式
    // 格式说明：
    // [%Y-%m-%d %H:%M:%S.%e]：日期时间（年-月-日 时:分:秒.毫秒）
    // [%l]：日志级别（如info/error）
    // %v：日志内容（包括宏添加的文件名、行号和用户输入的内容）
    logger->set_pattern("[%Y-%m-%d %H:%M:%S] [%l] %v");

    // 设置全局日志级别：trace（最低级别，所有日志都输出）
    // 可选级别：trace < debug < info < warn < error < critical
    logger->set_level(spdlog::level::level_enum(RestrictionLevel));

    // 注册为默认日志器：后续spdlog::xxx()调用会使用该日志器
    spdlog::set_default_logger(logger);

    spdlog::flush_every(std::chrono::seconds(Refresh));// 定期刷新日志缓冲区

    file.close();

}


