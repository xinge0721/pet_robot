#pragma once
#include <string>
#include <fstream>
#include <mutex>

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL
};

class Logger {
public:
    static void init(const std::string& logDir);

    static void debug(const std::string& message);
    static void info(const std::string& message);
    static void warn(const std::string& message);
    static void error(const std::string& message);
    static void fatal(const std::string& message);

private:
    static void log(LogLevel level, const std::string& message);
    static std::string getCurrentTime();
    static std::string levelToString(LogLevel level);

    static std::string logDir_;
    static std::mutex mutex_;
};
