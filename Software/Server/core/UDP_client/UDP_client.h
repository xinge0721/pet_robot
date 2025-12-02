#pragma once
#include <map>
#include <string>
#include <mutex>

class ClientManager {
public:
    static ClientManager& instance();

    void addAndroidClient(const std::string& userId, void* ws);
    void setHardwareClient(void* ws);
    void removeClient(void* ws);

    void broadcastToAndroid(const std::string& message);
    void sendToHardware(const std::string& message);

    bool isConnected(void* ws);

private:
    ClientManager() = default;

    std::map<std::string, void*> androidClients_;
    void* hardwareClient_ = nullptr;
    std::mutex mutex_;
};
