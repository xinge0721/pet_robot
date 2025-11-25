#pragma once
#include <functional>
#include <string>

class ExceptionHandler {
public:
    static void safe_execute(std::function<void()> func, const std::string& moduleName);
};
