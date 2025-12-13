
 #include "AuthWebSocketClient.h"

using namespace drogon;

// =============== 登陆 ===============
// 参数 ：req：请求对象，conn：连接对象
void AuthWebSocketClient::handleLogin(const HttpRequestPtr& req, const WebSocketConnectionPtr& conn) {
  // 获取请求参数
  auto username = req->getParameter("username");
  auto password = req->getParameter("password");

  // 验证请求参数
  if (username.empty() || password.empty()) {
    req->send(HttpResponse::newHttpResponse());
    return;
  }


}