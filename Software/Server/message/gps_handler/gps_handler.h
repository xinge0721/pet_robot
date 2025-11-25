#pragma once
#include <string>

class GpsHandler {
public:
    static void handle(void* ws, const std::string& data);
};
