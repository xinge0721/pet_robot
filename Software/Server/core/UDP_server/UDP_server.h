#pragma once
#include <winsock2.h>
#include <string>

class UDPServer {
public:
    UDPServer(int port);
    ~UDPServer();

    void start();  // 启动服务器
    void stop();   // 停止服务器

private:
    int port_;
    SOCKET socket_;
    bool running_;
};
