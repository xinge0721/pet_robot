#pragma once
#include <string>

class UserManager {
public:
    static UserManager& instance();

    bool registerUser(const std::string& username, const std::string& password);
    bool verifyUser(const std::string& username, const std::string& password, std::string& userId);

private:
    UserManager();
    void initDatabase();
    std::string hashPassword(const std::string& password);
};
