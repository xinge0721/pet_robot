#pragma once
#include <string>

class VideoHandler {
public:
    static void handle(void* ws, const std::string& data);
};
