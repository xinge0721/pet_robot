#pragma once
#include <string>
#include <map>
#include <mutex>

class TokenManager {
public:
    static TokenManager& instance();

    std::string generateToken(const std::string& userId);
    bool verifyToken(const std::string& token, std::string& userId);
    void removeToken(const std::string& token);

private:
    TokenManager() = default;

    std::map<std::string, std::string> tokens_; // token -> userId
    std::mutex mutex_;
};
