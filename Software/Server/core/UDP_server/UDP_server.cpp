#include "UDP_server.h"
#include <iostream>
#include <ws2tcpip.h>

#pragma comment(lib, "ws2_32.lib")

UDPServer::UDPServer(int port) : port_(port), socket_(INVALID_SOCKET), running_(false) {
    std::cout << "UDP Server initialized, port: " << port << std::endl;
}

UDPServer::~UDPServer() {
    stop();
}

void UDPServer::start() {
    // 步骤一：初始化Winsock库
    WSADATA wsaData;
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        std::cerr << "WSAStartup failed" << std::endl;
        return;
    }

    // 步骤二：创建UDP socket
    socket_ = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    if (socket_ == INVALID_SOCKET) {
        std::cerr << "Create socket failed" << std::endl;
        WSACleanup();
        return;
    }

    // 步骤三：绑定地址和端口
    sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(port_);
    serverAddr.sin_addr.s_addr = INADDR_ANY;

    if (bind(socket_, (sockaddr*)&serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
        std::cerr << "Bind failed" << std::endl;
        closesocket(socket_);
        WSACleanup();
        return;
    }

    std::cout << "UDP Server started successfully, listening on port: " << port_ << std::endl;
    running_ = true;

    // 步骤四：接收和发送数据循环
    char buffer[1024];
    sockaddr_in clientAddr;
    int clientAddrLen = sizeof(clientAddr);

    while (running_) {
        // 接收数据
        int recvLen = recvfrom(socket_, buffer, sizeof(buffer) - 1, 0,
                               (sockaddr*)&clientAddr, &clientAddrLen);

        if (recvLen > 0) {
            buffer[recvLen] = '\0';  // 添加空终止符

            // 打印接收到的数据
            char clientIP[INET_ADDRSTRLEN];
            inet_ntop(AF_INET, &clientAddr.sin_addr, clientIP, INET_ADDRSTRLEN);
            std::cout << "Received from " << clientIP << ":" << ntohs(clientAddr.sin_port)
                      << " message: " << buffer << std::endl;

            // 回显数据
            sendto(socket_, buffer, recvLen, 0,
                   (sockaddr*)&clientAddr, clientAddrLen);
            std::cout << "Reply sent" << std::endl;
        }
    }
}

void UDPServer::stop() {
    if (running_) {
        running_ = false;
        if (socket_ != INVALID_SOCKET) {
            closesocket(socket_);
            socket_ = INVALID_SOCKET;
        }
        WSACleanup();
        std::cout << "UDP Server stopped" << std::endl;
    }
}
