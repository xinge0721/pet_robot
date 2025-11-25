#pragma once
#include <string>

class ControlHandler {
public:
    static void handle(void* ws, const std::string& data);
};
