cmake_minimum_required(VERSION 3.15)
project(PetCollarServer)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 查找依赖
find_package(Threads REQUIRED)

# 包含目录
include_directories(
    ${PROJECT_SOURCE_DIR}
    ${PROJECT_SOURCE_DIR}/third_party
)

# 源文件
file(GLOB_RECURSE SOURCES
    "main.cpp"
    "core/**/*.cpp"
    "message/**/*.cpp"
    "database/**/*.cpp"
    "utils/**/*.cpp"
)

# 可执行文件
add_executable(pet_collar_server ${SOURCES})

# 链接库
target_link_libraries(pet_collar_server
    Threads::Threads
)

# 安装
install(TARGETS pet_collar_server DESTINATION bin)
