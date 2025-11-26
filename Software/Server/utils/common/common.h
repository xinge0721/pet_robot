#pragma once
#include <string>
#include <vector>
#include <cstdint>

class Common {
public:
    static std::string base64Encode(const std::vector<uint8_t>& data);
    static std::vector<uint8_t> base64Decode(const std::string& encoded);
};
