#include <drogon/HttpController.h>
using namespace drogon;

class DeviceTcpController : public drogon::HttpController<DeviceTcpController> {
private:
    OpenSSL openssl;
public:
    METHOD_LIST_BEGIN
    // 传输视频流
    METHOD_ADD(DeviceTcpController::transmitVideoStream, "/api/v1/transmitVideoStream", Get);
    // 传输GPS数据
    METHOD_ADD(DeviceTcpController::transmitGpsData, "/api/v1/transmitGpsData", Get);
    // 传输心跳
    METHOD_ADD(DeviceTcpController::transmitHeartbeat, "/api/v1/transmitHeartbeat", Get);
    METHOD_LIST_END

    void transmitVideoStream(const HttpRequestPtr &req,
                    std::function<void(const HttpResponsePtr &)> &&callback);
    
    void transmitGpsData(const HttpRequestPtr &req,
                        std::function<void(const HttpResponsePtr &)> &&callback,
                        const std::string &id); 
    
    void transmitHeartbeat(const HttpRequestPtr &req,
                    std::function<void(const HttpResponsePtr &)> &&callback);
};