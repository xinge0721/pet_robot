#ifndef AuthWebSocketClient_h
#define AuthWebSocketClient_h

#include <drogon/WebSocketController.h>

// 登陆和注册的逻辑
class AuthWebSocketClient : public WebSocketController<AuthWebSocketClient> {
    
private:
  WebSocketConnectionPtr m_conn; // 当前连接的WebSocket连接
  // 密码加密工具
  OpenSSL m_openssl;

public:
    PATH_LIST_BEGIN
    PATH_ADD(handleLogin,"/auth/login", Get); // 将路径 “/test” 绑定到当前处理函数
    PATH_ADD(handleRegister,"/auth/register", Post); // 将路径 “/test” 绑定到当前处理函数
    PATH_ADD(handleLogout,"/auth/logout", Get); // 将路径 “/test” 绑定到当前处理函数
    PATH_LIST_END
    // =========== 登陆 =============
    void handleLogin(const HttpRequestPtr& req, const WebSocketConnectionPtr& conn) override;
    // =========== 注册 =============
    void handleRegister(const HttpRequestPtr& req, const WebSocketConnectionPtr& conn) override;
    // =========== 登出 =============
    void handleLogout(const WebSocketConnectionPtr& conn) override;
};


#endif