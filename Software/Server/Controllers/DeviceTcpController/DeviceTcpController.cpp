#include "DeviceTcpController.h"
#include "../../tool/OpenSSL/OpenSSL.h"
#include <hiredis/hiredis.h>  // Redis工具包

void DeviceTcpController::transmitVideoStream(const HttpRequestPtr &req,
    std::function<void(const HttpResponsePtr &)> &&callback)
{
    // 传输视频流
}

void DeviceTcpController::transmitGpsData(const HttpRequestPtr &req,
       std::function<void(const HttpResponsePtr &)> &&callback,//
       const std::string &id)
{
    // 第一步：解密GPS数据
    std::string data = openssl.decrypt(req->body(), id); //解密GPS数据

    // 第二步：根据设备ID 查询是否存在 ——验证设备合法性（先查缓存，再查数据库，当然查了数据库就在缓存中记录一份，等待下次查询时直接从缓存中获取）
    
        
    // 第三步 如果不存在则，记录违法信息，并中止返回错误数据

    // 第四步 如果存在则查询redis缓存 中用户是否在线

    // 第五步 如果在线则向用户发送GPS数据

    // 第六步 无论是否在线都记录该设备GPS数据到数据库

    // 第七步 返回处理成功结果
    auto resp = HttpResponse::newHttpJsonResponse({{"status", "ok"}});//返回处理成功结果
    callback(resp); 
} 
void DeviceTcpController::transmitHeartbeat(const HttpRequestPtr &req,/ // 传输心跳
      std::function<void(const HttpResponsePtr &)> &&callback)
{
    // 传输心跳
}