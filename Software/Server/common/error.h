#ifndef Error_h
#define Error_h
// ======= 错误码枚举=========
// ------- 网络错误码--------
enum NetworkErrorCode
{
    NETWORK_SUCCESS = 1, // 网络成功
    NETWORK_ERROR = 2, // 网络错误
    NETWORK_INVALID_PARAM = 3, // 网络无效参数
    NETWORK_INVALID_REQUEST = 4, // 网络无效请求
    NETWORK_INVALID_RESPONSE = 5, // 网络无效响应
};
// ------- 业务错误码--------
enum ErrorCode
{
    SUCCESS = 6, // 成功
    ERROR = 7, // 错误
    INVALID_PARAM = 8, // 无效参数
    INVALID_REQUEST = 9, // 无效请求
    INVALID_RESPONSE = 10, // 无效响应
    INVALID_DATA = 11, // 无效数据
    INVALID_TOKEN = 12, // 无效令牌
    INVALID_USER = 13, // 无效用户
    INVALID_DEVICE = 14, // 无效设备
};
// ------- 系统错误码--------
enum SystemErrorCode
{
    SYSTEM_SUCCESS = 15, // 系统成功
    SYSTEM_ERROR = 16, // 系统错误
    SYSTEM_INVALID_PARAM = 18, // 系统无效参数
    SYSTEM_INVALID_REQUEST = 19, // 系统无效请求
    SYSTEM_INVALID_RESPONSE = 20, // 系统无效响应
    SYSTEM_INVALID_DATA = 21, // 系统无效数据
};
// ------- 数据库错误码--------
enum DatabaseErrorCode
{
    DATABASE_SUCCESS = 22, // 数据库成功
    DATABASE_ERROR = 23, // 数据库错误
    DATABASE_INVALID_PARAM = 25, // 数据库无效参数
    DATABASE_INVALID_REQUEST = 26, // 数据库无效请求
    DATABASE_INVALID_RESPONSE = 27, // 数据库无效响应
};
// ------- 缓存错误码--------
enum CacheErrorCode
{
    CACHE_SUCCESS = 28, // 缓存成功
    CACHE_ERROR = 29, // 缓存错误
    CACHE_INVALID_PARAM = 31, // 缓存无效参数
    CACHE_INVALID_REQUEST = 32, // 缓存无效请求
    CACHE_INVALID_RESPONSE = 33, // 缓存无效响应
};
// ------- 加密错误码--------
enum EncryptionErrorCode
{
    ENCRYPTION_SUCCESS = 34, // 加密成功
    ENCRYPTION_ERROR = 35, // 加密错误
    ENCRYPTION_INVALID_PARAM = 36, // 加密无效参数
    ENCRYPTION_INVALID_REQUEST = 37, // 加密无效请求
    ENCRYPTION_INVALID_RESPONSE = 38, // 加密无效响应
};
// ------- 解密错误码--------
enum DecryptionErrorCode
{
    DECRYPTION_SUCCESS = 41, // 解密成功
    DECRYPTION_ERROR = 42, // 解密错误
    DECRYPTION_INVALID_PARAM = 42, // 解密无效参数
    DECRYPTION_INVALID_REQUEST = 43, // 解密无效请求
    DECRYPTION_INVALID_RESPONSE = 44, // 解密无效响应
};

// ------- 数据类型错误码--------
enum DataTypeErrorCode
{
    DATA_TYPE_SUCCESS = 45, // 数据类型成功
    DATA_TYPE_ERROR = 46, // 数据类型错误
    DATA_TYPE_INVALID_PARAM = 47, // 数据类型无效参数
    DATA_TYPE_INVALID_REQUEST = 48, // 数据类型无效请求
    DATA_TYPE_INVALID_RESPONSE = 49, // 数据类型无效响应
};
#endifs