cmake_minimum_required(VERSION 3.15)
project(PetRobotServer CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 编译选项
if(MSVC)
    add_compile_options(/W4 /utf-8)
else()
    add_compile_options(-Wall -Wextra -pedantic)
endif()

# 查找Drogon框架（已包含SQLite3支持）
find_package(Drogon CONFIG REQUIRED)
find_package(jsoncpp REQUIRED)

# 包含目录
include_directories(${PROJECT_SOURCE_DIR})

# 递归收集所有源文件
file(GLOB_RECURSE SOURCES
    Core/**/*.cpp
    Controllers/**/*.cpp
    Filters/**/*.cpp
    Services/**/*.cpp
    Models/*.cc
    utils/**/*.cpp
)

# 可执行文件
add_executable(${PROJECT_NAME} main.cpp ${SOURCES})

# 链接Drogon（已包含SQLite3）
target_link_libraries(${PROJECT_NAME} PRIVATE Drogon::Drogon jsoncpp_lib)

# 测试
enable_testing()
add_subdirectory(test)

# 安装
install(TARGETS ${PROJECT_NAME} DESTINATION bin)
install(DIRECTORY config/ DESTINATION etc/pet_robot)
install(DIRECTORY views/ DESTINATION share/pet_robot/views)
install(DIRECTORY static/ DESTINATION share/pet_robot/static)
