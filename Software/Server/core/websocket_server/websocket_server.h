#pragma once
#include <string>
#include <functional>

class WebSocketServer {
public:
    WebSocketServer(int port);
    ~WebSocketServer();

    void start();
    void stop();

    void broadcast(const std::string& message);
    void sendTo(const std::string& clientId, const std::string& message);

private:
    void onConnection(void* ws);
    void onMessage(void* ws, const std::string& message);
    void onClose(void* ws);

    int port_;
    bool running_;
};
