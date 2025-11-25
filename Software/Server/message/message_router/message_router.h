#pragma once
#include <string>

class MessageRouter {
public:
    static void route(void* ws, const std::string& message);

private:
    static void handleAuth(void* ws, const std::string& type, const std::string& data);
    static void handleGps(void* ws, const std::string& data);
    static void handleVideo(void* ws, const std::string& data);
    static void handleControl(void* ws, const std::string& data);
};
