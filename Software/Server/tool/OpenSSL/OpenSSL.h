#ifndef OpenSSL_h
#define OpenSSL_h

#include <openssl/ssl.h>
#include <openssl/aes.h>
#include <openssl/rsa.h>
#include <openssl/sha.h>
#include <openssl/rand.h>

class OpenSSL
{
    public:
        OpenSSL();
        ~OpenSSL();

        // ====================数据 加密和解密 （AES-128-GCM）====================
        // ----------------------- 加密 -----------------------
        // 参数 ：data：要加密的数据，key：加密密钥
        std::string encrypt(const std::string &data, const std::string &key);
        // ----------------------- 解密 -----------------------
        // 参数 ：data：要解密的数据，key：解密密钥
         std::string decrypt(const std::string &data, const std::string &key);
        // ----------------------- 生成RSA密钥对 -----------------------
        // 参数 ：无
        // 返回值 ：RSA密钥对
         std::string generateRSAKeyPair();
        // ----------------------- 生成AES密钥 -----------------------
        // 参数 ：无
        // 返回值 ：AES密钥
         std::string generateAESKey();
        // ----------------------- 生成随机数 -----------------------
        // 参数 ：无
        // 返回值 ：随机数
         std::string generateRandomNumber();
        // ====================用户 加密和解密 （哈希算法——待定）====================
        // ----------------------- 用户密码加密 -----------------------
        // 参数 ：data：要加密的数据，key：加密密钥
         std::string encryptPassword(const std::string &password);
        // ----------------------- 用户密码解密 -----------------------
        // 参数 ：data：要解密的数据，key：解密密钥
         std::string decryptPassword(const std::string &password);
};


#endif