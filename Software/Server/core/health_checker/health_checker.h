#pragma once
#include <atomic>
#include <thread>

class HealthChecker {
public:
    HealthChecker();
    ~HealthChecker();

    void start();
    void stop();

private:
    void checkLoop();
    void checkClient(void* ws);

    std::atomic<bool> running_;
    std::thread thread_;
    int interval_ = 30; // 秒
};
