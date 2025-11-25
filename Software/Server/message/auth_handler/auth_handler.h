#pragma once
#include <string>

class AuthHandler {
public:
    static void handleLogin(void* ws, const std::string& data);
    static void handleRegister(void* ws, const std::string& data);
};
