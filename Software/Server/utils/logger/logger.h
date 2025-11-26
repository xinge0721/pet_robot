#pragma once
#include <string>
#include <fstream>
#include <mutex>
#include <sstream>

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

    template<typename... Args>
    static void debug(const std::string& format, Args&&... args) {
        log(LogLevel::DEBUG, formatMessage(format, std::forward<Args>(args)...));
    }

    template<typename... Args>
    static void info(const std::string& format, Args&&... args) {
        log(LogLevel::INFO, formatMessage(format, std::forward<Args>(args)...));
    }

    template<typename... Args>
    static void warn(const std::string& format, Args&&... args) {
        log(LogLevel::WARN, formatMessage(format, std::forward<Args>(args)...));
    }

    template<typename... Args>
    static void error(const std::string& format, Args&&... args) {
        log(LogLevel::ERROR, formatMessage(format, std::forward<Args>(args)...));
    }

    template<typename... Args>
    static void fatal(const std::string& format, Args&&... args) {
        log(LogLevel::FATAL, formatMessage(format, std::forward<Args>(args)...));
    }

private:
    static void log(LogLevel level, const std::string& message);
    static std::string getCurrentTime();
    static std::string levelToString(LogLevel level);

    template<typename T>
    static void formatHelper(std::ostringstream& oss, const std::string& format, size_t& pos, T&& value) {
        size_t placeholder = format.find("{}", pos);
        if (placeholder != std::string::npos) {
            oss << format.substr(pos, placeholder - pos) << value;
            pos = placeholder + 2;
        }
    }

    template<typename T, typename... Args>
    static void formatHelper(std::ostringstream& oss, const std::string& format, size_t& pos, T&& value, Args&&... args) {
        formatHelper(oss, format, pos, std::forward<T>(value));
        formatHelper(oss, format, pos, std::forward<Args>(args)...);
    }

    template<typename... Args>
    static std::string formatMessage(const std::string& format, Args&&... args) {
        std::ostringstream oss;
        size_t pos = 0;
        formatHelper(oss, format, pos, std::forward<Args>(args)...);
        oss << format.substr(pos);
        return oss.str();
    }

    static std::string formatMessage(const std::string& format) {
        return format;
    }

    static std::string logDir_;
    static std::mutex mutex_;
};
